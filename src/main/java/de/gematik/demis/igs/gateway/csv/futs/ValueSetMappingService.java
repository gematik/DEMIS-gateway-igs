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

import static java.lang.String.format;

import de.gematik.demis.igs.gateway.communication.FutsClient;
import de.gematik.demis.igs.gateway.csv.DataPreprocessingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/** This class provides the mapping of the ValueSets from the FUTS to the corresponding display */
@Service
@Slf4j
public class ValueSetMappingService {

  private final Map<System, ReloadableCache> systemCacheMap;

  ValueSetMappingService(final FutsClient futsClient, final ValueSetProvider valueSetProvider) {
    systemCacheMap = new HashMap<>();
    Arrays.stream(System.values())
        .forEach(
            system ->
                systemCacheMap.put(
                    system,
                    new ReloadableCache(
                        system.valueSetName,
                        () ->
                            futsClient.getConceptMap(
                                valueSetProvider.getValueSetUrl(system.valueSetName)))));
    refreshCachedValues();
  }

  /**
   * Checks if the given code or display string is present in the specified system's value set.
   *
   * <p>This method searches for the code or display string in the cache associated with the
   * specified system. It first checks if the exact code is present, and if not, it searches for a
   * display string that starts with the given prefix, ignoring case.
   *
   * @param system the system whose value set is to be checked
   * @param codeOrDisplayString the code or display string to be checked
   * @return true if the code or display string is present in the value set, false otherwise
   */
  public boolean valueSetContains(final System system, final String codeOrDisplayString) {
    ReloadableCache cache = systemCacheMap.get(system);
    return cache.getByCode(codeOrDisplayString).isPresent()
        || searchByPrefixIgnoreCase(cache, codeOrDisplayString).isPresent();
  }

  /**
   * Returns the code for the given code or incomplete display string from the specified system's
   * value set.
   *
   * <p>If the code or incomplete display string is blank, an empty string is returned. Otherwise,
   * the method searches for the exact code in the cache associated with the specified system. If
   * the exact code is not found, it searches for a display string that starts with the given
   * prefix, ignoring case.
   *
   * @param system the system whose value set is to be checked
   * @param codeOrIncompleteDisplay the code or incomplete display string to be checked
   * @return the code if found, otherwise throws a DataPreprocessingException
   * @throws DataPreprocessingException if the code or display string is not found in the value set
   */
  public String getCodeFromCodeOrIncompleteDisplay(
      final System system, final String codeOrIncompleteDisplay) {
    if (codeOrIncompleteDisplay.isBlank()) {
      return "";
    }
    ReloadableCache cache = systemCacheMap.get(system);
    return cache
        .getByCode(codeOrIncompleteDisplay)
        .or(() -> searchByPrefixIgnoreCase(cache, codeOrIncompleteDisplay))
        .orElseThrow(
            () ->
                new DataPreprocessingException(
                    format(
                        "Could not find valid code system for %s '%s'",
                        system, codeOrIncompleteDisplay),
                    null))
        .code();
  }

  private Optional<CodeDisplay> searchByPrefixIgnoreCase(
      final ReloadableCache cache, final String prefix) {
    return cache.getAll().stream()
        .filter(code -> code.display().toLowerCase().startsWith(prefix.toLowerCase()))
        .findFirst();
  }

  @Scheduled(cron = "${igs-gateway.cache.futs.cron}")
  public void refreshCachedValues() {
    systemCacheMap.values().forEach(ReloadableCache::loadCache);
  }

  public enum System {
    UPLOAD_STATUS("uploadStatus"),
    SEQUENCING_REASON("sequencingReason");

    public final String valueSetName;

    System(final String name) {
      this.valueSetName = name;
    }
  }
}
