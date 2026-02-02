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

import de.gematik.demis.igs.gateway.communication.FutsClient;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class requests all ValueSets on strat from the FUTS and provides the URLs for the ValueSets.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ValueSetProvider {

  private Map<String, String> valueSetUrls = new HashMap<>();

  private final FutsClient futsClient;

  /**
   * Initializes the ValueSetProvider. Creates a map with the system name as key and the URL as
   * value.
   */
  @PostConstruct
  public void init() {
    valueSetUrls =
        futsClient.getValueSetUrls().stream()
            .collect(
                Collectors.toMap(
                    url -> Arrays.stream(url.split("/")).toList().getLast(), url -> url));
    log.info("ValueSetProvider initialized with {} urls", valueSetUrls.size());
  }

  /**
   * Returns the URL for the ValueSet with the given system name.
   *
   * @param systemName
   * @return
   */
  public String getValueSetUrl(String systemName) {
    if (!valueSetUrls.containsKey(systemName)) {
      init();
    }
    return valueSetUrls.get(systemName);
  }
}
