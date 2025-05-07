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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.igs.gateway.DefaultExceptionHandler.ERROR_MESSAGE_INVALID_INPUT;
import static de.gematik.demis.igs.gateway.DefaultExceptionHandler.ERROR_MESSAGE_VALIDATION;
import static de.gematik.demis.igs.gateway.IgsOverviewCsvTestData.FAKE_DEMIS_NOTIFICATION_ID;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_DATE_FORMAT;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_DUPLICATE_FILE_NAME;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_EMPTY_FILE;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_INVALID_FILE_FORMAT;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_PREFIX;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_PRIME_DIAGNOSTIC_LAB;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_REQUIRED;
import static de.gematik.demis.igs.gateway.csv.OverviewDataExtractor.ERROR_MESSAGE_INCONSISTENT_ROW_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.igs.gateway.FutsMockTestTemplate;
import de.gematik.demis.igs.gateway.IgsOverviewCsvTestData;
import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.model.OverviewParsedRowResult;
import de.gematik.demis.igs.gateway.csv.model.OverviewResponse;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class CsvUploadControllerIT extends FutsMockTestTemplate {

  @Autowired ValueSetMappingService valueSetMappingService;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private MessageSourceWrapper messageSourceWrapper;

  private String CONCAT_ERROR_DATE;
  private String CONCAT_DUPLICATE;
  private String CONCAT_REQUIRED;
  private String CONCAT_LAB;

  @BeforeEach
  void setup() {
    CONCAT_ERROR_DATE =
        messageSourceWrapper.getMessage(ERROR_PREFIX)
            + messageSourceWrapper.getMessage(ERROR_DATE_FORMAT);
    CONCAT_DUPLICATE =
        messageSourceWrapper.getMessage(ERROR_PREFIX)
            + messageSourceWrapper.getMessage(ERROR_DUPLICATE_FILE_NAME);
    CONCAT_REQUIRED =
        messageSourceWrapper.getMessage(ERROR_PREFIX)
            + messageSourceWrapper.getMessage(ERROR_REQUIRED);
    CONCAT_LAB =
        messageSourceWrapper.getMessage(ERROR_PREFIX)
            + messageSourceWrapper.getMessage(ERROR_PRIME_DIAGNOSTIC_LAB);
  }

  private static final String PATH_TO_CSV =
      "src/test/resources/testdata/igs-batch-fastq-testdata.csv";
  private static final String PATH_TO_MINIMAL_CSV =
      "src/test/resources/testdata/igs-batch-fasta-testdata_minimal.csv";
  private static final String PATH_TO_MIXED_ERRORS_CSV =
      "src/test/resources/testdata/igs-batch-fastq-testdata_mixed_errors.csv";
  private static final String PATH_TO_CORRUPTED_CSV =
      "src/test/resources/testdata/igs-batch-fastq-testdata_corrupted.csv";
  private static final String PATH_TO_CSV_WITH_ONLY_EMPTY_ROWS =
      "src/test/resources/testdata/igs-batch-fastq-testdata_only_empty_rows.csv";
  private static final String PATH_TO_CSV_WITH_EMPTY_ROWS =
      "src/test/resources/testdata/igs-batch-fastq-testdata_empty_rows.csv";
  private static final String PATH_TO_CSV_WITH_WRONG_DATEFORMAT =
      "src/test/resources/testdata/igs-batch-fastq-testdata_wrong_dateformat.csv";
  private static final String PATH_TO_CSV_WITH_INVALID_FORMAT =
      "src/test/resources/testdata/igs-batch-fastq-testdata.xlsx";
  private static final String PATH_TO_CSV_WITH_INVALID_PRIME_DIAGNOSTIC_LAB =
      "src/test/resources/testdata/igs-batch-fastq-testdata_invalid_prime_diagnostic_lab.csv";
  private static final String PATH_TO_CSV_WITH_DUPLICATE_FILE_NAMES =
      "src/test/resources/testdata/igs-batch-fastq-duplicate-file-names.csv";
  private static final String PATH_TO_CSV_WITH_LOCAL_DATES =
      "src/test/resources/testdata/igs-batch-fasta-testdata_local_date_format.csv";

  @ParameterizedTest
  @ValueSource(strings = {PATH_TO_CSV, PATH_TO_CSV_WITH_LOCAL_DATES})
  void shouldUploadCsv(String pathToCsv) throws Exception {
    byte[] csvData = Files.readAllBytes(Paths.get(pathToCsv));
    valueSetMappingService.refreshCachedValues();
    MvcResult mvcResult =
        mockMvc
            .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
            .andExpect(status().isOk())
            .andReturn();

    String responseContentString = mvcResult.getResponse().getContentAsString();
    OverviewResponse response =
        objectMapper.readValue(responseContentString, OverviewResponse.class);
    List<OverviewParsedRowResult> parsedRowResultList = response.getItems();

    List<IgsOverviewCsv> expectedData = IgsOverviewCsvTestData.determineExpectedOverviewData();
    assertThat(parsedRowResultList).hasSize(expectedData.size());

    // iterate over the expected data list and compare each item with the corresponding item in the
    // parsed row result list
    for (int i = 0; i < expectedData.size(); i++) {
      assertThat(parsedRowResultList.get(i).getData())
          .usingRecursiveComparison()
          // Use a case-insensitive comparator for the "sequencingStrategy" field
          .withComparatorForFields(
              Comparator.comparing(
                  sequencingStrategy -> ((String) sequencingStrategy).toLowerCase()),
              "sequencingStrategy")
          // Ignore the "sequencingReason" and "uploadStatus" fields during comparison
          .ignoringFields("sequencingReason", "uploadStatus")
          .isEqualTo(expectedData.get(i));
    }
  }

  @Test
  void shouldIgnoreEmptyLines() throws Exception {
    byte[] csvData = Files.readAllBytes(Paths.get(PATH_TO_CSV_WITH_EMPTY_ROWS));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value(ERROR_MESSAGE_VALIDATION))
        .andExpect(jsonPath("$.errorReport", hasSize(52)))
        .andExpect(jsonPath("$.errorReport[?(@.errorCode == 'REQUIRED_FIELD')]").value(hasSize(50)))
        .andExpect(jsonPath("$.errorReport[?(@.errorCode == 'UNIQUE_FILENAME')]").value(hasSize(0)))
        .andExpect(jsonPath("$.errorReport[?(@.errorCode == 'DATE_FORMAT')]").value(hasSize(0)))
        .andExpect(
            jsonPath("$.errorReport[?(@.errorCode == 'PRIME_DIAGNOSTIC_LAB')]").value(hasSize(0)))
        .andExpect(jsonPath("$.errorReport[?(@.errorCode == 'SEQUENCING_LAB')]").value(hasSize(2)))
        .andExpect(jsonPath("$.errorReport[*].rowNumber", hasSize(52)))
        .andExpect(
            jsonPath("$.errorReport[*].rowNumber").value(everyItem(either(is(4)).or(is(5)))));
  }

  @Test
  void shouldBeAbleToHandleEmptyCSV() throws Exception {
    byte[] csvData = Files.readAllBytes(Paths.get(PATH_TO_CSV_WITH_ONLY_EMPTY_ROWS));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value(ERROR_MESSAGE_VALIDATION))
        .andExpect(jsonPath("$.title").value(ERROR_MESSAGE_INVALID_INPUT))
        .andExpect(jsonPath("$.errorReport").isArray())
        .andExpect(jsonPath("$.errorReport", hasSize(1)))
        .andExpect(
            jsonPath("$.errorReport[0].msg")
                .value(messageSourceWrapper.getMessage(ERROR_EMPTY_FILE)))
        .andExpect(jsonPath("$.errorReport[0].errorCode").value(ErrorCode.EMPTY_FILE.name()))
        .andExpect(jsonPath("$.errorReport[0].rowNumber").value(0))
        .andExpect(jsonPath("$.errorReport[0].foundValue").isEmpty())
        .andExpect(jsonPath("$.errorReport[0].columnName").isEmpty());
  }

  @NullSource
  @EmptySource
  @ParameterizedTest
  @SneakyThrows
  void shouldAddBlankInputToErrorReport(byte[] input) {
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", input))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value(ERROR_MESSAGE_VALIDATION))
        .andExpect(jsonPath("$.title").value(ERROR_MESSAGE_INVALID_INPUT))
        .andExpect(jsonPath("$.errorReport").isArray())
        .andExpect(jsonPath("$.errorReport", hasSize(1)))
        .andExpect(jsonPath("$.errorReport", hasSize(1)))
        .andExpect(
            jsonPath("$.errorReport[0].msg")
                .value((messageSourceWrapper.getMessage(ERROR_EMPTY_FILE))))
        .andExpect(jsonPath("$.errorReport[0].errorCode").value(ErrorCode.EMPTY_FILE.name()))
        .andExpect(jsonPath("$.errorReport[0].rowNumber").value(0))
        .andExpect(jsonPath("$.errorReport[0].foundValue").isEmpty())
        .andExpect(jsonPath("$.errorReport[0].columnName").isEmpty());
  }

  @Test
  void shouldUploadMinimalCsv() throws Exception {
    byte[] csvData = Files.readAllBytes(Paths.get(PATH_TO_MINIMAL_CSV));
    MvcResult mvcResult =
        mockMvc
            .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
            .andExpect(status().isOk())
            .andReturn();

    String responseContentString = mvcResult.getResponse().getContentAsString();
    OverviewResponse response =
        objectMapper.readValue(responseContentString, OverviewResponse.class);
    List<OverviewParsedRowResult> parsedRowResultList = response.getItems();

    assertThat(parsedRowResultList).hasSize(1);
    assertThat(parsedRowResultList.getFirst().getData()).isNotNull();
    IgsOverviewCsv data = parsedRowResultList.getFirst().getData();
    assertThat(data.getDemisNotificationId()).isNotNull().isEqualTo(FAKE_DEMIS_NOTIFICATION_ID);
  }

  @Test
  void shouldReturnBadRequestOnWrongDateFormatCsv() throws Exception {
    byte[] csvData = Files.readAllBytes(Paths.get(PATH_TO_CSV_WITH_WRONG_DATEFORMAT));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value(ERROR_MESSAGE_VALIDATION))
        .andExpect(jsonPath("$.title").value(ERROR_MESSAGE_INVALID_INPUT))
        .andExpect(jsonPath("$.errorReport").isArray())
        .andExpect(jsonPath("$.errorReport", hasSize(1)))
        .andExpect(
            jsonPath("$.errorReport[0].msg")
                .value(CONCAT_ERROR_DATE.formatted(1, "DATE_OF_RECEIVING", "2023/03/03")))
        .andExpect(jsonPath("$.errorReport[0].errorCode").value(ErrorCode.DATE_FORMAT.name()))
        .andExpect(jsonPath("$.errorReport[0].rowNumber").value(1))
        .andExpect(jsonPath("$.errorReport[0].foundValue").value("2023/03/03"))
        .andExpect(jsonPath("$.errorReport[0].columnName").value("DATE_OF_RECEIVING"));
  }

  @Test
  void shouldReturnBadRequestOnNoSupportedFileFormat() throws Exception {
    byte[] csvData = Files.readAllBytes(Paths.get(PATH_TO_CSV_WITH_INVALID_FORMAT));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value(ERROR_MESSAGE_INVALID_INPUT))
        .andExpect(jsonPath("$.detail").value(ERROR_MESSAGE_VALIDATION))
        .andExpect(jsonPath("$.errorReport", hasSize(1)))
        .andExpect(
            jsonPath("$.errorReport[0].msg")
                .value(messageSourceWrapper.getMessage(ERROR_INVALID_FILE_FORMAT)))
        .andExpect(
            jsonPath("$.errorReport[0].errorCode").value(ErrorCode.INVALID_FILE_FORMAT.name()))
        .andExpect(jsonPath("$.errorReport[0].rowNumber").value(0));
  }

  @Test
  void shouldReturnBadRequestOnCorruptedCsv() throws Exception {
    byte[] csvData = Files.readAllBytes(Paths.get(PATH_TO_CORRUPTED_CSV));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value(ERROR_MESSAGE_VALIDATION))
        .andExpect(jsonPath("$.title").value(ERROR_MESSAGE_INVALID_INPUT))
        .andExpect(jsonPath("$.errorReport").isArray())
        .andExpect(jsonPath("$.errorReport", hasSize(3)))
        .andExpect(
            jsonPath("$.errorReport[0].msg")
                .value(ERROR_MESSAGE_INCONSISTENT_ROW_LENGTH.formatted(2)))
        .andExpect(
            jsonPath("$.errorReport[0].errorCode").value(ErrorCode.INCONSISTENT_ROW_LENGTH.name()))
        .andExpect(
            jsonPath("$.errorReport[1].msg")
                .value(ERROR_MESSAGE_INCONSISTENT_ROW_LENGTH.formatted(3)))
        .andExpect(
            jsonPath("$.errorReport[1].errorCode").value(ErrorCode.INCONSISTENT_ROW_LENGTH.name()))
        .andExpect(
            jsonPath("$.errorReport[2].msg")
                .value(ERROR_MESSAGE_INCONSISTENT_ROW_LENGTH.formatted(4)))
        .andExpect(
            jsonPath("$.errorReport[2].errorCode").value(ErrorCode.INCONSISTENT_ROW_LENGTH.name()));
  }

  @Test
  void shouldReturnBadRequestMixedErrorsCsv() throws Exception {
    byte[] csvData = Files.readAllBytes(Paths.get(PATH_TO_MIXED_ERRORS_CSV));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value(ERROR_MESSAGE_VALIDATION))
        .andExpect(jsonPath("$.title").value(ERROR_MESSAGE_INVALID_INPUT))
        .andExpect(jsonPath("$.errorReport").isArray())
        .andExpect(jsonPath("$.errorReport", hasSize(6)))
        .andExpect(jsonPath("$.errorReport[?(@.errorCode == 'REQUIRED_FIELD')]").value(hasSize(2)))
        .andExpect(jsonPath("$.errorReport[?(@.errorCode == 'UNIQUE_FILENAME')]").value(hasSize(1)))
        .andExpect(jsonPath("$.errorReport[?(@.errorCode == 'DATE_FORMAT')]").value(hasSize(1)))
        .andExpect(
            jsonPath("$.errorReport[?(@.errorCode == 'PRIME_DIAGNOSTIC_LAB')]").value(hasSize(1)))
        .andExpect(jsonPath("$.errorReport[?(@.errorCode == 'SEQUENCING_LAB')]").value(hasSize(1)));
  }

  @Test
  void shouldReturnBadRequestOnCsvWithInvalidPrimeDiagnosticLab() throws Exception {
    byte[] csvData = Files.readAllBytes(Paths.get(PATH_TO_CSV_WITH_INVALID_PRIME_DIAGNOSTIC_LAB));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value(ERROR_MESSAGE_VALIDATION))
        .andExpect(jsonPath("$.title").value(ERROR_MESSAGE_INVALID_INPUT))
        .andExpect(jsonPath("$.errorReport").isArray())
        .andExpect(jsonPath("$.errorReport", hasSize(2)))
        .andExpect(
            jsonPath("$.errorReport[0].msg")
                .value(CONCAT_REQUIRED.formatted(2, "PRIME_DIAGNOSTIC_LAB.NAME")))
        .andExpect(jsonPath("$.errorReport[0].errorCode").value(ErrorCode.REQUIRED_FIELD.name()))
        .andExpect(jsonPath("$.errorReport[1].msg").value(CONCAT_LAB.formatted(2)))
        .andExpect(
            jsonPath("$.errorReport[1].errorCode").value(ErrorCode.PRIME_DIAGNOSTIC_LAB.name()));
  }

  @Test
  void shouldReturnBadRequestOnCsvWithDuplicateFilenames() throws Exception {
    byte[] csvData = Files.readAllBytes(Paths.get(PATH_TO_CSV_WITH_DUPLICATE_FILE_NAMES));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value(ERROR_MESSAGE_VALIDATION))
        .andExpect(jsonPath("$.title").value(ERROR_MESSAGE_INVALID_INPUT))
        .andExpect(jsonPath("$.errorReport").isArray())
        .andExpect(jsonPath("$.errorReport", hasSize(1)))
        .andExpect(jsonPath("$.errorReport[0].errorCode").value(ErrorCode.UNIQUE_FILENAME.name()))
        .andExpect(
            jsonPath("$.errorReport[0].msg")
                .value(CONCAT_DUPLICATE.formatted(2, "Sample12347_R1.fastq")));
  }

  @Test
  @SneakyThrows
  void shouldRecognizeIllegalHeaderAsInvalidCsv() {
    final String pathToCsv = "src/test/resources/testdata/igs-batch-fasta-illegal-header.csv";
    final byte[] csvData = Files.readAllBytes(Paths.get(pathToCsv));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value(ERROR_MESSAGE_INVALID_INPUT))
        .andExpect(jsonPath("$.detail").value(ERROR_MESSAGE_VALIDATION))
        .andExpect(jsonPath("$.errorReport", hasSize(1)))
        .andExpect(
            jsonPath("$.errorReport[0].msg")
                .value(messageSourceWrapper.getMessage((ERROR_INVALID_FILE_FORMAT))))
        .andExpect(
            jsonPath("$.errorReport[0].errorCode").value(ErrorCode.INVALID_FILE_FORMAT.name()))
        .andExpect(jsonPath("$.errorReport[0].rowNumber").value(0));
  }

  @Test
  @SneakyThrows
  void shouldAddIllegalHeaderAndMissingRequiredValueToErrorReport() {
    final String pathToCsv =
        "src/test/resources/testdata/igs-batch-fasta-illegal-header-and-missing-required-value.csv";
    final byte[] csvData = Files.readAllBytes(Paths.get(pathToCsv));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value(ERROR_MESSAGE_VALIDATION))
        .andExpect(jsonPath("$.title").value(ERROR_MESSAGE_INVALID_INPUT))
        .andExpect(jsonPath("$.errorReport").isArray())
        .andExpect(jsonPath("$.errorReport", hasSize(2)))
        .andExpect(jsonPath("$.errorReport[?(@.errorCode == 'REQUIRED_FIELD')]").value(hasSize(1)))
        .andExpect(jsonPath("$.errorReport[?(@.errorCode == 'HEADER')]").value(hasSize(1)));
  }

  @Test
  @SneakyThrows
  void shouldAddLoincMappingToErrorReport() {
    final String pathToCsv = "src/test/resources/testdata/igs-batch-fasta-unknown-loinc.csv";
    final byte[] csvData = Files.readAllBytes(Paths.get(pathToCsv));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value(ERROR_MESSAGE_VALIDATION))
        .andExpect(jsonPath("$.title").value(ERROR_MESSAGE_INVALID_INPUT))
        .andExpect(jsonPath("$.errorReport").isArray())
        .andExpect(jsonPath("$.errorReport", hasSize(2)))
        .andExpect(jsonPath("$.errorReport[?(@.errorCode == 'LOINC_CODE')]").value(hasSize(2)));
  }

  @Test
  @SneakyThrows
  void shouldWorkCorrectlyOnCommaSeperatedCsv() {
    final String pathToCsv = "src/test/resources/testdata/igs-batch-fasta-commaCsv.csv";
    final byte[] csvData = Files.readAllBytes(Paths.get(pathToCsv));
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/csv/upload").file("csvFile", csvData))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value(ERROR_MESSAGE_VALIDATION))
        .andExpect(jsonPath("$.title").value(ERROR_MESSAGE_INVALID_INPUT))
        .andExpect(jsonPath("$.errorReport").isArray())
        .andExpect(jsonPath("$.errorReport", hasSize(76)))
        .andExpect(jsonPath("$.errorReport[?(@.errorCode == 'REQUIRED_FIELD')]").value(hasSize(25)))
        .andExpect(jsonPath("$.errorReport[?(@.errorCode == 'HEADER')]").value(hasSize(51)));
  }
}
