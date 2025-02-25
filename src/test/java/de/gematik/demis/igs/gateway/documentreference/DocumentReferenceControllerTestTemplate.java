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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.gematik.demis.igs.gateway.FutsMockTestTemplate;
import de.gematik.demis.igs.gateway.IgsGatewayException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentReferenceControllerTestTemplate extends FutsMockTestTemplate {

  public static final String CREATE_DOCUMENT_REFERENCE_PATH = "/document-reference";
  private static final String SIMULATED_EXCEPTION_MESSAGE = "a simulated exception";
  private static final String MOCK_HASH = "MOCK_HASH";

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DocumentReferenceService documentReferenceService;

  @Test
  @SneakyThrows
  void shouldReturnInternalServerErrorOnIgsGatewayException() {
    when(documentReferenceService.createDocumentReference(MOCK_HASH))
        .thenThrow(
            new IgsGatewayException(
                SIMULATED_EXCEPTION_MESSAGE, new RuntimeException(SIMULATED_EXCEPTION_MESSAGE)));
    mockMvc
        .perform(post(CREATE_DOCUMENT_REFERENCE_PATH).param("hash", MOCK_HASH))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestIfNoHashWasDelivered() {
    mockMvc.perform(post(CREATE_DOCUMENT_REFERENCE_PATH)).andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturnDocumentReferenceCreatedResponse() {
    when(documentReferenceService.createDocumentReference(MOCK_HASH))
        .thenReturn(
            new DocumentReferenceCreatedResponse("http://localhost:8080/fhir/DocumentReference"));

    mockMvc
        .perform(post(CREATE_DOCUMENT_REFERENCE_PATH).param("hash", MOCK_HASH))
        .andExpect(status().isOk());
  }
}
