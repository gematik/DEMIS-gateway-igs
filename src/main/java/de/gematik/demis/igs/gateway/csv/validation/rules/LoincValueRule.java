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

import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_LOINC_CODE;
import static de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService.System.SEQUENCING_REASON;
import static de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService.System.UPLOAD_STATUS;

import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoincValueRule extends CsvValidationRule implements ErrorDecorator {

  private static final String SEQUENCING_REASON_COLUMN_NAME = "SEQUENCING_REASON";
  private static final String UPLOAD_STATUS_COLUMN_NAME = "UPLOAD_STATUS";

  private final MessageSourceWrapper messageSourceWrapper;
  private final ValueSetMappingService valueSetMappingService;

  @Override
  public List<ValidationError> applyOnValue(IgsOverviewCsv csvRow) {
    Optional<ValidationError> sequencingReasonError =
        validateLoinc(
            csvRow, SEQUENCING_REASON, SEQUENCING_REASON_COLUMN_NAME, csvRow.getSequencingReason());
    Optional<ValidationError> uploadStatusError =
        validateLoinc(csvRow, UPLOAD_STATUS, UPLOAD_STATUS_COLUMN_NAME, csvRow.getUploadStatus());

    return Stream.of(sequencingReasonError, uploadStatusError)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  private Optional<ValidationError> validateLoinc(
      IgsOverviewCsv csvRow,
      ValueSetMappingService.System system,
      String columnName,
      String value) {
    if (StringUtils.isBlank(value) || valueSetMappingService.valueSetContains(system, value)) {
      return Optional.empty();
    }
    return Optional.of(loincFieldError(csvRow, columnName, value));
  }

  private ValidationError loincFieldError(
      IgsOverviewCsv csvRow, String csvColumnName, String foundValue) {
    return createError(
        csvRow,
        foundValue,
        csvColumnName,
        ValidationError.ErrorCode.LOINC_CODE,
        messageSourceWrapper,
        ERROR_LOINC_CODE,
        foundValue,
        csvColumnName);
  }
}
