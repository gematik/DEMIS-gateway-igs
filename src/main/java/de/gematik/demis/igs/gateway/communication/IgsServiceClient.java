package de.gematik.demis.igs.gateway.communication;

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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "igsService",
    url = "${igs-gateway.client.igs}",
    configuration = IgsServiceClientConfiguration.class)
public interface IgsServiceClient {

  @PostMapping(
      value = "/fhir/DocumentReference",
      consumes = "application/json+fhir",
      produces = "application/json")
  Response createDocumentReference(@RequestBody String documentReferenceJson);

  @PostMapping(
      value = "/fhir/$process-notification-sequence",
      consumes = {
        APPLICATION_JSON_VALUE,
        APPLICATION_XML_VALUE,
        "application/json+fhir",
        "application/fhir+json"
      },
      produces = {
        APPLICATION_JSON_VALUE,
        APPLICATION_XML_VALUE,
        "application/json+fhir",
        "application/fhir+json"
      })
  Response sendNotificationSequence(@RequestBody String notificationSequenceJson);
}
