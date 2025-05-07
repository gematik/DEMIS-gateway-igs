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

import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_DUPLICATE_FILE_NAME;

import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** Checks whether the filenames in a CSV file are unique. */
@Component
@RequiredArgsConstructor
public class UniqueFilenameRule extends CsvValidationRule implements ErrorDecorator {

  private final MessageSourceWrapper messageSourceWrapper;

  @Override
  public List<ValidationError> applyOnValues(List<IgsOverviewCsv> igsOverviewDataItems) {
    Set<String> uniqueFilenames = new HashSet<>();
    return igsOverviewDataItems.stream()
        .map(csvRow -> checkIfDouble(csvRow, uniqueFilenames))
        .filter(l -> !l.isEmpty())
        .flatMap(List::stream)
        .toList();
  }

  List<ValidationError> checkIfDouble(IgsOverviewCsv csvRow, Set<String> uniqueFilenames) {
    List<ValidationError> errors = new ArrayList<>();
    for (String fileName : extractFilenames(csvRow)) {
      if (!uniqueFilenames.add(fileName)) {
        errors.add(
            createError(
                csvRow,
                ErrorCode.UNIQUE_FILENAME,
                messageSourceWrapper,
                ERROR_DUPLICATE_FILE_NAME,
                fileName));
      }
    }
    return errors;
  }

  private List<String> extractFilenames(IgsOverviewCsv igsOverviewDataItem) {
    List<String> fileNames = new ArrayList<>();
    if (igsOverviewDataItem != null) {
      if (StringUtils.isNotBlank(igsOverviewDataItem.getFileOneName())) {
        fileNames.add(igsOverviewDataItem.getFileOneName());
      }
      if (StringUtils.isNotBlank(igsOverviewDataItem.getFileTwoName())) {
        fileNames.add(igsOverviewDataItem.getFileTwoName());
      }
    }
    return fileNames;
  }
}
