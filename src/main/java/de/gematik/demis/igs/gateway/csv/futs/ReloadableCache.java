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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** This class provides a cache for the code display values */
@RequiredArgsConstructor
@Slf4j
class ReloadableCache {

  private final String name;
  private final Supplier<List<CodeDisplay>> cacheValuesSupplier;

  private List<CodeDisplay> list = new CopyOnWriteArrayList<>();

  /**
   * Returns the display for the given code
   *
   * @param code
   * @return
   */
  public Optional<CodeDisplay> getByCode(final String code) {
    checkLoaded();
    return list.stream().filter(cd -> cd.code().equals(code)).findFirst();
  }

  /**
   * Returns all code displays in the cache after checking if the cache is loaded
   *
   * @return
   */
  public List<CodeDisplay> getAll() {
    checkLoaded();
    return list;
  }

  /** Loads the cache with the values from the supplier */
  public void loadCache() {
    try {
      final var newCodeDisplayList = cacheValuesSupplier.get();
      if (newCodeDisplayList != null && !newCodeDisplayList.isEmpty()) {
        list = newCodeDisplayList;
        log.info("{} value set cache (re)loaded. # entries = {}", name, newCodeDisplayList.size());
      }
    } catch (final Exception e) {
      log.error("error fetching code map for {}", name, e);
    }
  }

  private void checkLoaded() {
    if (list == null || list.isEmpty()) {
      loadCache();
    }
  }
}
