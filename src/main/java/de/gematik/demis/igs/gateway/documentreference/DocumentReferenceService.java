package de.gematik.demis.igs.gateway.documentreference;

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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.gematik.demis.igs.gateway.IgsGatewayException;
import de.gematik.demis.igs.gateway.communication.IgsServiceClient;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.DocumentReferenceBuilder;
import feign.FeignException;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.DocumentReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentReferenceService {

  private final IgsServiceClient igsServiceClient;

  @Setter
  @Value("${igs-gateway.client.igsPublic}")
  private String igsServiceUrlPublic;

  static final String IGS_SERVICE_DOCUMENT_REFERENCE_BINARY_ACCESS_WRITE_SUFFIX =
      "/$binary-access-write?path=DocumentReference.content.attachment";

  DocumentReferenceCreatedResponse createDocumentReference(String hash) throws IgsGatewayException {

    String docRefJson = buildDocumentReferenceJsonString(hash);
    try (Response response = igsServiceClient.createDocumentReference(docRefJson)) {

      if (response.status() >= 200 && response.status() < 300) {
        String location =
            response.headers().get("Location").stream()
                .findFirst()
                .orElseThrow(() -> new IgsGatewayException("Location header not found"));

        String url =
            igsServiceUrlPublic
                + location
                + IGS_SERVICE_DOCUMENT_REFERENCE_BINARY_ACCESS_WRITE_SUFFIX;

        return new DocumentReferenceCreatedResponse(url);
      } else {
        throw new IgsGatewayException(
            String.format(
                "Failed to create document reference: Reason: %s, HttpStatus: %s, Body: %s",
                response.reason(), response.status(), response.body()));
      }

    } catch (FeignException e) {
      throw new IgsGatewayException("Failed to send request to IGS service", e);
    }
  }

  private String buildDocumentReferenceJsonString(String hash) {
    final DocumentReference docRef =
        DocumentReferenceBuilder.builder().hash(hash).initialize().build();
    IParser parser = FhirContext.forR4().newJsonParser();
    return parser.encodeResourceToString(docRef);
  }
}
