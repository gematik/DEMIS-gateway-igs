package de.gematik.demis.igs.gateway.notification;

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

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.VersionInfos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CodeSystemVersions {

  @Value("${igs.code-systems.loinc.version}")
  private String loincVersion;

  @Value("${igs.code-systems.snomed.version}")
  private String snomedVersion;

  /**
   * Determines the versions of the code systems as required by the notification builder library.
   *
   * @return The versions of the code systems as required by the notification builder library.
   */
  @Bean
  public VersionInfos determineCodeSystemVersions() {
    return new VersionInfos(loincVersion, snomedVersion);
  }
}
