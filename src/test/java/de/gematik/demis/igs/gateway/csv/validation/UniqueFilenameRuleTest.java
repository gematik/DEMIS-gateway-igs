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

import static de.gematik.demis.igs.gateway.TestUtils.MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.configuration.MessagesProperties;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.rules.UniqueFilenameRule;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UniqueFilenameRuleTest extends AbstractValidatorRuleTest {

  public static final String SAMPLE_FILE_NAME = "filename";
  private static final MessageSourceWrapper MESSAGE_SOURCE_WRAPPER =
      mock(MessageSourceWrapper.class);
  private final UniqueFilenameRule rule = new UniqueFilenameRule(MESSAGE_SOURCE_WRAPPER);

  @BeforeEach
  void setUp() {
    String formattedMessage = MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER.formatted(SAMPLE_FILE_NAME);
    when(MESSAGE_SOURCE_WRAPPER.getMessage(
            MessagesProperties.ERROR_DUPLICATE_FILE_NAME, SAMPLE_FILE_NAME))
        .thenReturn(formattedMessage);
  }

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
    mockMessageWithRowNumber(secondRow);
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

    mockMessageWithRowNumber(secondRow);
    mockMessageWithRowNumber(thirdRow);

    List<ValidationError> errors =
        rule.applyOnValues(List.of(firstRowBuilder.build(), secondRow, thirdRow));
    assertThat(errors).hasSize(1);
    applyOnValuesValidationError(errors.getFirst(), thirdRow.getRowNumber());
  }

  @Test
  void shouldWriteMultipleErrorWithIdenticalFilenamesInDifferentRows() {
    UniqueFilenameRule rule = new UniqueFilenameRule(MESSAGE_SOURCE_WRAPPER);
    IgsOverviewCsv firstRow = firstRowBuilder.build();
    IgsOverviewCsv secondRow = secondRowBuilder.build();
    IgsOverviewCsv thirdRow = thirdRowBuilder.build();
    firstRow.setFileOneName(SAMPLE_FILE_NAME);
    secondRow.setFileOneName(SAMPLE_FILE_NAME);
    thirdRow.setFileTwoName(SAMPLE_FILE_NAME);

    mockMessageWithRowNumber(firstRow);
    mockMessageWithRowNumber(secondRow);
    mockMessageWithRowNumber(thirdRow);

    List<ValidationError> errors = rule.applyOnValues(List.of(firstRow, secondRow, thirdRow));
    assertThat(errors).hasSize(2);
    applyOnValuesValidationError(errors.getFirst(), secondRow.getRowNumber());
    applyOnValuesValidationError(errors.getLast(), thirdRow.getRowNumber());
  }

  private void applyOnValuesValidationError(ValidationError error, long rowNumber) {
    final String CONCAT_ERROR =
        MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER + MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER;
    assertThat(error.getMsg()).isEqualTo(CONCAT_ERROR.formatted(rowNumber, SAMPLE_FILE_NAME));
    assertThat(error.getRowNumber()).isEqualTo(rowNumber);
    assertThat(error.getErrorCode()).isEqualTo(ValidationError.ErrorCode.UNIQUE_FILENAME);
  }

  private void mockMessageWithRowNumber(IgsOverviewCsv row) {
    String rowNumber = String.valueOf(row.getRowNumber());
    when(MESSAGE_SOURCE_WRAPPER.getMessage(ERROR_PREFIX, rowNumber))
        .thenReturn(MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER.formatted(rowNumber));
  }
}
