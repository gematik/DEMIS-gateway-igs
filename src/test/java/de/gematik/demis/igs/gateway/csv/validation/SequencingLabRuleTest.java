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
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_SEQUENCING_LAB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import de.gematik.demis.igs.gateway.csv.validation.rules.SequencingLabRule;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SequencingLabRuleTest extends AbstractValidatorRuleTest {

  private final String CONCAT_ERROR =
      MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER + MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER;
  private MessageSourceWrapper messageSourceWrapper;
  private SequencingLabRule rule;

  @BeforeEach
  void init() {
    messageSourceWrapper = mock(MessageSourceWrapper.class);
    rule = new SequencingLabRule(messageSourceWrapper);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "asdfg", "1234", "123456", "1234a", "a2345", "12c45",
      })
  void shouldAddErrorIfSequencingLabDemisLabIdNotFiveDigits(String invalidDemisLabId) {
    IgsOverviewCsv row = firstRowBuilder.build();
    row.setSequencingLabDemisLabId(invalidDemisLabId);

    mockMessageWithFalseId(invalidDemisLabId);
    mockMessageWithRowNumber(row);

    List<ValidationError> errors = new ArrayList<>(rule.applyOnValue(row));

    verify(messageSourceWrapper).getMessage(ERROR_SEQUENCING_LAB, invalidDemisLabId);
    assertThat(errors).hasSize(1);
    assertThat(errors.getFirst().getMsg())
        .isEqualTo(CONCAT_ERROR.formatted(row.getRowNumber(), invalidDemisLabId));
    assertThat(errors.getFirst().getErrorCode()).isEqualTo(ErrorCode.SEQUENCING_LAB);
    assertThat(errors.getFirst().getRowNumber()).isEqualTo(row.getRowNumber());
  }

  @Test
  void shouldAddTwoErrorIfSequencingLabDemisLabInMultipleLinesAreIncorrect() {
    IgsOverviewCsv row = firstRowBuilder.build();
    IgsOverviewCsv row2 = secondRowBuilder.build();
    IgsOverviewCsv row3 = thirdRowBuilder.build();

    String falseIdOne = "1234";
    String falseIdTwo = "1234e";

    row.setSequencingLabDemisLabId(falseIdOne);
    row3.setSequencingLabDemisLabId(falseIdTwo);

    mockMessageWithRowNumber(row);
    mockMessageWithRowNumber(row3);

    mockMessageWithFalseId(falseIdOne);
    mockMessageWithFalseId(falseIdTwo);

    List<ValidationError> errors = new ArrayList<>(rule.applyOnValue(row));
    errors.addAll(rule.applyOnValue(row2));
    errors.addAll(rule.applyOnValue(row3));

    verify(messageSourceWrapper).getMessage(ERROR_SEQUENCING_LAB, falseIdOne);
    verify(messageSourceWrapper).getMessage(ERROR_SEQUENCING_LAB, falseIdTwo);
    assertThat(errors).hasSize(2);
    assertThat(errors.getFirst().getMsg())
        .isEqualTo(CONCAT_ERROR.formatted(row.getRowNumber(), falseIdOne));
    assertThat(errors.getLast().getMsg())
        .isEqualTo(CONCAT_ERROR.formatted(row3.getRowNumber(), falseIdTwo));
  }

  @Test
  void shouldReturnEmptyListIfAllValuesOfCsvAreNull() {
    IgsOverviewCsv row = IgsOverviewCsv.builder().build();
    List<ValidationError> errors = new ArrayList<>(rule.applyOnValue(row));
    assertThat(errors).isEmpty();
  }

  private void mockMessageWithRowNumber(IgsOverviewCsv row) {
    String rowNumber = String.valueOf(row.getRowNumber());
    when(messageSourceWrapper.getMessage(ERROR_PREFIX, rowNumber))
        .thenReturn(MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER.formatted(rowNumber));
  }

  private void mockMessageWithFalseId(String falseId) {
    when(messageSourceWrapper.getMessage(ERROR_SEQUENCING_LAB, falseId))
        .thenReturn(MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER.formatted(falseId));
  }
}
