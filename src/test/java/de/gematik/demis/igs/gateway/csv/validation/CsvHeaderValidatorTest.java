package de.gematik.demis.igs.gateway.csv.validation;

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

import static de.gematik.demis.igs.gateway.TestUtils.MOCKED_ERROR_MESSAGE;
import static de.gematik.demis.igs.gateway.TestUtils.MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_EMPTY_FILE;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_ILLEGAL_HEADER;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_MISSING_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.configuration.MessagesProperties;
import de.gematik.demis.igs.gateway.csv.InvalidInputDataException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class CsvHeaderValidatorTest {

  private MessageSourceWrapper messageSourceWrapper;
  private CsvHeaderValidator underTest;

  private static final String VALID_CSV_FILEPATH =
      "src/test/resources/testdata/igs-batch-fasta-testdata_minimal.csv";
  private static final String VALID_CSV_PATH_WITH_QUOTES =
      "src/test/resources/testdata/igs-batch-fastq-testdata_quotes_in_header.csv";

  @BeforeEach
  void init() {
    messageSourceWrapper = mock(MessageSourceWrapper.class);
    underTest = new CsvHeaderValidator(messageSourceWrapper);
  }

  @ParameterizedTest
  @ValueSource(strings = {VALID_CSV_FILEPATH, VALID_CSV_PATH_WITH_QUOTES})
  void shouldFindNoErrors(String validCsvPath) {
    String csvData = readFile(validCsvPath);
    assertDoesNotThrow(() -> underTest.validate(csvData));
  }

  @NullSource
  @EmptySource
  @ParameterizedTest
  void shouldThrowExceptionOnBlankInput(String input) {
    when(messageSourceWrapper.getMessage(ERROR_EMPTY_FILE)).thenReturn(MOCKED_ERROR_MESSAGE);
    InvalidInputDataException exception =
        assertThrows(InvalidInputDataException.class, () -> underTest.validate(input));
    verify(messageSourceWrapper).getMessage(ERROR_EMPTY_FILE);
    ValidationError error = exception.getErrors().getFirst();
    assertEquals(MOCKED_ERROR_MESSAGE, error.getMsg());
    assertEquals(ValidationError.ErrorCode.EMPTY_FILE, error.getErrorCode());
  }

  @Test
  void shouldAddInvalidHeaderToReport() {
    final String invalidHeaderCsv =
        "src/test/resources/testdata/igs-batch-fasta-illegal-header.csv";
    final String csvData = readFile(invalidHeaderCsv);
    final String illegalHeaderMsg =
        MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER.formatted("ILLEGAL_HEADER");
    final String singleQuoteIllegalHeaderMsg =
        MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER.formatted("''");

    when(messageSourceWrapper.getMessage(ERROR_ILLEGAL_HEADER, "ILLEGAL_HEADER"))
        .thenReturn(illegalHeaderMsg);
    when(messageSourceWrapper.getMessage(ERROR_ILLEGAL_HEADER, "''"))
        .thenReturn(singleQuoteIllegalHeaderMsg);

    List<ValidationError> validationErrors = underTest.validate(csvData);

    verify(messageSourceWrapper, times(2)).getMessage(any(MessagesProperties.class), anyString());
    assertEquals(2, validationErrors.size());
    assertThat(validationErrors.stream().map(ValidationError::getMsg))
        .containsExactlyInAnyOrder(illegalHeaderMsg, singleQuoteIllegalHeaderMsg);
    assertThat(validationErrors.stream().map(ValidationError::getErrorCode))
        .containsOnly(ValidationError.ErrorCode.HEADER);
  }

  @Test
  void shouldAddMissingAdapterHeaderToReport() {
    final String missingAdapterHeaderCsv =
        "src/test/resources/testdata/igs-batch-fasta-testdata-missing-adapter-header.csv";
    String csvData = readFile(missingAdapterHeaderCsv);
    when(messageSourceWrapper.getMessage(ERROR_MISSING_HEADER, "ADAPTER"))
        .thenReturn(MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER.formatted("ADAPTER"));

    List<ValidationError> validationErrors = underTest.validate(csvData);

    assertEquals(1, validationErrors.size());
    ValidationError error = validationErrors.getFirst();
    assertEquals(MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER.formatted("ADAPTER"), error.getMsg());
    assertEquals(ValidationError.ErrorCode.HEADER, error.getErrorCode());
  }

  @SneakyThrows
  private String readFile(String path) {
    return Files.readString(Paths.get(path));
  }
}
