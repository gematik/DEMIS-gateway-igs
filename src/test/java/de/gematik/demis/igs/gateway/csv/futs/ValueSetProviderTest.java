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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.gematik.demis.igs.gateway.communication.FutsClient;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValueSetProviderTest {

  private final String URL1 = "http://test.de/test";
  private final String URL2 = "http://test.de/atest";
  private final String URL3 = "http://test.de/acodeSystem";
  private final String URL4 = "http://test.de/codeSystem";
  private List<String> urlList = List.of(URL1, URL2, URL3, URL4);

  private FutsClient futsClient;
  private ValueSetProvider underTest;

  @BeforeEach
  void init() {
    futsClient = mock(FutsClient.class);
    when(futsClient.getValueSetUrls()).thenReturn(urlList);
    underTest = spy(new ValueSetProvider(futsClient));
  }

  @Test
  @SneakyThrows
  void shouldSaveValueSetUrlsCorrectly() {
    assertThat(underTest.getValueSetUrl("test")).isEqualTo(URL1);
    assertThat(underTest.getValueSetUrl("atest")).isEqualTo(URL2);
    assertThat(underTest.getValueSetUrl("acodeSystem")).isEqualTo(URL3);
    assertThat(underTest.getValueSetUrl("codeSystem")).isEqualTo(URL4);
    verify(underTest, times(1)).init();
  }

  @Test
  @SneakyThrows
  void shouldCallInitIfNoValueFound() {
    underTest.getValueSetUrl("notExistent");
    underTest.getValueSetUrl("notExistent");
    verify(underTest, times(2)).init();
  }
}
