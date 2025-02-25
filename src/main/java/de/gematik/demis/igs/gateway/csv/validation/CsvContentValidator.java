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

import de.gematik.demis.igs.gateway.csv.InvalidInputDataException;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorMessage;
import de.gematik.demis.igs.gateway.csv.validation.rules.CsvValidationRule;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Performs validations on the CSV file. */
@Component
public class CsvContentValidator {

  private final List<CsvValidationRule> rules;

  @Autowired
  public CsvContentValidator(List<CsvValidationRule> rules) {
    this.rules = rules;
  }

  /**
   * Validates CSV Data against the defined rules.
   *
   * @param igsOverviewDataItems The provided CSV data
   * @throws InvalidInputDataException In case the provided data about the prime diagnostic lab is
   *     inconsistent
   */
  public List<ValidationError> validate(List<IgsOverviewCsv> igsOverviewDataItems) {
    handleBlankCsvContent(igsOverviewDataItems);
    return applyValidationRules(igsOverviewDataItems);
  }

  private void handleBlankCsvContent(List<IgsOverviewCsv> igsOverviewDataItems) {
    if (igsOverviewDataItems == null || igsOverviewDataItems.isEmpty()) {
      throw new InvalidInputDataException(
          List.of(
              ValidationError.builder()
                  .rowNumber(0)
                  .msg(ErrorMessage.EMPTY_FILE.msg())
                  .errorCode(ErrorCode.EMPTY_FILE)
                  .build()));
    }
  }

  private List<ValidationError> applyValidationRules(List<IgsOverviewCsv> igsOverviewDataItems) {
    List<ValidationError> errors = new ArrayList<>();
    for (CsvValidationRule rule : rules) {
      errors.addAll(rule.applyOnValues(igsOverviewDataItems));
    }
    return errors;
  }
}
