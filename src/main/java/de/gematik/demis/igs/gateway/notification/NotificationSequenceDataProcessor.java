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

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Preprocesses data sent from frontend and prepares it for further processing creating notification
 */
@Service
public class NotificationSequenceDataProcessor {

  private static final String DOCUMENT_REFERENCE_ID_PLACEHOLDER = "{documentId}";

  @Value("${igs-gateway.client.igsPublic}")
  protected String igsPublicUrl;

  @Value("${igs-gateway.client.igsReadBinary}")
  protected String igsReadBinaryPath;

  /**
   * Checks if the igsReadBinaryPath contains the placeholder for the document reference id. This is
   * necessary for the correct replacement of the document reference id.
   */
  @PostConstruct
  public void init() {
    if (!igsReadBinaryPath.contains(DOCUMENT_REFERENCE_ID_PLACEHOLDER)) {
      throw new IllegalArgumentException(
          "igsReadBinary-variable does not contain the placeholder "
              + DOCUMENT_REFERENCE_ID_PLACEHOLDER);
    }
  }

  /**
   * Processes the notification data sent from the frontend. This includes setting the date of
   * submission to now and replacing the document
   *
   * @param notificationData The data sent from the frontend
   * @return The processed data
   */
  public IgsOverviewModel processNotificationData(IgsOverviewModel notificationData) {
    IgsOverviewModel preprocessedOverviewData = new IgsOverviewModel();
    BeanUtils.copyProperties(notificationData, preprocessedOverviewData);
    setNotificationDateToNow(preprocessedOverviewData);
    replaceDocumentReference(preprocessedOverviewData);
    return preprocessedOverviewData;
  }

  private void setNotificationDateToNow(IgsOverviewModel notificationData) {
    notificationData.setDateOfSubmission(new Date());
  }

  private void replaceDocumentReference(IgsOverviewModel data) {
    if (StringUtils.isNotBlank(data.getFileOneDocumentReference())) {
      data.setFileOneDocumentReference(getDownloadUrl(data.getFileOneDocumentReference()));
    }
    if (StringUtils.isNotBlank(data.getFileTwoDocumentReference())) {
      data.setFileTwoDocumentReference(getDownloadUrl(data.getFileTwoDocumentReference()));
    }
  }

  private String getDownloadUrl(String documentReference) {
    return igsPublicUrl
        + igsReadBinaryPath.replace(DOCUMENT_REFERENCE_ID_PLACEHOLDER, documentReference);
  }
}
