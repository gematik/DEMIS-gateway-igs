package de.gematik.demis.igs.gateway.notification;

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

import static de.gematik.demis.igs.gateway.IgsOverviewModelTestData.firstRow;
import static de.gematik.demis.igs.gateway.IgsOverviewModelTestData.formatter;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import java.time.Duration;
import java.util.Date;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationSequenceDataProcessorTest {

  NotificationSequenceDataProcessor underTest = new NotificationSequenceDataProcessor();

  @BeforeEach
  void setUp() {
    underTest.igsPublicUrl =
        "http://igs-service.demis.svc.cluster.local/surveillance/notification-sequence";
    underTest.igsReadBinaryPath = "/fhir/DocumentReference/{documentId}/$binary-access-read";
  }

  @Test
  @SneakyThrows
  void shouldUpdateDate() {
    Date fixedDate = formatter.parse("2022-05-18");
    IgsOverviewModel igsOverviewModel = new IgsOverviewModel();
    BeanUtils.copyProperties(igsOverviewModel, firstRow.build());
    igsOverviewModel.setDateOfSubmission(fixedDate);

    IgsOverviewData progressedData = underTest.processNotificationData(igsOverviewModel);

    assertThat(progressedData.getDateOfSubmission()).isNotEqualTo(fixedDate);
    Duration duration =
        Duration.between(progressedData.getDateOfSubmission().toInstant(), new Date().toInstant());
    assertThat(duration.toSeconds()).isLessThan(5);
  }

  @Test
  @SneakyThrows
  void shouldUpdateDocumentReferenceOne() {
    String documentReferenceOne = "documentReferenceOne";
    IgsOverviewModel igsOverviewModel = new IgsOverviewModel();
    BeanUtils.copyProperties(igsOverviewModel, firstRow.build());
    igsOverviewModel.setFileOneDocumentReference(documentReferenceOne);
    igsOverviewModel.setFileTwoDocumentReference(null);

    IgsOverviewData progressedData = underTest.processNotificationData(igsOverviewModel);

    assertThat(progressedData.getFileOneDocumentReference())
        .isEqualTo(
            underTest.igsPublicUrl
                + underTest.igsReadBinaryPath.replace("{documentId}", documentReferenceOne));
    assertThat(progressedData.getFileTwoDocumentReference()).isNull();
  }

  @Test
  @SneakyThrows
  void shouldUpdateDocumentReferenceTwo() {
    String documentReferenceTwo = "documentReferenceTwo";
    IgsOverviewModel igsOverviewModel = new IgsOverviewModel();
    BeanUtils.copyProperties(igsOverviewModel, firstRow.build());
    igsOverviewModel.setFileOneDocumentReference(null);
    igsOverviewModel.setFileTwoDocumentReference(documentReferenceTwo);

    IgsOverviewData progressedData = underTest.processNotificationData(igsOverviewModel);

    assertThat(progressedData.getFileTwoDocumentReference())
        .isEqualTo(
            underTest.igsPublicUrl
                + underTest.igsReadBinaryPath.replace("{documentId}", documentReferenceTwo));
    assertThat(progressedData.getFileOneDocumentReference()).isNull();
  }

  @Test
  @SneakyThrows
  void shouldUpdateBothDocumentReferences() {
    String documentReferenceOne = "documentReferenceOne";
    String documentReferenceTwo = "documentReferenceTwo";
    IgsOverviewModel igsOverviewModel = new IgsOverviewModel();
    BeanUtils.copyProperties(igsOverviewModel, firstRow.build());
    igsOverviewModel.setFileOneDocumentReference(documentReferenceOne);
    igsOverviewModel.setFileTwoDocumentReference(documentReferenceTwo);

    IgsOverviewData progressedData = underTest.processNotificationData(igsOverviewModel);

    assertThat(progressedData.getFileOneDocumentReference())
        .isEqualTo(
            underTest.igsPublicUrl
                + underTest.igsReadBinaryPath.replace("{documentId}", documentReferenceOne));
    assertThat(progressedData.getFileTwoDocumentReference())
        .isEqualTo(
            underTest.igsPublicUrl
                + underTest.igsReadBinaryPath.replace("{documentId}", documentReferenceTwo));
  }
}
