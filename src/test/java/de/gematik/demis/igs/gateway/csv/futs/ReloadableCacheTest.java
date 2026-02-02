package de.gematik.demis.igs.gateway.csv.futs;

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

import static de.gematik.demis.igs.gateway.TestUtils.LIST_SEQUENCING_REASON_CODE_DISPLAY;
import static de.gematik.demis.igs.gateway.TestUtils.SEQUENCING_REASON_CLINICAL;
import static de.gematik.demis.igs.gateway.TestUtils.SEQUENCING_REASON_CLINICAL_CODE;
import static de.gematik.demis.igs.gateway.TestUtils.SEQUENCING_REASON_OTHER;
import static de.gematik.demis.igs.gateway.TestUtils.SEQUENCING_REASON_OTHER_CODE;
import static de.gematik.demis.igs.gateway.TestUtils.SEQUENCING_REASON_RANDOM;
import static de.gematik.demis.igs.gateway.TestUtils.SEQUENCING_REASON_RANDOM_CODE;
import static de.gematik.demis.igs.gateway.TestUtils.SEQUENCING_REASON_REQUESTED;
import static de.gematik.demis.igs.gateway.TestUtils.SEQUENCING_REASON_REQUESTED_CODE;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ReloadableCacheTest {

  private final ReloadableCache underTest =
      new ReloadableCache("name", () -> LIST_SEQUENCING_REASON_CODE_DISPLAY);

  @ParameterizedTest
  @CsvSource({
    SEQUENCING_REASON_RANDOM_CODE + "," + SEQUENCING_REASON_RANDOM,
    SEQUENCING_REASON_REQUESTED_CODE + "," + SEQUENCING_REASON_REQUESTED,
    SEQUENCING_REASON_OTHER_CODE + "," + SEQUENCING_REASON_OTHER,
    SEQUENCING_REASON_CLINICAL_CODE + "," + SEQUENCING_REASON_CLINICAL
  })
  void shouldReturnCorrectDisplayByCode(String code, String expectedDisplay) {
    assertThat(underTest.getByCode(code).orElseThrow().display()).isEqualTo(expectedDisplay);
  }

  @Test
  void shouldReturnOptionalNullIfCodeNotFound() {
    assertThat(underTest.getByCode("notExistingCode")).isEmpty();
  }

  @Test
  void shouldReturnFullListOnGetAll() {
    assertThat(underTest.getAll()).containsExactlyElementsOf(LIST_SEQUENCING_REASON_CODE_DISPLAY);
  }
}
