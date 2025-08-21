package de.gematik.demis.igs.gateway.communication;

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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.gematik.demis.igs.gateway.csv.futs.CodeDisplay;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "futs",
    configuration = FutsClientConfiguration.class,
    url = "${igs-gateway.client.futs.address}${igs-gateway.client.futs.context-path}")
public interface FutsClient {

  @GetMapping(value = "ValueSet", produces = APPLICATION_JSON_VALUE)
  List<CodeDisplay> getConceptMap(@RequestParam("system") String systemName);

  @GetMapping(value = "ValueSet", produces = APPLICATION_JSON_VALUE)
  List<String> getValueSetUrls();
}
