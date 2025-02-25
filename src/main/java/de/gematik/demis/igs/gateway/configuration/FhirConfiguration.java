package de.gematik.demis.igs.gateway.configuration;

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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

/** Provides beans related to processing of FHIR resources. */
@Configuration
public class FhirConfiguration {

  /**
   * Generates a single instance of the FHIR context.
   *
   * @return The single instance of the FHIR context.
   */
  @Bean
  public FhirContext fhirContext() {
    return FhirContext.forR4Cached();
  }

  /**
   * Generates a FHIR parser for JSON.
   *
   * @return FHIR parser for JSON.
   */
  @Bean
  @RequestScope
  public IParser fhirJsonParser(FhirContext fhirContext) {
    return fhirContext.newJsonParser();
  }
}
