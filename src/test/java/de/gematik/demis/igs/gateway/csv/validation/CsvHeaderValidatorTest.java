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
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.gematik.demis.igs.gateway.csv.InvalidInputDataException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

class CsvHeaderValidatorTest {

  final CsvHeaderValidator csvHeaderValidator = new CsvHeaderValidator();

  @SneakyThrows
  private String readFile(String path) {
    return Files.readString(Paths.get(path));
  }

  @Test
  void shouldFindNoErrors() {
    final String validCsv = "src/test/resources/testdata/igs-batch-fasta-testdata_minimal.csv";
    String csvData = readFile(validCsv);
    assertDoesNotThrow(() -> csvHeaderValidator.validate(csvData));
  }

  @NullSource
  @EmptySource
  @ParameterizedTest
  void shouldThrowExceptionOnBlankInput(String input) {
    InvalidInputDataException exception =
        assertThrows(InvalidInputDataException.class, () -> csvHeaderValidator.validate(input));

    ValidationError error = exception.getErrors().getFirst();
    assertEquals(ValidationError.ErrorMessage.EMPTY_FILE.msg(), error.getMsg());
    assertEquals(ValidationError.ErrorCode.EMPTY_FILE, error.getErrorCode());
  }

  @Test
  void shouldAddInvalidHeaderToReport() {
    final String invalidHeaderCsv =
        "src/test/resources/testdata/igs-batch-fasta-illegal-header.csv";
    final String csvData = readFile(invalidHeaderCsv);
    final String illegalHeaderMsg = "Die Kopfzeile ILLEGAL_HEADER ist nicht erlaubt.";
    final String singleQuoteIllegalHeaderMsg = "Die Kopfzeile '' ist nicht erlaubt.";
    final String doubleQuoteIllegalHeaderMsg = "Die Kopfzeile \"\" ist nicht erlaubt.";

    List<ValidationError> validationErrors = csvHeaderValidator.validate(csvData);

    assertEquals(3, validationErrors.size());
    assertThat(validationErrors.stream().map(ValidationError::getMsg))
        .containsExactlyInAnyOrder(
            illegalHeaderMsg, singleQuoteIllegalHeaderMsg, doubleQuoteIllegalHeaderMsg);
    assertThat(validationErrors.stream().map(ValidationError::getErrorCode))
        .containsOnly(ValidationError.ErrorCode.HEADER);
  }

  @Test
  void shouldAddMissingAdapterHeaderToReport() {
    final String missingAdapterHeaderCsv =
        "src/test/resources/testdata/igs-batch-fasta-testdata-missing-adapter-header.csv";
    String csvData = readFile(missingAdapterHeaderCsv);
    List<ValidationError> validationErrors = csvHeaderValidator.validate(csvData);

    assertEquals(1, validationErrors.size());

    ValidationError error = validationErrors.getFirst();
    assertEquals("Die Kopfzeile ADAPTER fehlt.", error.getMsg());
    assertEquals(ValidationError.ErrorCode.HEADER, error.getErrorCode());
  }
}
