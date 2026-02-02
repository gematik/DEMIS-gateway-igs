package de.gematik.demis.igs.gateway.notification;

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

import ca.uhn.fhir.parser.IParser;
import de.gematik.demis.igs.gateway.communication.IgsServiceClient;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory.ProcessNotificationSequenceRequestParametersBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.InvalidInputDataException;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.VersionInfos;
import feign.FeignException;
import feign.Response;
import feign.Util;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/** Generates an IGS notification and sends it to NCAPI. */
@Component
@AllArgsConstructor
@Slf4j
class NotificationSequenceService {

  private IgsServiceClient igsServiceClient;
  private IParser fhirParser;
  private NotificationSequenceDataProcessor notificationSequenceDataProcessor;
  private VersionInfos codeSystemVersions;

  /**
   * Generates an IGS notification and sends it to NCAPI.
   *
   * @param notificationData The data for the notification.
   * @return The HTTP response for the client.
   */
  ResponseEntity<String> sendNotificationSequence(IgsOverviewModel notificationData) {

    notificationData = notificationSequenceDataProcessor.processNotificationData(notificationData);

    String fhirNotificationSequenceJson;
    try {
      fhirNotificationSequenceJson =
          fhirParser.encodeResourceToString(
              new ProcessNotificationSequenceRequestParametersBuilder(
                      notificationData, codeSystemVersions)
                  .build());
    } catch (InvalidInputDataException exception) {
      throw new InvalidProfileDataException(exception.getMessage());
    }
    try (Response response =
        igsServiceClient.sendNotificationSequence(fhirNotificationSequenceJson)) {
      if (response.status() >= 200 && response.status() < 300) {
        String responseBody = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
        return ResponseEntity.ok().body(responseBody);
      } else if (response.status() == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
        String responseBody = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
        throw new NotificationValidationException(responseBody);
      } else {
        throw new NotificationSendException(
            String.format(
                "Failed to send notification sequence: HttpStatus: %s, Reason: %s",
                response.status(), response.reason()));
      }
    } catch (FeignException | IOException exception) {
      throw new NotificationSendException(exception);
    }
  }
}
