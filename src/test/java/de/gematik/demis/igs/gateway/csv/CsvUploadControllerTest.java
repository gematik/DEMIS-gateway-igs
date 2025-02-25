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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.gematik.demis.igs.gateway.FutsMockTestTemplate;
import de.gematik.demis.igs.gateway.IgsGatewayRuntimeException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class CsvUploadControllerTest extends FutsMockTestTemplate {

  private static final String SIMULATED_EXCEPTION_MESSAGE = "a simulated exception";

  private static final String PATH_TO_REDUCED_CSV =
      "src/test/resources/testdata/igs-batch-fastq-testdata.csv";

  @Autowired private MockMvc mockMvc;
  @MockitoBean private CsvService csvService;

  @Test
  @SneakyThrows
  void shouldReturnInternalServerErrorOnIgsGatewayRuntimeException() {
    when(csvService.processCsv(any()))
        .thenThrow(
            new IgsGatewayRuntimeException(
                SIMULATED_EXCEPTION_MESSAGE, new RuntimeException(SIMULATED_EXCEPTION_MESSAGE)));
    byte[] csvData = Files.readAllBytes(Paths.get(PATH_TO_REDUCED_CSV));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestOnCsvParseException() {
    when(csvService.processCsv(any()))
        .thenThrow(
            new CsvParseException(
                SIMULATED_EXCEPTION_MESSAGE, new RuntimeException(SIMULATED_EXCEPTION_MESSAGE)));
    byte[] csvData = Files.readAllBytes(Paths.get(PATH_TO_REDUCED_CSV));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isBadRequest());
  }
}
