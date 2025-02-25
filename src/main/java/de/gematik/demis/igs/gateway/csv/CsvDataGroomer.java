package de.gematik.demis.igs.gateway.csv;

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

import static de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService.System.SEQUENCING_REASON;
import static de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService.System.UPLOAD_STATUS;

import de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.model.OverviewDataCsv;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/** Preprocesses data extracted from the CSV for subsequent use in a notification. */
@Component
@RequiredArgsConstructor
public class CsvDataGroomer {

  private static final String FAKE_DEMIS_NOTIFICATION_ID = "00000000-0000-0000-0000-000000000000";

  private final ValueSetMappingService valueSetMappingService;

  /**
   * Preprocesses data extracted from the CSV for subsequent use in a notification. This comprises
   * converting certain entries from the CSV to lower case, converting entries to a required format
   * by querying the FUTS, as well as replacing empty DEMIS notification IDs with a fake value.
   *
   * @param igsOverviewModel The data extracted from the CSV.
   * @return The data as required in notifications.
   */
  public List<IgsOverviewCsv> prepareForResponse(final List<IgsOverviewCsv> igsOverviewModel) {
    return igsOverviewModel.stream()
        .map(this::convertToLowerCase)
        .map(this::replaceCodes)
        .map(this::replaceEmptyDemisNotificationId)
        .toList();
  }

  private IgsOverviewCsv convertToLowerCase(final OverviewDataCsv igsOverviewModel) {
    IgsOverviewCsv preprocessedOverviewData = new IgsOverviewCsv();
    BeanUtils.copyProperties(igsOverviewModel, preprocessedOverviewData);
    preprocessedOverviewData.setSequencingInstrument(
        igsOverviewModel.getSequencingInstrument().toLowerCase());
    preprocessedOverviewData.setRepositoryName(igsOverviewModel.getRepositoryName().toLowerCase());
    preprocessedOverviewData.setSequencingPlatform(
        igsOverviewModel.getSequencingPlatform().toLowerCase());
    if (igsOverviewModel.getSequencingStrategy() != null) {
      preprocessedOverviewData.setSequencingStrategy(
          igsOverviewModel.getSequencingStrategy().toLowerCase());
    }
    return preprocessedOverviewData;
  }

  private IgsOverviewCsv replaceCodes(OverviewDataCsv igsOverviewModel) {
    IgsOverviewCsv preprocessedOverviewData = new IgsOverviewCsv();
    BeanUtils.copyProperties(igsOverviewModel, preprocessedOverviewData);
    preprocessedOverviewData.setSequencingReason(
        valueSetMappingService.getCodeFromCodeOrIncompleteDisplay(
            SEQUENCING_REASON, igsOverviewModel.getSequencingReason()));
    preprocessedOverviewData.setUploadStatus(
        valueSetMappingService.getCodeFromCodeOrIncompleteDisplay(
            UPLOAD_STATUS, igsOverviewModel.getUploadStatus()));
    return preprocessedOverviewData;
  }

  private IgsOverviewCsv replaceEmptyDemisNotificationId(OverviewDataCsv igsOverviewModel) {
    IgsOverviewCsv preprocessedOverviewData = new IgsOverviewCsv();
    BeanUtils.copyProperties(igsOverviewModel, preprocessedOverviewData);
    if (StringUtils.isBlank(preprocessedOverviewData.getDemisNotificationId())) {
      preprocessedOverviewData.setDemisNotificationId(FAKE_DEMIS_NOTIFICATION_ID);
    }
    return preprocessedOverviewData;
  }
}
