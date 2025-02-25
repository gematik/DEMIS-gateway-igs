package de.gematik.demis.igs.gateway.notification;

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

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

@Slf4j
class NotificationSendException extends ErrorResponseException {

  static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

  public NotificationSendException(String problemDetail) {
    super(HTTP_STATUS, problemDetailFrom(problemDetail), null);
  }

  public NotificationSendException(Throwable throwable) {
    super(HTTP_STATUS, problemDetailFrom(throwable.getMessage()), throwable);
    log.error("An exception occurred: ", throwable);
  }

  private static ProblemDetail problemDetailFrom(String detail) {
    ProblemDetail problemDetail = ProblemDetail.forStatus(HTTP_STATUS);
    problemDetail.setTitle("Fehler beim Übermitteln der Meldung");
    problemDetail.setDetail(detail);
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }
}
