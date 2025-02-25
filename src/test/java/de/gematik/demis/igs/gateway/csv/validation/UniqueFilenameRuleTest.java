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

import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.rules.UniqueFilenameRule;
import java.util.List;
import org.junit.jupiter.api.Test;

class UniqueFilenameRuleTest extends AbstractValidatorRuleTest {

  public static final String SAMPLE_FILE_NAME = "filename";
  private final UniqueFilenameRule rule = new UniqueFilenameRule();

  @Test
  void shouldFindNoErrors() {
    List<ValidationError> errors =
        rule.applyOnValues(
            List.of(firstRowBuilder.build(), secondRowBuilder.build(), thirdRowBuilder.build()));
    assertThat(errors).isEmpty();
  }

  @Test
  void shouldWriteErrorWithIdenticalFilenamesInOneRow() {
    IgsOverviewCsv secondRow = secondRowBuilder.build();
    secondRow.setFileOneName(SAMPLE_FILE_NAME);
    secondRow.setFileTwoName(SAMPLE_FILE_NAME);
    List<ValidationError> errors =
        rule.applyOnValues(List.of(firstRowBuilder.build(), secondRow, thirdRowBuilder.build()));
    assertThat(errors).hasSize(1);
    applyOnValuesValidationError(errors.getFirst(), secondRow.getRowNumber());
  }

  @Test
  void shouldWriteErrorWithIdenticalFilenamesInDifferentRows() {
    IgsOverviewCsv secondRow = secondRowBuilder.build();
    IgsOverviewCsv thirdRow = thirdRowBuilder.build();
    secondRow.setFileOneName(SAMPLE_FILE_NAME);
    thirdRow.setFileTwoName(SAMPLE_FILE_NAME);
    List<ValidationError> errors =
        rule.applyOnValues(List.of(firstRowBuilder.build(), secondRow, thirdRow));
    assertThat(errors).hasSize(1);
    applyOnValuesValidationError(errors.getFirst(), thirdRow.getRowNumber());
  }

  @Test
  void shouldWriteMultipleErrorWithIdenticalFilenamesInDifferentRows() {
    UniqueFilenameRule rule = new UniqueFilenameRule();
    IgsOverviewCsv firstRow = firstRowBuilder.build();
    IgsOverviewCsv secondRow = secondRowBuilder.build();
    IgsOverviewCsv thirdRow = thirdRowBuilder.build();
    firstRow.setFileOneName(SAMPLE_FILE_NAME);
    secondRow.setFileOneName(SAMPLE_FILE_NAME);
    thirdRow.setFileTwoName(SAMPLE_FILE_NAME);
    List<ValidationError> errors = rule.applyOnValues(List.of(firstRow, secondRow, thirdRow));
    assertThat(errors).hasSize(2);
    applyOnValuesValidationError(errors.getFirst(), secondRow.getRowNumber());
    applyOnValuesValidationError(errors.getLast(), thirdRow.getRowNumber());
  }

  private void applyOnValuesValidationError(ValidationError error, long rowNumber) {
    final String CONCAT_ERROR =
        ValidationError.ErrorMessage.PREFIX.msg()
            + ValidationError.ErrorMessage.DUPLICATE_FILE_NAME.msg();
    assertThat(error.getMsg()).isEqualTo(CONCAT_ERROR.formatted(rowNumber, SAMPLE_FILE_NAME));
    assertThat(error.getRowNumber()).isEqualTo(rowNumber);
    assertThat(error.getErrorCode()).isEqualTo(ValidationError.ErrorCode.UNIQUE_FILENAME);
  }
}
