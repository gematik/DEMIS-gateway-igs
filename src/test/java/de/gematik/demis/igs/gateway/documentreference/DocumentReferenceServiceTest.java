package de.gematik.demis.igs.gateway.documentreference;

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

import static de.gematik.demis.igs.gateway.documentreference.DocumentReferenceService.IGS_SERVICE_DOCUMENT_REFERENCE_BINARY_ACCESS_WRITE_SUFFIX;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.gematik.demis.igs.gateway.IgsGatewayException;
import de.gematik.demis.igs.gateway.communication.IgsServiceClient;
import feign.FeignException;
import feign.Request;
import feign.Response;
import java.util.Collections;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DocumentReferenceServiceTest {

  private static final String MOCK_HASH = "MOCKHASH";
  public static final Request EMPTY_REQUEST =
      Request.create(
          Request.HttpMethod.GET, "", Collections.emptyMap(), Request.Body.empty(), null);

  private static final String EXPECTED_DOCUMENT_REFERENCE_JSON =
      "{\"resourceType\":\"DocumentReference\",\"content\":[{\"attachment\":{\"hash\":\"MOCKHASH\"}}]}";
  private static final String igsServiceUrlPublic = "https://igs-service.public";

  @Mock private IgsServiceClient igsServiceClient;

  @InjectMocks private DocumentReferenceService documentReferenceService;

  @BeforeEach
  void init() {
    documentReferenceService.setIgsServiceUrlPublic(igsServiceUrlPublic);
  }

  @Test
  void testCreateDocumentReference() throws IgsGatewayException {
    String location = "/fhir/DocumentReference/abdf8f8d-0e0f-40b2-89e3-6b7d8b06ffeb";
    Response response =
        Response.builder()
            .status(200)
            .headers(Collections.singletonMap("Location", Collections.singletonList(location)))
            .request(EMPTY_REQUEST)
            .build();

    when(igsServiceClient.createDocumentReference(EXPECTED_DOCUMENT_REFERENCE_JSON))
        .thenReturn(response);

    DocumentReferenceCreatedResponse result =
        documentReferenceService.createDocumentReference(MOCK_HASH);

    verify(igsServiceClient, times(1)).createDocumentReference(EXPECTED_DOCUMENT_REFERENCE_JSON);
    Assertions.assertThat(result.sequenceUploadUrl())
        .isEqualTo(
            igsServiceUrlPublic
                + location
                + IGS_SERVICE_DOCUMENT_REFERENCE_BINARY_ACCESS_WRITE_SUFFIX);
  }

  @Test
  void testCreateDocumentReferenceThrowsException() {

    FeignException feignException =
        FeignException.errorStatus(
            "createDocumentReference", Response.builder().request(EMPTY_REQUEST).build());
    when(igsServiceClient.createDocumentReference(EXPECTED_DOCUMENT_REFERENCE_JSON))
        .thenThrow(feignException);

    assertThrows(
        IgsGatewayException.class,
        () -> documentReferenceService.createDocumentReference(MOCK_HASH));
    verify(igsServiceClient, times(1)).createDocumentReference(EXPECTED_DOCUMENT_REFERENCE_JSON);
  }
}
