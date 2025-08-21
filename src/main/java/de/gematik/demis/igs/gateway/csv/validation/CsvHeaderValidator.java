package de.gematik.demis.igs.gateway.csv.validation;

/*-
 * #%L
 * IGS-Gateway
 * %%
 * Copyright (C) 2025 gematik GmbH
 * %%
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the
 * European Commission – subsequent versions of the EUPL (the "Licence").
 * You may not use this work except in compliance with the Licence.
 *
 * You find a copy of the Licence in the "Licence" file or at
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.
 * In case of changes by gematik find details in the "Readme" file.
 *
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_EMPTY_FILE;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_ILLEGAL_HEADER;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_MISSING_HEADER;

import com.opencsv.bean.CsvBindByName;
import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.configuration.MessagesProperties;
import de.gematik.demis.igs.gateway.csv.InvalidInputDataException;
import de.gematik.demis.igs.gateway.csv.model.OverviewDataCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CsvHeaderValidator {

  private static final String SPLIT_REGEX = "\n";
  private static final String SEPARATOR = ";";
  private static final int HEADER_ROW = 0;
  private static final List<String> headers = headers();

  private final MessageSourceWrapper messageSourceWrapper;

  /**
   * Retrieves a list of header names from the fields of the `OverviewDataCsv` class that are
   * annotated with `CsvBindByName`, which are used for validation.
   *
   * @return a list of header names
   */
  private static List<String> headers() {
    final Field[] fields = OverviewDataCsv.class.getDeclaredFields();
    return Arrays.stream(fields)
        .map(
            field -> {
              if (field.isAnnotationPresent(CsvBindByName.class)) {
                CsvBindByName annotation = field.getAnnotation(CsvBindByName.class);
                return annotation.column();
              }
              return null;
            })
        .filter(Objects::nonNull)
        .toList();
  }

  public List<ValidationError> validate(String csvData) {
    handleBlankCsv(csvData);
    return validateHeaders(csvData);
  }

  private void handleBlankCsv(String csvData) {
    if (StringUtils.isBlank(csvData)) {
      throw new InvalidInputDataException(
          List.of(
              ValidationError.builder()
                  .rowNumber(HEADER_ROW)
                  .msg(messageSourceWrapper.getMessage(ERROR_EMPTY_FILE))
                  .errorCode(ErrorCode.EMPTY_FILE)
                  .build()));
    }
  }

  private List<ValidationError> validateHeaders(String csvData) {
    final List<String> extractHeaders = extractHeaders(csvData);
    final List<ValidationError> invalidHeaders = checkForUnknownHeaders(extractHeaders);
    final List<ValidationError> missingHeaders = checkForMissingHeaders(extractHeaders);
    return Stream.concat(invalidHeaders.stream(), missingHeaders.stream()).toList();
  }

  private List<String> extractHeaders(String csvData) {
    return Arrays.stream(csvData.split(SPLIT_REGEX)[HEADER_ROW].split(SEPARATOR))
        .map(String::trim)
        .map(header -> StringUtils.strip(header, "\""))
        .filter(StringUtils::isNoneBlank)
        .toList();
  }

  private List<ValidationError> checkForUnknownHeaders(List<String> extractedHeaders) {
    return extractedHeaders.stream()
        .filter(header -> !headers.contains(header))
        .map(header -> createValidationError(header, ERROR_ILLEGAL_HEADER))
        .toList();
  }

  private List<ValidationError> checkForMissingHeaders(List<String> extractedHeaders) {
    return headers.stream()
        .filter(header -> !extractedHeaders.contains(header))
        .map(header -> createValidationError(header, ERROR_MISSING_HEADER))
        .toList();
  }

  private ValidationError createValidationError(
      String header, MessagesProperties messagesProperties) {
    return ValidationError.builder()
        .msg(messageSourceWrapper.getMessage(messagesProperties, header))
        .rowNumber(HEADER_ROW)
        .errorCode(ErrorCode.HEADER)
        .columnName(header)
        .build();
  }
}
