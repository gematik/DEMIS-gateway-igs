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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.igs.gateway.TestUtils.MOCKED_ERROR_MESSAGE;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_EMPTY_FILE;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_INVALID_FILE_FORMAT;
import static de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode.EMPTY_FILE;
import static de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode.INVALID_FILE_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class CsvBytesToStringConverterTest {

  private static final String PATH_TO_VALID_CSV =
      "src/test/resources/testdata/igs-batch-fastq-testdata.csv";
  private static final String PATH_TO_VALID_CSV_WITH_HEADERS =
      "src/test/resources/testdata/igs-batch-fastq-testdata_quotes_in_header.csv";
  private static final String PATH_TO_INVALID_CSV =
      "src/test/resources/testdata/igs-batch-fasta-illegal-header.csv";

  private static final MessageSourceWrapper MESSAGE_SOURCE_WRAPPER =
      mock(MessageSourceWrapper.class);
  private final CsvBytesToStringConverter converter =
      new CsvBytesToStringConverter(MESSAGE_SOURCE_WRAPPER);

  @SneakyThrows
  @ParameterizedTest
  @ValueSource(strings = {PATH_TO_VALID_CSV, PATH_TO_VALID_CSV_WITH_HEADERS})
  void shouldConvertSuccessfully(String csvPath) {
    byte[] validCsv = Files.readAllBytes(Paths.get(csvPath));

    String actualConverted = converter.convert(validCsv);

    String expectedConverted = new String(validCsv, StandardCharsets.UTF_8);
    assertThat(actualConverted).isNotEmpty().isEqualTo(expectedConverted);
  }

  @NullSource
  @EmptySource
  @ParameterizedTest
  void shouldThrowExceptionOnBlankInput(byte[] input) {
    when(MESSAGE_SOURCE_WRAPPER.getMessage(ERROR_EMPTY_FILE)).thenReturn(MOCKED_ERROR_MESSAGE);

    InvalidInputDataException exception =
        assertThrows(InvalidInputDataException.class, () -> converter.convert(input));
    ValidationError error = exception.getErrors().getFirst();
    assertThat(error.getMsg()).isEqualTo(MOCKED_ERROR_MESSAGE);
    assertThat(error.getErrorCode()).isEqualTo(EMPTY_FILE);
  }

  @Test
  @SneakyThrows
  void shouldThrowExceptionOnInvalidInput() {
    when(MESSAGE_SOURCE_WRAPPER.getMessage(ERROR_INVALID_FILE_FORMAT))
        .thenReturn(MOCKED_ERROR_MESSAGE);
    byte[] invalidCsv = Files.readAllBytes(Paths.get(PATH_TO_INVALID_CSV));

    InvalidInputDataException exception =
        assertThrows(InvalidInputDataException.class, () -> converter.convert(invalidCsv));

    ValidationError error = exception.getErrors().getFirst();
    assertThat(error.getMsg()).isEqualTo(MOCKED_ERROR_MESSAGE);
    assertThat(error.getErrorCode()).isEqualTo(INVALID_FILE_FORMAT);
  }
}
