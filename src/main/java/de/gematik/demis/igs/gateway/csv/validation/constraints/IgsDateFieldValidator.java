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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.igs.gateway.csv.DateFormat.ISO_8601_DATE_FORMAT;
import static de.gematik.demis.igs.gateway.csv.DateFormat.LOCAL_DATE_FORMAT;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;

/** Validator for the {@link IgsDate} annotation to ensure the date format is yyyy-MM-dd. */
public class IgsDateFieldValidator implements ConstraintValidator<IgsDate, String> {

  /**
   * Validates that the given date string conforms either to the ISO 8601 date format or to the
   * local date format, as well as being a valid date.
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
    if (value.contains("-")) {
      return checkDateFormat(value, ISO_8601_DATE_FORMAT.getFormatter());
    } else if (value.contains(".")) {
      return checkDateFormat(value, LOCAL_DATE_FORMAT.getFormatter());
    } else {
      return false;
    }
  }

  private boolean checkDateFormat(String value, DateTimeFormatter dateTimeFormatter) {
    try {
      LocalDate.parse(value, dateTimeFormatter);
      return true;
    } catch (RuntimeException exception) {
      return false;
    }
  }
}
