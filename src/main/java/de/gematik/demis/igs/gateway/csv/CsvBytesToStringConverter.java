package de.gematik.demis.igs.gateway.csv;

/*-
 * #%L
 * IGS-Gateway
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_EMPTY_FILE;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_INVALID_FILE_FORMAT;

import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Converts the bytes of a CSV file to a string representation. */
@Component
@Slf4j
@RequiredArgsConstructor
public class CsvBytesToStringConverter {

  private static final int HEADER_ROW = 0;
  private static final int NUMBER_OF_FIRST_BYTES = 900;
  private static final String CSV_HEADER_REGULAR_EXPRESSION = "^[0-9A-Z_;,.\"]*$";

  private final Pattern csvHeaderPattern = Pattern.compile(CSV_HEADER_REGULAR_EXPRESSION);

  private final MessageSourceWrapper messageSourceWrapper;

  /**
   * Converts the given byte array to a string in UTF-8 encoding.
   *
   * @param bytes The byte array to convert.
   * @return The string representation of the byte array. throws InvalidFileFormatException if the
   *     byte array is empty or not a valid CSV file.
   * @throws InvalidInputDataException If the bytes do not represent a CSV file.
   */
  public String convert(final byte[] bytes) throws InvalidInputDataException {
    if (bytes == null || bytes.length == 0) {
      throw new InvalidInputDataException(
          List.of(
              ValidationError.builder()
                  .rowNumber(HEADER_ROW)
                  .msg(messageSourceWrapper.getMessage(ERROR_EMPTY_FILE))
                  .errorCode(ErrorCode.EMPTY_FILE)
                  .build()));
    }

    String firstBytes = readFirstBytes(bytes);
    if (!csvHeaderPattern.matcher(firstBytes).matches()) {
      throw new InvalidInputDataException(
          List.of(
              ValidationError.builder()
                  .rowNumber(HEADER_ROW)
                  .msg(messageSourceWrapper.getMessage(ERROR_INVALID_FILE_FORMAT))
                  .errorCode(ErrorCode.INVALID_FILE_FORMAT)
                  .build()));
    }

    return new String(bytes, StandardCharsets.UTF_8);
  }

  private String readFirstBytes(final byte[] bytes) {
    String stringRead = "";
    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
      byte[] buffer = new byte[NUMBER_OF_FIRST_BYTES];
      int bytesRead = byteArrayInputStream.read(buffer, 0, NUMBER_OF_FIRST_BYTES);
      if (bytesRead > 0) {
        stringRead = new String(buffer, StandardCharsets.UTF_8);
      }
    } catch (IOException exception) {
      log.error("An error occurred while reading the file: " + exception);
    }
    return stringRead;
  }
}
