package de.gematik.demis.igs.gateway.csv.validation.rules;

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

import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_DATE_FORMAT;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_REQUIRED;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_UNKNOWN;

import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Checks whether all required fields are present. */
@Component
@RequiredArgsConstructor
public class FieldConstraintsRule extends CsvValidationRule implements ErrorDecorator {

  private static final String IGS_DATE_ANNOTATION = "IgsDate";
  private static final String IGS_REQUIRED_ANNOTATION = "IgsRequired";
  private static final String CSV_COLUMN_NAME_ANNOTATION = "column";

  private final Validator validator;

  private final MessageSourceWrapper messageSourceWrapper;

  @Override
  public List<ValidationError> applyOnValue(IgsOverviewCsv csvRow) {
    Set<ConstraintViolation<IgsOverviewCsv>> violations = validator.validate(csvRow);
    return violations.stream().map(this::mapViolationToError).toList();
  }

  private ValidationError mapViolationToError(ConstraintViolation<IgsOverviewCsv> violation) {
    String annotationName =
        violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
    return switch (annotationName) {
      case IGS_REQUIRED_ANNOTATION -> requiredFieldError(violation);
      case IGS_DATE_ANNOTATION -> dateFieldError(violation);
      default -> unknownError(violation);
    };
  }

  private ValidationError requiredFieldError(ConstraintViolation<IgsOverviewCsv> violation) {
    final String csvColumnName = csvColumnName(violation);
    return createError(
        violation.getRootBean(),
        csvColumnName,
        ErrorCode.REQUIRED_FIELD,
        messageSourceWrapper,
        ERROR_REQUIRED,
        csvColumnName);
  }

  private ValidationError dateFieldError(ConstraintViolation<IgsOverviewCsv> violation) {
    final String csvColumnName = csvColumnName(violation);
    return createError(
        violation.getRootBean(),
        String.valueOf(violation.getInvalidValue()),
        csvColumnName,
        ErrorCode.DATE_FORMAT,
        messageSourceWrapper,
        ERROR_DATE_FORMAT,
        csvColumnName,
        String.valueOf(violation.getInvalidValue()));
  }

  private ValidationError unknownError(ConstraintViolation<IgsOverviewCsv> violation) {
    final String csvColumnName = csvColumnName(violation);
    return createError(
        violation.getRootBean(),
        ErrorCode.UNKNOWN,
        messageSourceWrapper,
        ERROR_UNKNOWN,
        csvColumnName);
  }

  private String csvColumnName(ConstraintViolation<IgsOverviewCsv> violation) {
    return violation
        .getConstraintDescriptor()
        .getAttributes()
        .get(CSV_COLUMN_NAME_ANNOTATION)
        .toString();
  }
}
