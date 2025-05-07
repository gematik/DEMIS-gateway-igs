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

import static de.gematik.demis.igs.gateway.TestUtils.MOCKED_ERROR_MESSAGE;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_EMPTY_FILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.gematik.demis.igs.gateway.IgsOverviewCsvTestData;
import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.csv.InvalidInputDataException;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import de.gematik.demis.igs.gateway.csv.validation.rules.CsvValidationRule;
import de.gematik.demis.igs.gateway.csv.validation.rules.FieldConstraintsRule;
import de.gematik.demis.igs.gateway.csv.validation.rules.PrimeDiagnosticLabRule;
import de.gematik.demis.igs.gateway.csv.validation.rules.SequencingLabRule;
import de.gematik.demis.igs.gateway.csv.validation.rules.UniqueFilenameRule;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CsvContentValidatorTest {

  private static final String SAMPLE_ERROR_MESSAGE = "Invalid Input Data";
  private static final FieldConstraintsRule fieldConstraintsRule = mock(FieldConstraintsRule.class);
  private static final PrimeDiagnosticLabRule primeDiagnosticLabRule =
      mock(PrimeDiagnosticLabRule.class);
  private static final SequencingLabRule sequencingLabRule = mock(SequencingLabRule.class);
  private static final UniqueFilenameRule uniqueFilenameRule = mock(UniqueFilenameRule.class);
  private static final MessageSourceWrapper MESSAGE_SOURCE_WRAPPER =
      mock(MessageSourceWrapper.class);

  @NullSource
  @EmptySource
  @ParameterizedTest
  void shouldValidateWhenIgsOverviewDataItemsIsEmpty(List<IgsOverviewCsv> input) {
    MessageSourceWrapper messageSourceWrapper = mock(MessageSourceWrapper.class);
    when(messageSourceWrapper.getMessage(any())).thenReturn(MOCKED_ERROR_MESSAGE);
    CsvContentValidator validator =
        new CsvContentValidator(
            List.of(
                fieldConstraintsRule,
                primeDiagnosticLabRule,
                sequencingLabRule,
                uniqueFilenameRule),
            messageSourceWrapper);
    InvalidInputDataException ex =
        assertThrows(InvalidInputDataException.class, () -> validator.validate(input));
    verify(messageSourceWrapper, times(1)).getMessage(ERROR_EMPTY_FILE);
    assertThat(ex.getErrors()).hasSize(1);
    assertThat(ex.getErrors().getFirst().getRowNumber()).isZero();
    assertThat(ex.getErrors().getFirst().getMsg()).isEqualTo(MOCKED_ERROR_MESSAGE);
    assertThat(ex.getErrors().getFirst().getErrorCode()).isEqualTo(ErrorCode.EMPTY_FILE);
    assertThat(ex.getErrors().getFirst().getFoundValue()).isNull();
    assertThat(ex.getErrors().getFirst().getColumnName()).isNull();
  }

  public static Stream<Arguments> shouldAddErrorToReportIfRuleFails() {
    return Stream.of(
        Arguments.of(fieldConstraintsRule),
        Arguments.of(primeDiagnosticLabRule),
        Arguments.of(sequencingLabRule),
        Arguments.of(uniqueFilenameRule));
  }

  @SneakyThrows
  @MethodSource
  @ParameterizedTest(name = "{0}")
  void shouldAddErrorToReportIfRuleFails(CsvValidationRule rule) {
    when(rule.applyOnValues(any()))
        .thenReturn(
            List.of(
                ValidationError.builder()
                    .errorCode(ErrorCode.REQUIRED_FIELD)
                    .msg(SAMPLE_ERROR_MESSAGE)
                    .build()));
    CsvContentValidator validator = new CsvContentValidator(List.of(rule), MESSAGE_SOURCE_WRAPPER);
    List<IgsOverviewCsv> csvs = List.of(IgsOverviewCsvTestData.firstRow.build());
    List<ValidationError> validationErrors = validator.validate(csvs);
    assertThat(validationErrors).hasSize(1);
  }
}
