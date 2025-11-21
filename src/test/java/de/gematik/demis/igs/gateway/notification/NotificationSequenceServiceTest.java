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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.igs.gateway.TestUtils.determineVersionInfos;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.igs.gateway.communication.IgsServiceClient;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import feign.Response;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.http.ResponseEntity;

class NotificationSequenceServiceTest {

  private static final int STATUS_CODE_VALIDATION_CASE = 422;
  private static final int STATUS_CODE_ERROR_CASE = 404;
  private static final String REASON_ERROR_CASE = "not found";
  private static final String PATH_TO_NOTIFICATION_RESPONSE =
      "src/test/resources/notificationResponse.json";
  private final NotificationSequenceDataProcessor notificationSequenceDataProcessor =
      mock(NotificationSequenceDataProcessor.class);
  private IgsServiceClient igsServiceClient = mock(IgsServiceClient.class);
  private NotificationSequenceService notificationSequenceService;

  @BeforeEach
  void setUp() {
    when(notificationSequenceDataProcessor.processNotificationData(any(IgsOverviewModel.class)))
        .thenAnswer((Answer<IgsOverviewData>) invocation -> invocation.getArgument(0));
    igsServiceClient = mock(IgsServiceClient.class);
    notificationSequenceService =
        new NotificationSequenceService(
            igsServiceClient,
            FhirContext.forR4Cached().newJsonParser(),
            notificationSequenceDataProcessor,
            determineVersionInfos());
  }

  @AfterEach
  void tearDown() {
    reset(igsServiceClient, notificationSequenceDataProcessor);
  }

  @Test
  @SneakyThrows
  void shouldSendNotificationSequence() {
    Response ncapiResponse = mock(Response.class);
    when(ncapiResponse.status()).thenReturn(200);

    String responseString =
        Files.readString(Path.of(PATH_TO_NOTIFICATION_RESPONSE), StandardCharsets.UTF_8);
    Response.Body responseBody = mock(Response.Body.class);
    StringReader stringReader = new StringReader(responseString);
    when(responseBody.asReader(StandardCharsets.UTF_8)).thenReturn(stringReader);
    when(ncapiResponse.body()).thenReturn(responseBody);
    when(igsServiceClient.sendNotificationSequence(any())).thenReturn(ncapiResponse);

    ResponseEntity response =
        notificationSequenceService.sendNotificationSequence(
            NotificationTestDataGenerator.generateIgsOverviewModel());

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isNotNull();
    assertThat(response.getStatusCode().value()).isEqualTo(200);
  }

  @Test
  @SneakyThrows
  void shouldThrowNotificationSendExceptionOn404FromNcapi() {
    Response ncapiResponse = mock(Response.class);
    when(ncapiResponse.status()).thenReturn(STATUS_CODE_ERROR_CASE);
    when(ncapiResponse.reason()).thenReturn(REASON_ERROR_CASE);
    when(igsServiceClient.sendNotificationSequence(any())).thenReturn(ncapiResponse);

    assertThatThrownBy(
            () ->
                notificationSequenceService.sendNotificationSequence(
                    NotificationTestDataGenerator.generateIgsOverviewModel()))
        .isInstanceOf(NotificationSendException.class)
        .satisfies(
            exception -> {
              assertThat(exception)
                  .extracting("body.detail")
                  .isEqualTo(
                      String.format(
                          "Failed to send notification sequence: HttpStatus: %d, Reason: %s",
                          STATUS_CODE_ERROR_CASE, REASON_ERROR_CASE));
            });
  }

  @Test
  @SneakyThrows
  void shouldThrowNotificationValidationExceptionOn422FromNcapi() {
    Response ncapiResponse = mock(Response.class);
    when(ncapiResponse.status()).thenReturn(STATUS_CODE_VALIDATION_CASE);
    Response.Body responseBody = mock(Response.Body.class);
    when(responseBody.asReader(StandardCharsets.UTF_8))
        .thenReturn(new StringReader("body from ncapi"));
    when(ncapiResponse.body()).thenReturn(responseBody);
    when(igsServiceClient.sendNotificationSequence(any())).thenReturn(ncapiResponse);

    assertThatThrownBy(
            () ->
                notificationSequenceService.sendNotificationSequence(
                    NotificationTestDataGenerator.generateIgsOverviewModel()))
        .isInstanceOf(NotificationValidationException.class)
        .satisfies(
            exception -> {
              assertThat(exception)
                  .extracting("body.title")
                  .isEqualTo("Fehler beim Übermitteln der Meldung: Validierung fehlgeschlagen");
              assertThat(exception).extracting("body.detail").isEqualTo("body from ncapi");
            });
  }
}
