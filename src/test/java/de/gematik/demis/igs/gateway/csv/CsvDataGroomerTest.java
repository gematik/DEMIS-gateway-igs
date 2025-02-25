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

import static de.gematik.demis.igs.gateway.IgsOverviewCsvTestData.FAKE_DEMIS_NOTIFICATION_ID;
import static de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService.System.SEQUENCING_REASON;
import static de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService.System.UPLOAD_STATUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import de.gematik.demis.igs.gateway.IgsOverviewCsvTestData;
import de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CsvDataGroomerTest {

  private static final String TEST_DISPLAY_VALUE = "TEST_CODE";
  @Mock private ValueSetMappingService valueSetMappingService;

  @Test
  @SneakyThrows
  void shouldConvertToLowerCase() {
    List<IgsOverviewCsv> igsOverviewModel = IgsOverviewCsvTestData.determineTestOverviewData();
    CsvDataGroomer underTest = new CsvDataGroomer(valueSetMappingService);
    List<IgsOverviewCsv> preprocessedNotificationData =
        underTest.prepareForResponse(igsOverviewModel);
    for (IgsOverviewCsv igsOverviewRow : preprocessedNotificationData) {
      assertThat(igsOverviewRow.getSequencingInstrument())
          .isEqualTo(igsOverviewRow.getSequencingInstrument().toLowerCase());
      assertThat(igsOverviewRow.getRepositoryName())
          .isEqualTo(igsOverviewRow.getRepositoryName().toLowerCase());
      assertThat(igsOverviewRow.getSequencingPlatform())
          .isEqualTo(igsOverviewRow.getSequencingPlatform().toLowerCase());
      if (igsOverviewRow.getSequencingStrategy() != null) {
        assertThat(igsOverviewRow.getSequencingStrategy())
            .isEqualTo(igsOverviewRow.getSequencingStrategy().toLowerCase());
      }
    }
  }

  @Test
  @SneakyThrows
  void shouldConvertUpdateStatusAndSequencingReason() {
    List<IgsOverviewCsv> igsOverviewModel = IgsOverviewCsvTestData.determineTestOverviewData();
    CsvDataGroomer underTest = new CsvDataGroomer(valueSetMappingService);
    when(valueSetMappingService.getCodeFromCodeOrIncompleteDisplay(
            eq(SEQUENCING_REASON), anyString()))
        .thenReturn(TEST_DISPLAY_VALUE);
    when(valueSetMappingService.getCodeFromCodeOrIncompleteDisplay(eq(UPLOAD_STATUS), anyString()))
        .thenReturn(TEST_DISPLAY_VALUE);
    List<IgsOverviewCsv> preprocessedNotificationData =
        underTest.prepareForResponse(igsOverviewModel);
    for (IgsOverviewCsv igsOverviewRow : preprocessedNotificationData) {
      assertThat(igsOverviewRow.getUploadStatus()).isEqualTo(TEST_DISPLAY_VALUE);
      assertThat(igsOverviewRow.getUploadStatus()).isEqualTo(TEST_DISPLAY_VALUE);
    }
  }

  @Test
  void shouldReplaceEmptyDemisNotificationId() {
    List<IgsOverviewCsv> igsOverviewModel = IgsOverviewCsvTestData.determineTestOverviewData();
    igsOverviewModel.get(0).setDemisNotificationId(" ");
    igsOverviewModel.get(2).setDemisNotificationId(null);
    CsvDataGroomer underTest = new CsvDataGroomer(valueSetMappingService);
    List<IgsOverviewCsv> preprocessedNotificationData =
        underTest.prepareForResponse(igsOverviewModel);
    assertThat(preprocessedNotificationData.get(0).getDemisNotificationId())
        .isNotNull()
        .isEqualTo(FAKE_DEMIS_NOTIFICATION_ID);
    assertThat(preprocessedNotificationData.get(1).getDemisNotificationId())
        .isNotNull()
        .isEqualTo("25cae3da-a8ae-4363-8475-6c24c897f099");
    assertThat(preprocessedNotificationData.get(2).getDemisNotificationId())
        .isNotNull()
        .isEqualTo(FAKE_DEMIS_NOTIFICATION_ID);
  }
}
