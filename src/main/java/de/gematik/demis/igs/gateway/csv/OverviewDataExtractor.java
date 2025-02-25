package de.gematik.demis.igs.gateway.csv;

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
 * #L%
 */

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import de.gematik.demis.igs.gateway.IgsGatewayRuntimeException;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.model.OverviewDataCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Component;

/** Extracts the data which is required for the upload overview. */
@Slf4j
@Component
class OverviewDataExtractor {

  private static final char SEMICOLON_SEPARATOR = ';';
  public static final String ERROR_MESSAGE_INCONSISTENT_ROW_LENGTH =
      "Zeile %d: Diese Zeile hat nicht die gleiche Anzahl an Einträgen wie es Header gibt";

  /**
   * Extracts the data which is required for the upload overview.
   *
   * @param csvData The csv data from which the overview data has to be extracted.
   * @return The data which is required for the upload overview.
   * @throws CsvParseException When the CSV file could not be parsed.
   */
  List<IgsOverviewCsv> extractOverviewData(String csvData) {
    List<OverviewDataCsv> parsedData = parseCsvData(csvData);
    return mapToOverviewData(parsedData);
  }

  private List<OverviewDataCsv> parseCsvData(String csvData) {
    CsvToBean<OverviewDataCsv> parser = initializeParser(csvData);
    List<OverviewDataCsv> parsedData = parser.parse();
    handleParsingExceptions(parser.getCapturedExceptions());
    return parsedData;
  }

  private CsvToBean<OverviewDataCsv> initializeParser(String csvData) {
    Reader reader = new StringReader(csvData);
    return new CsvToBeanBuilder<OverviewDataCsv>(reader)
        .withSeparator(SEMICOLON_SEPARATOR)
        .withType(OverviewDataCsv.class)
        .withErrorLocale(Locale.GERMAN)
        .withThrowExceptions(false)
        .build();
  }

  private void handleParsingExceptions(List<CsvException> csvExceptions) {
    if (!csvExceptions.isEmpty()) {
      List<ValidationError> errors = csvExceptions.stream().map(this::exceptionToError).toList();
      throw new InvalidInputDataException(errors);
    }
  }

  private ValidationError exceptionToError(CsvException e) {
    return ValidationError.builder()
        .msg(ERROR_MESSAGE_INCONSISTENT_ROW_LENGTH.formatted(e.getLineNumber()))
        .rowNumber(e.getLineNumber())
        .errorCode(ValidationError.ErrorCode.INCONSISTENT_ROW_LENGTH)
        .build();
  }

  private List<IgsOverviewCsv> mapToOverviewData(List<OverviewDataCsv> overviewDataCsvs) {
    List<IgsOverviewCsv> overviewData = new ArrayList<>();
    AtomicLong index = new AtomicLong(1);
    if (overviewDataCsvs != null && !overviewDataCsvs.isEmpty()) {
      overviewData =
          overviewDataCsvs.stream()
              .map(data -> copyProperties(data, index.getAndIncrement()))
              .toList();
    }
    return overviewData;
  }

  private IgsOverviewCsv copyProperties(OverviewDataCsv overviewDataCsv, long rowNumber) {
    IgsOverviewCsv overviewData = new IgsOverviewCsv();
    overviewData.setRowNumber(rowNumber);
    try {
      BeanUtils.copyProperties(overviewData, overviewDataCsv);
    } catch (InvocationTargetException | IllegalAccessException exception) {
      log.error("Error while copying beans: ", exception);
      throw new IgsGatewayRuntimeException(exception.getMessage(), exception);
    }
    return overviewData;
  }
}
