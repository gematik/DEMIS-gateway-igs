package de.gematik.demis.igs.gateway.communication;

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

import static de.gematik.demis.igs.gateway.communication.IgsHeaderRequestInterceptor.*;
import static org.mockito.Mockito.*;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class IgsHeaderRequestInterceptorTest {

  @Test
  void shouldSetPackageHeadersIfNotPresent() {
    // given
    RequestTemplate template = mock(RequestTemplate.class);
    when(template.headers()).thenReturn(Map.of());

    // when
    RequestInterceptor interceptor = new IgsHeaderRequestInterceptor("v3", "igs-profile-snapshots");
    interceptor.apply(template);

    // then
    verify(template).header(FHIR_PROFILE_HEADER, "igs-profile-snapshots");
    verify(template).header(FHIR_PACKAGE_HEADER, "igs-profile-snapshots");
    verify(template).header(FHIR_PACKAGE_VERSION_HEADER, "v3");
  }

  @Test
  void shouldCopyPackageHeadersFromIncomingRequest() {
    // given
    RequestInterceptor interceptor = new IgsHeaderRequestInterceptor("v3", "igs-profile-snapshots");

    RequestTemplate template = mock(RequestTemplate.class);
    when(template.headers()).thenReturn(Map.of());

    // mock incoming request with relevant headers
    try (MockedStatic<RequestContextHolder> mocked = mockStatic(RequestContextHolder.class)) {
      ServletRequestAttributes requestAttributes = mock(ServletRequestAttributes.class);
      HttpServletRequest servletRequest = mock(HttpServletRequest.class);
      mocked.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
      when(requestAttributes.getRequest()).thenReturn(servletRequest);

      when(servletRequest.getHeader(FHIR_PROFILE_HEADER)).thenReturn("igs");
      when(servletRequest.getHeader(FHIR_PACKAGE_HEADER)).thenReturn("igs");
      when(servletRequest.getHeader(FHIR_PACKAGE_VERSION_HEADER)).thenReturn("v4");

      // when
      interceptor.apply(template);
    }

    // then
    verify(template).header(FHIR_PROFILE_HEADER, "igs");
    verify(template).header(FHIR_PACKAGE_HEADER, "igs");
    verify(template).header(FHIR_PACKAGE_VERSION_HEADER, "v4");
  }

  @Test
  void shouldKeepHeadersIfPresent() {
    // given
    RequestTemplate template = mock(RequestTemplate.class);
    when(template.headers())
        .thenReturn(
            Map.of(
                FHIR_PROFILE_HEADER, Set.of("custom-profile"),
                FHIR_PACKAGE_HEADER, Set.of("custom-package"),
                FHIR_PACKAGE_VERSION_HEADER, Set.of("custom-version")));

    // when
    RequestInterceptor interceptor = new IgsHeaderRequestInterceptor("v3", "igs-profile-snapshots");
    interceptor.apply(template);

    // then
    verify(template, never()).header(any(), anyString());
  }
}
