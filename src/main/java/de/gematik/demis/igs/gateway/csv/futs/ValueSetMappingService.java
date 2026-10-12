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
import de.gematik.demis.igs.gateway.configuration.FeatureFlags;
import de.gematik.demis.igs.gateway.csv.DataPreprocessingException;
import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Provides lookup and mapping of codes and display values for configured FHIR ValueSets.
 *
 * <p>The ValueSet contents are loaded from FUTS, cached locally, and refreshed periodically.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ValueSetMappingService {

  private final Map<IgsValueSet, ReloadableCache> systemCacheMap = new EnumMap<>(IgsValueSet.class);
  private final FutsClient futsClient;
  private final ValueSetProvider valueSetProvider;
  private final FeatureFlags featureFlags;

  @PostConstruct
  void initValueSet() {
    EnumSet.allOf(IgsValueSet.class).forEach(this::initValueSet);
    refreshCachedValues();
  }

  private void initValueSet(IgsValueSet valueSet) {
    final Supplier<List<CodeDisplay>> cacheValuesSupplier;
    if (featureFlags.isFutsStaticResourceUrls()) {
      log.info("Using static ValueSet URL for {} from FUTS: {}", valueSet, valueSet.url);
      cacheValuesSupplier = () -> futsClient.getConceptMap(valueSet.url);
    } else {
      log.info(
          "Using dynamic ValueSet URL resolving from FUTS via ValueSetProvider for {}", valueSet);
      cacheValuesSupplier =
          () -> futsClient.getConceptMap(valueSetProvider.getValueSetUrl(valueSet.valueSetName));
    }
    systemCacheMap.put(valueSet, new ReloadableCache(valueSet.valueSetName, cacheValuesSupplier));
  }

  /**
   * Checks if the given code or display string is present in the specified system's value set.
   *
   * <p>This method searches for the code or display string in the cache associated with the
   * specified system. It first checks if the exact code is present, and if not, it searches for a
   * display string that starts with the given prefix, ignoring case.
   *
   * @param igsValueSet the system whose value set is to be checked
   * @param codeOrDisplayString the code or display string to be checked
   * @return true if the code or display string is present in the value set, false otherwise
   */
  public boolean valueSetContains(final IgsValueSet igsValueSet, final String codeOrDisplayString) {
    ReloadableCache cache = systemCacheMap.get(igsValueSet);
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
   * @param igsValueSet the system whose value set is to be checked
   * @param codeOrIncompleteDisplay the code or incomplete display string to be checked
   * @return the code if found, otherwise throws a DataPreprocessingException
   * @throws DataPreprocessingException if the code or display string is not found in the value set
   */
  public String getCodeFromCodeOrIncompleteDisplay(
      final IgsValueSet igsValueSet, final String codeOrIncompleteDisplay) {
    if (codeOrIncompleteDisplay.isBlank()) {
      return "";
    }
    ReloadableCache cache = systemCacheMap.get(igsValueSet);
    return cache
        .getByCode(codeOrIncompleteDisplay)
        .or(() -> searchByPrefixIgnoreCase(cache, codeOrIncompleteDisplay))
        .orElseThrow(
            () ->
                new DataPreprocessingException(
                    format(
                        "Could not find valid code system for %s '%s'",
                        igsValueSet, codeOrIncompleteDisplay),
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
}
