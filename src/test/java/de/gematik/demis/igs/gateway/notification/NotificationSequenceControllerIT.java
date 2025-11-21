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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.igs.gateway.FutsMockTestTemplate;
import de.gematik.demis.igs.gateway.communication.IgsServiceClient;
import feign.Response;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationSequenceControllerIT extends FutsMockTestTemplate {

  private static final String PATH_TO_NOTIFICATION_RESPONSE =
      "src/test/resources/notificationResponse.json";

  @Autowired private MockMvc mockMvc;

  @MockitoBean private IgsServiceClient igsServiceClient;

  @Test
  @SneakyThrows
  void shouldSendNotificationSequence() {
    Response response = mock(Response.class);
    when(response.status()).thenReturn(200);

    String responseString =
        Files.readString(Path.of(PATH_TO_NOTIFICATION_RESPONSE), StandardCharsets.UTF_8);
    Response.Body responseBody = mock(Response.Body.class);
    StringReader stringReader = new StringReader(responseString);
    when(responseBody.asReader(StandardCharsets.UTF_8)).thenReturn(stringReader);
    when(response.body()).thenReturn(responseBody);
    when(igsServiceClient.sendNotificationSequence(any())).thenReturn(response);

    ObjectMapper objectMapper = new ObjectMapper();
    String notificationData =
        objectMapper.writeValueAsString(NotificationTestDataGenerator.generateIgsOverviewModel());

    mockMvc
        .perform(
            post("/notification-sequence/$process-notification-sequence")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notificationData))
        .andExpect(status().isOk())
        .andReturn();

    ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
    verify(igsServiceClient).sendNotificationSequence(stringCaptor.capture());
    String capturedArgument = stringCaptor.getValue();
    assertThat(capturedArgument).isNotNull().hasSizeGreaterThan(9000);
  }

  @Test
  @SneakyThrows
  void shouldThrowNotificationValidationExceptionOn422Response() {
    Response response = mock(Response.class);
    Response.Body mockResponseBody = mock(Response.Body.class);
    when(mockResponseBody.asReader(StandardCharsets.UTF_8))
        .thenReturn(new StringReader("response body"));
    when(response.body()).thenReturn(mockResponseBody);
    when(response.status()).thenReturn(422);
    when(igsServiceClient.sendNotificationSequence(any())).thenReturn(response);

    ObjectMapper objectMapper = new ObjectMapper();
    String notificationData =
        objectMapper.writeValueAsString(NotificationTestDataGenerator.generateIgsOverviewModel());

    mockMvc
        .perform(
            post("/notification-sequence/$process-notification-sequence")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notificationData))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(
            jsonPath("$.title")
                .value("Fehler beim Übermitteln der Meldung: Validierung fehlgeschlagen"))
        .andExpect(jsonPath("$.status").value(422))
        .andExpect(jsonPath("$.detail").value("response body"))
        .andExpect(
            jsonPath("$.instance").value("/notification-sequence/$process-notification-sequence"))
        .andExpect(
            jsonPath("$.timestamp")
                .value(
                    Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z")))
        .andReturn();
  }

  @Test
  @SneakyThrows
  void shouldThrowNotificationSendExceptionOn404Response() {
    Response response = mock(Response.class);
    Response.Body mockResponseBody = mock(Response.Body.class);
    when(mockResponseBody.asReader(StandardCharsets.UTF_8))
        .thenReturn(new StringReader("response body"));
    when(response.body()).thenReturn(mockResponseBody);
    when(response.status()).thenReturn(404);
    when(response.reason()).thenReturn("Not Found");
    when(igsServiceClient.sendNotificationSequence(any())).thenReturn(response);

    ObjectMapper objectMapper = new ObjectMapper();
    String notificationData =
        objectMapper.writeValueAsString(NotificationTestDataGenerator.generateIgsOverviewModel());

    mockMvc
        .perform(
            post("/notification-sequence/$process-notification-sequence")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notificationData))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.title").value("Fehler beim Übermitteln der Meldung"))
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(
            jsonPath("$.detail")
                .value("Failed to send notification sequence: HttpStatus: 404, Reason: Not Found"))
        .andExpect(
            jsonPath("$.instance").value("/notification-sequence/$process-notification-sequence"))
        .andExpect(
            jsonPath("$.timestamp")
                .value(
                    Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z")))
        .andReturn();
  }

  @Test
  @SneakyThrows
  void shouldThrowBadRequestOnInvalidGender() {
    ObjectMapper objectMapper = new ObjectMapper();
    IgsOverviewModel igsOverviewModel = NotificationTestDataGenerator.generateIgsOverviewModel();
    igsOverviewModel.setHostSex("androgynous");
    String notificationData = objectMapper.writeValueAsString(igsOverviewModel);

    mockMvc
        .perform(
            post("/notification-sequence/$process-notification-sequence")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notificationData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Ungültige Eingabe-Daten"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail").value("Unknown AdministrativeGender code 'androgynous'"))
        .andExpect(
            jsonPath("$.instance").value("/notification-sequence/$process-notification-sequence"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"November", "13"})
  @SneakyThrows
  void shouldThrowBadRequestOnInvalidBirthdayMonth(String invalidMonth) {
    ObjectMapper objectMapper = new ObjectMapper();
    IgsOverviewModel igsOverviewModel = NotificationTestDataGenerator.generateIgsOverviewModel();
    igsOverviewModel.setHostBirthMonth(invalidMonth);
    String notificationData = objectMapper.writeValueAsString(igsOverviewModel);

    mockMvc
        .perform(
            post("/notification-sequence/$process-notification-sequence")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notificationData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Ungültige Eingabe-Daten"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.detail").value(String.format("Invalid birthday month: %s", invalidMonth)))
        .andExpect(
            jsonPath("$.instance").value("/notification-sequence/$process-notification-sequence"));
  }

  @ParameterizedTest
  @MethodSource("invalidBirthYear")
  @SneakyThrows
  void shouldThrowBadRequestOnInvalidBirthYear(String birthYear) {
    ObjectMapper objectMapper = new ObjectMapper();
    IgsOverviewModel igsOverviewModel = NotificationTestDataGenerator.generateIgsOverviewModel();
    igsOverviewModel.setHostBirthYear(birthYear);
    String notificationData = objectMapper.writeValueAsString(igsOverviewModel);

    mockMvc
        .perform(
            post("/notification-sequence/$process-notification-sequence")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notificationData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Ungültige Eingabe-Daten"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.detail").value(String.format("Invalid birthday year: %s", birthYear)))
        .andExpect(
            jsonPath("$.instance").value("/notification-sequence/$process-notification-sequence"));
  }

  private static Stream<Arguments> invalidBirthYear() {
    return Stream.of(
        Arguments.of("twenty twenty"),
        Arguments.of("1799"),
        Arguments.of("" + Year.now().getValue() + 1));
  }
}
