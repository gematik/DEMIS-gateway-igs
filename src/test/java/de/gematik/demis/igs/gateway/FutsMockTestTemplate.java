package de.gematik.demis.igs.gateway;

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
import static de.gematik.demis.igs.gateway.TestUtils.LIST_UPLOAD_STATUS_CODE_DISPLAY;
import static org.mockito.Mockito.lenient;

import de.gematik.demis.igs.gateway.communication.FutsClient;
import de.gematik.demis.igs.gateway.csv.futs.ValueSetMappingService.System;
import de.gematik.demis.igs.gateway.csv.validation.AbstractValidatorRuleTest;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class FutsMockTestTemplate extends AbstractValidatorRuleTest {

  @MockitoBean protected FutsClient mockFutsClient;

  @BeforeEach
  public void futsClient() {
    lenient()
        .when(mockFutsClient.getValueSetUrls())
        .thenReturn(Arrays.stream(System.values()).map(s -> s.valueSetName).toList());
    lenient()
        .when(mockFutsClient.getConceptMap(System.UPLOAD_STATUS.valueSetName))
        .thenReturn(LIST_UPLOAD_STATUS_CODE_DISPLAY);
    lenient()
        .when(mockFutsClient.getConceptMap(System.SEQUENCING_REASON.valueSetName))
        .thenReturn(LIST_SEQUENCING_REASON_CODE_DISPLAY);
  }
}
