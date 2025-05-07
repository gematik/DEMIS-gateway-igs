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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.igs.gateway.IgsOverviewCsvTestData;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class OverviewDataExtractorTest {

  private static final String PATH_TO_CSV =
      "src/test/resources/testdata/igs-batch-fastq-testdata.csv";

  @Test
  @SneakyThrows
  void shouldBeAbleToHandleEmptyInput() {
    OverviewDataExtractor overviewDataExtractor = new OverviewDataExtractor();
    List<IgsOverviewCsv> overviewData = overviewDataExtractor.extractOverviewData("");
    assertThat(overviewData).isNotNull().isEmpty();
  }

  @Test
  @SneakyThrows
  void shouldExtractOverviewData() {
    OverviewDataExtractor overviewDataExtractor = new OverviewDataExtractor();
    String csvData = new String(Files.readAllBytes(Paths.get(PATH_TO_CSV)));
    List<IgsOverviewCsv> overviewData = overviewDataExtractor.extractOverviewData(csvData);
    assertThat(overviewData).isNotNull().hasSize(3);
    List<IgsOverviewCsv> expectedData = IgsOverviewCsvTestData.determineTestOverviewData();
    assertThat(overviewData.get(0)).isEqualTo(expectedData.get(0));
    assertThat(overviewData.get(1)).isEqualTo(expectedData.get(1));
    assertThat(overviewData.get(2)).isEqualTo(expectedData.get(2));
  }
}
