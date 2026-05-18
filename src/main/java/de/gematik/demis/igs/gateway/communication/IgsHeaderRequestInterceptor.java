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

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import de.gematik.demis.service.base.feign.HeadersForwardingRequestInterceptor;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class IgsHeaderRequestInterceptor implements RequestInterceptor {

  public static final String FHIR_PACKAGE_HEADER = "x-fhir-package";
  public static final String FHIR_PROFILE_HEADER = "x-fhir-profile";
  public static final String FHIR_PACKAGE_VERSION_HEADER = "x-fhir-package-version";

  private final HeadersForwardingRequestInterceptor headerForwardingRequestInterceptor =
      new HeadersForwardingRequestInterceptor();

  @Value("${igs.profile.version}")
  private final String igsProfileVersion;

  @Value("${igs.package.name}")
  private final String igsPackageName;

  @Override
  public void apply(RequestTemplate template) {
    // copy headers from incoming request if available
    headerForwardingRequestInterceptor.apply(template);

    if (!template.headers().containsKey(FHIR_PACKAGE_HEADER)) {
      template.header(FHIR_PACKAGE_HEADER, igsPackageName);
    }
    if (!template.headers().containsKey(FHIR_PROFILE_HEADER)) {
      template.header(FHIR_PROFILE_HEADER, igsPackageName);
    }
    if (igsProfileVersion != null && !template.headers().containsKey(FHIR_PACKAGE_VERSION_HEADER)) {
      template.header(FHIR_PACKAGE_VERSION_HEADER, igsProfileVersion);
    }

    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      return;
    }
    HttpServletRequest request = attributes.getRequest();
    String authHeader = request.getHeader(AUTHORIZATION);
    if (authHeader != null) {
      template.header(AUTHORIZATION, authHeader);
    }
  }
}
