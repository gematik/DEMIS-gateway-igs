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

import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_SEQUENCING_LAB;
import static java.util.Objects.isNull;

import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Checks whether the sequencing lab DEMIS lab ID is a five-digit number. */
@Component
@RequiredArgsConstructor
public class SequencingLabRule extends CsvValidationRule implements ErrorDecorator {

  private final MessageSourceWrapper messageSourceWrapper;

  private final Pattern sequencingLabDemisLabIdPattern = Pattern.compile("\\d{5}");

  @Override
  public List<ValidationError> applyOnValue(IgsOverviewCsv csvRow) {
    // If splitting char is invalid the whole csvRow values are empty. This will be handled by the
    // required fields constraints and header constraints. To prevent NPE empty list is returned.
    if (isNull(csvRow.getSequencingLabDemisLabId())) {
      return List.of();
    }
    if (!sequencingLabDemisLabIdPattern.matcher(csvRow.getSequencingLabDemisLabId()).matches()) {
      return List.of(
          createError(
              csvRow,
              ErrorCode.SEQUENCING_LAB,
              messageSourceWrapper,
              ERROR_SEQUENCING_LAB,
              csvRow.getSequencingLabDemisLabId()));
    }
    return List.of();
  }
}
