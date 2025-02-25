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
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import de.gematik.demis.igs.gateway.csv.validation.rules.CsvValidationRule;
import de.gematik.demis.igs.gateway.csv.validation.rules.PrimeDiagnosticLabRule;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PrimeDiagnosticLabRuleTest extends AbstractValidatorRuleTest {

  private final CsvValidationRule rule = new PrimeDiagnosticLabRule();
  private final String CONCAT_ERROR =
      ValidationError.ErrorMessage.PREFIX.msg()
          + ValidationError.ErrorMessage.PRIME_DIAGNOSTIC_LAB.msg();

  @Test
  void shouldValidateWithoutPrimeDiagnosticLab() {
    IgsOverviewCsv secondRow = secondRowBuilder.build();
    secondRow.setPrimeDiagnosticLabName(null);
    secondRow.setPrimeDiagnosticLabEmail(null);
    secondRow.setPrimeDiagnosticLabAddress(null);
    secondRow.setPrimeDiagnosticLabCity(null);
    secondRow.setPrimeDiagnosticLabPostalCode(null);
    secondRow.setPrimeDiagnosticLabCountry(null);
    secondRow.setPrimeDiagnosticLabDemisLabId(null);
    secondRow.setPrimeDiagnosticLabFederalState(null);

    List<ValidationError> errors = new ArrayList<>(rule.applyOnValue(firstRowBuilder.build()));
    errors.addAll(rule.applyOnValue(secondRow));
    errors.addAll(rule.applyOnValue(thirdRowBuilder.build()));
    assertThat(errors).isEmpty();
  }

  @Test
  void shouldValidateWithValidPrimeDiagnosticLab() {
    List<ValidationError> errors = new ArrayList<>(rule.applyOnValue(firstRowBuilder.build()));
    errors.addAll(rule.applyOnValue(secondRowBuilder.build()));
    errors.addAll(rule.applyOnValue(thirdRowBuilder.build()));
    assertThat(errors).isEmpty();
  }

  @Test
  void shouldAddOneErrorOnPrimeDiagnosticLabWithNameOnly() {
    IgsOverviewCsv secondRow = secondRowBuilder.build();
    secondRow.setPrimeDiagnosticLabEmail(null);
    secondRow.setPrimeDiagnosticLabAddress(null);
    secondRow.setPrimeDiagnosticLabCity(null);
    secondRow.setPrimeDiagnosticLabPostalCode(null);
    secondRow.setPrimeDiagnosticLabCountry(null);
    secondRow.setPrimeDiagnosticLabDemisLabId(null);
    secondRow.setPrimeDiagnosticLabFederalState(null);

    expectOneErrorInList(secondRow);
  }

  @Test
  void shouldAddOneErrorOnPrimeDiagnosticLabWithEmailOnly() {
    IgsOverviewCsv secondRow = secondRowBuilder.build();
    secondRow.setPrimeDiagnosticLabName(null);
    secondRow.setPrimeDiagnosticLabAddress(null);
    secondRow.setPrimeDiagnosticLabCity(null);
    secondRow.setPrimeDiagnosticLabPostalCode(null);
    secondRow.setPrimeDiagnosticLabCountry(null);
    secondRow.setPrimeDiagnosticLabDemisLabId(null);
    secondRow.setPrimeDiagnosticLabFederalState(null);

    expectOneErrorInList(secondRow);
  }

  @Test
  void shouldAddOneErrorOnPrimeDiagnosticLabWithAddressOnly() {
    IgsOverviewCsv secondRow = secondRowBuilder.build();
    secondRow.setPrimeDiagnosticLabName(null);
    secondRow.setPrimeDiagnosticLabEmail(null);
    secondRow.setPrimeDiagnosticLabCity(null);
    secondRow.setPrimeDiagnosticLabPostalCode(null);
    secondRow.setPrimeDiagnosticLabCountry(null);
    secondRow.setPrimeDiagnosticLabDemisLabId(null);
    secondRow.setPrimeDiagnosticLabFederalState(null);

    expectOneErrorInList(secondRow);
  }

  @Test
  void shouldAddOneErrorOnPrimeDiagnosticLabWithCityOnly() {
    IgsOverviewCsv secondRow = secondRowBuilder.build();
    secondRow.setPrimeDiagnosticLabName(null);
    secondRow.setPrimeDiagnosticLabEmail(null);
    secondRow.setPrimeDiagnosticLabAddress(null);
    secondRow.setPrimeDiagnosticLabPostalCode(null);
    secondRow.setPrimeDiagnosticLabCountry(null);
    secondRow.setPrimeDiagnosticLabDemisLabId(null);
    secondRow.setPrimeDiagnosticLabFederalState(null);

    expectOneErrorInList(secondRow);
  }

  @Test
  void shouldAddOneErrorOnPrimeDiagnosticLabWithPostalCodeOnly() {
    IgsOverviewCsv secondRow = secondRowBuilder.build();
    secondRow.setPrimeDiagnosticLabName(null);
    secondRow.setPrimeDiagnosticLabEmail(null);
    secondRow.setPrimeDiagnosticLabAddress(null);
    secondRow.setPrimeDiagnosticLabCity(null);
    secondRow.setPrimeDiagnosticLabCountry(null);
    secondRow.setPrimeDiagnosticLabDemisLabId(null);
    secondRow.setPrimeDiagnosticLabFederalState(null);

    expectOneErrorInList(secondRow);
  }

  @Test
  void shouldAddOneErrorOnPrimeDiagnosticLabWithCountryOnly() {
    IgsOverviewCsv secondRow = secondRowBuilder.build();
    secondRow.setPrimeDiagnosticLabName(null);
    secondRow.setPrimeDiagnosticLabEmail(null);
    secondRow.setPrimeDiagnosticLabAddress(null);
    secondRow.setPrimeDiagnosticLabCity(null);
    secondRow.setPrimeDiagnosticLabPostalCode(null);
    secondRow.setPrimeDiagnosticLabDemisLabId(null);
    secondRow.setPrimeDiagnosticLabFederalState(null);

    expectOneErrorInList(secondRow);
  }

  @Test
  void shouldAddOneErrorOnPrimeDiagnosticLabWithDemisLabIdOnly() {
    IgsOverviewCsv secondRow = secondRowBuilder.build();
    secondRow.setPrimeDiagnosticLabName(null);
    secondRow.setPrimeDiagnosticLabEmail(null);
    secondRow.setPrimeDiagnosticLabAddress(null);
    secondRow.setPrimeDiagnosticLabCity(null);
    secondRow.setPrimeDiagnosticLabPostalCode(null);
    secondRow.setPrimeDiagnosticLabCountry(null);
    secondRow.setPrimeDiagnosticLabFederalState(null);

    expectOneErrorInList(secondRow);
  }

  @Test
  void shouldAddOneErrorOnPrimeDiagnosticLabWithFederalStateOnly() {
    IgsOverviewCsv secondRow = secondRowBuilder.build();
    secondRow.setPrimeDiagnosticLabName(null);
    secondRow.setPrimeDiagnosticLabEmail(null);
    secondRow.setPrimeDiagnosticLabAddress(null);
    secondRow.setPrimeDiagnosticLabCity(null);
    secondRow.setPrimeDiagnosticLabPostalCode(null);
    secondRow.setPrimeDiagnosticLabCountry(null);
    secondRow.setPrimeDiagnosticLabDemisLabId(null);

    expectOneErrorInList(secondRow);
  }

  private void expectOneErrorInList(IgsOverviewCsv row) {
    List<ValidationError> errors = new ArrayList<>(rule.applyOnValue(firstRowBuilder.build()));
    errors.addAll(rule.applyOnValue(row));
    errors.addAll(rule.applyOnValue(thirdRowBuilder.build()));
    assertThat(errors).hasSize(1);
    assertThat(errors.getFirst().getMsg()).isEqualTo(CONCAT_ERROR.formatted(row.getRowNumber()));
    assertThat(errors.getFirst().getErrorCode()).isEqualTo(ErrorCode.PRIME_DIAGNOSTIC_LAB);
    assertThat(errors.getFirst().getRowNumber()).isEqualTo(row.getRowNumber());
  }

  @Test
  void shouldAddTwoErrorsIfTwoRowsAreWrong() {
    IgsOverviewCsv firstRow = firstRowBuilder.build();
    firstRow.setPrimeDiagnosticLabName(null);
    firstRow.setPrimeDiagnosticLabEmail(null);
    firstRow.setPrimeDiagnosticLabAddress(null);
    firstRow.setPrimeDiagnosticLabCity(null);
    firstRow.setPrimeDiagnosticLabPostalCode(null);
    firstRow.setPrimeDiagnosticLabCountry(null);
    firstRow.setPrimeDiagnosticLabFederalState(null);

    IgsOverviewCsv secondRow = secondRowBuilder.build();
    secondRow.setPrimeDiagnosticLabName(null);
    secondRow.setPrimeDiagnosticLabEmail(null);
    secondRow.setPrimeDiagnosticLabAddress(null);
    secondRow.setPrimeDiagnosticLabCity(null);
    secondRow.setPrimeDiagnosticLabPostalCode(null);
    secondRow.setPrimeDiagnosticLabCountry(null);
    secondRow.setPrimeDiagnosticLabDemisLabId(null);

    List<IgsOverviewCsv> igsOverviewDataItems = List.of(firstRow, secondRow);
    List<ValidationError> errors = new ArrayList<>(rule.applyOnValue(firstRow));
    errors.addAll(rule.applyOnValue(secondRow));
    assertThat(errors).hasSize(2);
    assertThat(errors.getFirst().getMsg())
        .isEqualTo(CONCAT_ERROR.formatted(igsOverviewDataItems.getFirst().getRowNumber()));
    assertThat(errors.getLast().getMsg())
        .isEqualTo(CONCAT_ERROR.formatted(igsOverviewDataItems.getLast().getRowNumber()));
  }
}
