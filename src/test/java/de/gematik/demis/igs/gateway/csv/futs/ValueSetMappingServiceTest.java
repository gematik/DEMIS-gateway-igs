package de.gematik.demis.igs.gateway.csv.futs;

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

import static de.gematik.demis.igs.gateway.TestUtils.LIST_SEQUENCING_REASON_CODE_DISPLAY;
import static de.gematik.demis.igs.gateway.TestUtils.LIST_UPLOAD_STATUS_CODE_DISPLAY;
import static de.gematik.demis.igs.gateway.TestUtils.SEQUENCING_REASON_CLINICAL_CODE;
import static de.gematik.demis.igs.gateway.TestUtils.SEQUENCING_REASON_OTHER_CODE;
import static de.gematik.demis.igs.gateway.TestUtils.SEQUENCING_REASON_RANDOM_CODE;
import static de.gematik.demis.igs.gateway.TestUtils.SEQUENCING_REASON_REQUESTED_CODE;
import static de.gematik.demis.igs.gateway.TestUtils.UPLOAD_STATUS_ACCEPTED_CODE;
import static de.gematik.demis.igs.gateway.TestUtils.UPLOAD_STATUS_DENIED_CODE;
import static de.gematik.demis.igs.gateway.TestUtils.UPLOAD_STATUS_OTHER_CODE;
import static de.gematik.demis.igs.gateway.TestUtils.UPLOAD_STATUS_PLANNED_CODE;
import static de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService.System.SEQUENCING_REASON;
import static de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService.System.UPLOAD_STATUS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

import de.gematik.demis.igs.gateway.communication.FutsClient;
import de.gematik.demis.igs.gateway.csv.DataPreprocessingException;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValueSetMappingServiceTest {

  @Mock private FutsClient futsClient;
  @Mock private ValueSetProvider valueSetProvider;
  private ValueSetMappingService underTest;

  public static Stream<Arguments> shouldReturnRightValuesForUploadStatus() {
    return Stream.of(
        Arguments.of("Other", UPLOAD_STATUS_OTHER_CODE),
        Arguments.of("oTHer", UPLOAD_STATUS_OTHER_CODE),
        Arguments.of(UPLOAD_STATUS_OTHER_CODE, UPLOAD_STATUS_OTHER_CODE),
        Arguments.of("Accepted", UPLOAD_STATUS_ACCEPTED_CODE),
        Arguments.of("accepTed", UPLOAD_STATUS_ACCEPTED_CODE),
        Arguments.of(UPLOAD_STATUS_ACCEPTED_CODE, UPLOAD_STATUS_ACCEPTED_CODE),
        Arguments.of("Planned", UPLOAD_STATUS_PLANNED_CODE),
        Arguments.of("pLannED", UPLOAD_STATUS_PLANNED_CODE),
        Arguments.of(UPLOAD_STATUS_PLANNED_CODE, UPLOAD_STATUS_PLANNED_CODE),
        Arguments.of("Denied", UPLOAD_STATUS_DENIED_CODE),
        Arguments.of("dEnIeD", UPLOAD_STATUS_DENIED_CODE),
        Arguments.of("", ""),
        Arguments.of(UPLOAD_STATUS_DENIED_CODE, UPLOAD_STATUS_DENIED_CODE));
  }

  public static Stream<Arguments> shouldReturnRightValuesForSequencingReason() {
    return Stream.of(
        Arguments.of("requested", SEQUENCING_REASON_REQUESTED_CODE),
        Arguments.of("REqueSted", SEQUENCING_REASON_REQUESTED_CODE),
        Arguments.of(SEQUENCING_REASON_REQUESTED_CODE, SEQUENCING_REASON_REQUESTED_CODE),
        Arguments.of("random", SEQUENCING_REASON_RANDOM_CODE),
        Arguments.of("RanDom", SEQUENCING_REASON_RANDOM_CODE),
        Arguments.of(SEQUENCING_REASON_RANDOM_CODE, SEQUENCING_REASON_RANDOM_CODE),
        Arguments.of("other", SEQUENCING_REASON_OTHER_CODE),
        Arguments.of("OTHer", SEQUENCING_REASON_OTHER_CODE),
        Arguments.of(SEQUENCING_REASON_OTHER_CODE, SEQUENCING_REASON_OTHER_CODE),
        Arguments.of("clinical", SEQUENCING_REASON_CLINICAL_CODE),
        Arguments.of("CliNIcal", SEQUENCING_REASON_CLINICAL_CODE),
        Arguments.of("", ""),
        Arguments.of(SEQUENCING_REASON_CLINICAL_CODE, SEQUENCING_REASON_CLINICAL_CODE));
  }

  @BeforeEach
  public void initMock() {
    lenient()
        .when(valueSetProvider.getValueSetUrl(UPLOAD_STATUS.valueSetName))
        .thenReturn(UPLOAD_STATUS.valueSetName);
    lenient()
        .when(valueSetProvider.getValueSetUrl(SEQUENCING_REASON.valueSetName))
        .thenReturn(SEQUENCING_REASON.valueSetName);
    lenient()
        .when(futsClient.getConceptMap(UPLOAD_STATUS.valueSetName))
        .thenReturn(LIST_UPLOAD_STATUS_CODE_DISPLAY);
    lenient()
        .when(futsClient.getConceptMap(SEQUENCING_REASON.valueSetName))
        .thenReturn(LIST_SEQUENCING_REASON_CODE_DISPLAY);
    underTest = new ValueSetMappingService(futsClient, valueSetProvider);
    underTest.refreshCachedValues();
  }

  @SneakyThrows
  @ParameterizedTest
  @MethodSource
  void shouldReturnRightValuesForUploadStatus(String valueOrCode, String expectedValue) {
    assertThat(underTest.getCodeFromCodeOrIncompleteDisplay(UPLOAD_STATUS, valueOrCode))
        .isEqualTo(expectedValue);
  }

  @ParameterizedTest
  @MethodSource({"shouldReturnRightValuesForUploadStatus"})
  void shouldReturnTrueForUploadStatus(String valueOrCode) {
    assertTrue(underTest.valueSetContains(UPLOAD_STATUS, valueOrCode));
  }

  @SneakyThrows
  @ParameterizedTest
  @MethodSource
  void shouldReturnRightValuesForSequencingReason(String valueOrCode, String expectedValue) {
    assertThat(underTest.getCodeFromCodeOrIncompleteDisplay(SEQUENCING_REASON, valueOrCode))
        .isEqualTo(expectedValue);
  }

  @ParameterizedTest
  @MethodSource({"shouldReturnRightValuesForSequencingReason"})
  void shouldReturnTrueForSequencingReason(String valueOrCode) {
    assertTrue(underTest.valueSetContains(SEQUENCING_REASON, valueOrCode));
  }

  @Test
  @SneakyThrows
  void shouldThrowExceptionIfNoValidCodeOrDisplayDelivered() {
    DataPreprocessingException ex =
        assertThrows(
            DataPreprocessingException.class,
            () -> underTest.getCodeFromCodeOrIncompleteDisplay(UPLOAD_STATUS, "notExisting"));
    assertThat(ex.getMessage())
        .contains("Could not find valid code system for UPLOAD_STATUS 'notExisting'");
  }

  @ParameterizedTest
  @EnumSource(ValueSetMappingService.System.class)
  void shouldReturnFalseForInvalidCodeOrDisplay(ValueSetMappingService.System system) {
    assertFalse(underTest.valueSetContains(system, "notExisting"));
  }
}
