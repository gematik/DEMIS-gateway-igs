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
 * #L%
 */

import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorMessage;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/** Checks whether the sequencing lab DEMIS lab ID is a five-digit number. */
@Component
public class SequencingLabRule extends CsvValidationRule implements ErrorDecorator {

  private final Pattern sequencingLabDemisLabIdPattern = Pattern.compile("\\d{5}");

  @Override
  public List<ValidationError> applyOnValue(IgsOverviewCsv csvRow) {
    if (!sequencingLabDemisLabIdPattern.matcher(csvRow.getSequencingLabDemisLabId()).matches()) {
      return List.of(
          createError(
              csvRow,
              ErrorMessage.SEQUENCING_LAB.msg().formatted(csvRow.getSequencingLabDemisLabId()),
              ErrorCode.SEQUENCING_LAB));
    }
    return List.of();
  }
}
