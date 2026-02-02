package de.gematik.demis.igs.gateway.csv.validation.rules;

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

import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_PRIME_DIAGNOSTIC_LAB;

import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** Checks whether the submitting facility is either not provided or has all required fields. */
@Component
@RequiredArgsConstructor
public class PrimeDiagnosticLabRule extends CsvValidationRule implements ErrorDecorator {

  private final MessageSourceWrapper messageSourceWrapper;

  @Override
  public List<ValidationError> applyOnValue(IgsOverviewCsv csvRow) {
    if (hasSubmittingFacility(csvRow)) {
      return validateSubmittingFacilityData(csvRow);
    }
    return List.of();
  }

  private boolean hasSubmittingFacility(IgsOverviewCsv igsOverviewModel) {
    return StringUtils.isNotBlank(igsOverviewModel.getPrimeDiagnosticLabName())
        || StringUtils.isNotBlank(igsOverviewModel.getPrimeDiagnosticLabEmail())
        || StringUtils.isNotBlank(igsOverviewModel.getPrimeDiagnosticLabAddress())
        || StringUtils.isNotBlank(igsOverviewModel.getPrimeDiagnosticLabCity())
        || StringUtils.isNotBlank(igsOverviewModel.getPrimeDiagnosticLabPostalCode())
        || StringUtils.isNotBlank(igsOverviewModel.getPrimeDiagnosticLabCountry())
        || StringUtils.isNotBlank(igsOverviewModel.getPrimeDiagnosticLabDemisLabId())
        || StringUtils.isNotBlank(igsOverviewModel.getPrimeDiagnosticLabFederalState());
  }

  private List<ValidationError> validateSubmittingFacilityData(IgsOverviewCsv igsOverviewModel) {
    if (StringUtils.isBlank(igsOverviewModel.getPrimeDiagnosticLabName())
        || StringUtils.isBlank(igsOverviewModel.getPrimeDiagnosticLabEmail())
        || StringUtils.isBlank(igsOverviewModel.getPrimeDiagnosticLabAddress())
        || StringUtils.isBlank(igsOverviewModel.getPrimeDiagnosticLabCity())
        || StringUtils.isBlank(igsOverviewModel.getPrimeDiagnosticLabPostalCode())
        || StringUtils.isBlank(igsOverviewModel.getPrimeDiagnosticLabCountry())) {
      return List.of(
          createError(
              igsOverviewModel,
              ErrorCode.PRIME_DIAGNOSTIC_LAB,
              messageSourceWrapper,
              ERROR_PRIME_DIAGNOSTIC_LAB));
    }
    return List.of();
  }
}
