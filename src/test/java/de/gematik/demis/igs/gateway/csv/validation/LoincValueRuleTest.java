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
import static de.gematik.demis.igs.gateway.TestUtils.MOCKED_ERROR_MESSAGE_WITH_TWO_PLACEHOLDERS;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_LOINC_CODE;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.rules.CsvValidationRule;
import de.gematik.demis.igs.gateway.csv.validation.rules.LoincValueRule;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LoincValueRuleTest extends AbstractValidatorRuleTest {

  private static final String SEQUENCING_REASON_COLUMN_NAME = "SEQUENCING_REASON";
  private static final String UPLOAD_STATUS_COLUMN_NAME = "UPLOAD_STATUS";
  private static final String LOINC_ERROR_MESSAGE =
      MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER + MOCKED_ERROR_MESSAGE_WITH_TWO_PLACEHOLDERS;
  private MessageSourceWrapper messageSourceWrapper;
  private ValueSetMappingService valueSetMappingService;
  private CsvValidationRule underTest;

  @BeforeEach
  void setUp() {
    messageSourceWrapper = Mockito.mock(MessageSourceWrapper.class);
    valueSetMappingService = Mockito.mock(ValueSetMappingService.class);

    when(messageSourceWrapper.getMessage(eq(ERROR_LOINC_CODE), anyString(), anyString()))
        .thenAnswer(
            invocation -> {
              String foundValue = invocation.getArgument(1, String.class);
              String csvColumnName = invocation.getArgument(2, String.class);
              return MOCKED_ERROR_MESSAGE_WITH_TWO_PLACEHOLDERS.formatted(
                  foundValue, csvColumnName);
            });

    underTest = new LoincValueRule(messageSourceWrapper, valueSetMappingService);
  }

  @Test
  void shouldReturnNoErrors() {
    IgsOverviewCsv row = firstRowBuilder.build();
    when(valueSetMappingService.valueSetContains(any(), anyString())).thenReturn(true);

    List<ValidationError> validationErrors = underTest.applyOnValue(row);

    assertThat(validationErrors).isEmpty();
  }

  @Test
  void shouldReturnTwoErrors() {
    IgsOverviewCsv row = firstRowBuilder.build();
    final String rowNumber = String.valueOf(row.getRowNumber());
    final String uploadStatusErrorMessage =
        LOINC_ERROR_MESSAGE.formatted(
            row.getRowNumber(), row.getUploadStatus(), UPLOAD_STATUS_COLUMN_NAME);
    final String sequencingReasonErrorMessage =
        LOINC_ERROR_MESSAGE.formatted(
            row.getRowNumber(), row.getSequencingReason(), SEQUENCING_REASON_COLUMN_NAME);
    when(valueSetMappingService.valueSetContains(any(), anyString())).thenReturn(false);
    when(messageSourceWrapper.getMessage(ERROR_PREFIX, rowNumber))
        .thenReturn((MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER).formatted(rowNumber));

    List<ValidationError> validationErrors = underTest.applyOnValue(row);

    assertThat(validationErrors).hasSize(2);
    assertThat(validationErrors.stream().map(ValidationError::getMsg))
        .containsExactlyInAnyOrder(uploadStatusErrorMessage, sequencingReasonErrorMessage);
    assertThat(validationErrors.stream().map(ValidationError::getErrorCode))
        .containsOnly(ValidationError.ErrorCode.LOINC_CODE);
  }
}
