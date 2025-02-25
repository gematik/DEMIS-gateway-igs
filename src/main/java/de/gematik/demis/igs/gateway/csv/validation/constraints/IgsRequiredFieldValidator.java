package de.gematik.demis.igs.gateway.csv.validation.constraints;

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

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/** Validator for the {@link IgsRequired} annotation to ensure that a field is not blank. */
public class IgsRequiredFieldValidator implements ConstraintValidator<IgsRequired, String> {

  /**
   * Validates that the given string value is not blank.
   *
   * @param value the string value to validate
   * @param context the context in which the constraint is evaluated
   * @return true if the string value is not blank, false otherwise
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return !StringUtils.isBlank(value);
  }
}
