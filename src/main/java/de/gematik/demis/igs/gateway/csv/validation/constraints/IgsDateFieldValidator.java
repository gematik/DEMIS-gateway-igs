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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import org.apache.commons.lang3.StringUtils;

/** Validator for the {@link IgsDate} annotation to ensure the date format is yyyy-MM-dd. */
public class IgsDateFieldValidator implements ConstraintValidator<IgsDate, String> {
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

  /**
   * Validates that the given date string conforms to the yyyy-MM-dd format, as well as being a
   * valid date.
   *
   * @param value the date string to validate
   * @param context the context in which the constraint is evaluated
   * @return true if the date string is valid, null or empty, false otherwise
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (StringUtils.isEmpty(value)) {
      return true;
    }
    try {
      LocalDate.parse(value, DATE_FORMATTER);
      return true;
    } catch (RuntimeException e) {
      return false;
    }
  }
}
