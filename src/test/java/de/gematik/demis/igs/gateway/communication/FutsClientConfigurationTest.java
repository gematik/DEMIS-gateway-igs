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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {FutsClientConfigurationTest.TestConfig.class})
@TestPropertySource(properties = "feature.flag.new.api.endpoints=true")
class FutsClientConfigurationTest {

  @Configuration
  static class TestConfig {
    @Bean
    public FutsClientConfiguration futsClientConfiguration() {
      return new FutsClientConfiguration();
    }
  }

  @Autowired private FutsClientConfiguration configuration;

  @Test
  void shouldSetNewApiHeaderWhenFeatureFlagIsEnabled() {
    // given
    RequestTemplate template = mock(RequestTemplate.class);

    // when
    RequestInterceptor interceptor = configuration.requestInterceptor();
    interceptor.apply(template);

    // then
    verify(template).header("x-fhir-profile", "igs-profile-snapshots");
  }
}
