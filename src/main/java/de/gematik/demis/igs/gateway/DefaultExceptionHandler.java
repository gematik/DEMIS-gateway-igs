package de.gematik.demis.igs.gateway;

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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import de.gematik.demis.igs.gateway.csv.CsvParseException;
import de.gematik.demis.igs.gateway.csv.InvalidInputDataException;
import de.gematik.demis.igs.gateway.notification.InvalidProfileDataException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
/** Maps exceptions to HTTP responses according to RFC 9457. */
public class DefaultExceptionHandler {

  public static final String ERROR_MESSAGE_VALIDATION =
      "Error occurred during CSV validation. Please see errorReport";
  public static final String ERROR_MESSAGE_CSV_PARSING_ERROR = "CSV Parsing Error";
  public static final String ERROR_MESSAGE_INVALID_INPUT = "Ungültige Eingabe-Daten";
  public static final String ERROR_MESSAGE_MISSING_PARAMETER = "Fehlender Request Parameter";
  private static final String LOG_ERROR_MESSAGE = "An exception occurred: ";

  /**
   * Handling all unexpected errors.
   *
   * @param exception The thrown exception
   * @return The ResponseEntity that should be returned to the user
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ResponseEntity<ProblemDetail> serverError(Exception exception) {
    log.error(LOG_ERROR_MESSAGE, exception);
    ProblemDetail problemDetail = ProblemDetail.forStatus(INTERNAL_SERVER_ERROR);
    problemDetail.setTitle("Interner Serverfehler");
    problemDetail.setDetail("An unexpected error occurred");
    return ResponseEntity.internalServerError().body(problemDetail);
  }

  /**
   * Handling errors related to CSV parsing.
   *
   * @param exception The thrown exception
   * @return The ResponseEntity that should be returned to the user
   */
  @ExceptionHandler(CsvParseException.class)
  @ResponseStatus(BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ProblemDetail> csvParseError(Exception exception) {
    return generateBadRequestWithTitle(exception, ERROR_MESSAGE_CSV_PARSING_ERROR);
  }

  /**
   * Handling errors related to bad user input.
   *
   * @param exception The thrown exception
   * @return The ResponseEntity that should be returned to the user
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ProblemDetail> missingParameterError(
      MissingServletRequestParameterException exception) {
    return generateBadRequestWithTitle(exception, ERROR_MESSAGE_MISSING_PARAMETER);
  }

  /**
   * Handling errors related to generating FHIR resources from user input.
   *
   * @param exception The thrown exception
   * @return The ResponseEntity that should be returned to the user
   */
  @ExceptionHandler(InvalidProfileDataException.class)
  @ResponseStatus(BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ProblemDetail> invalidDataForFhirResource(
      InvalidProfileDataException exception) {
    return generateBadRequestWithTitle(exception, ERROR_MESSAGE_INVALID_INPUT);
  }

  private ResponseEntity<ProblemDetail> generateBadRequestWithTitle(
      Exception exception, String title) {
    log.info(LOG_ERROR_MESSAGE, exception);
    ProblemDetail problemDetail = ProblemDetail.forStatus(BAD_REQUEST);
    problemDetail.setTitle(title);
    problemDetail.setDetail(exception.getMessage());
    return ResponseEntity.badRequest().body(problemDetail);
  }

  /**
   * Handling errors related to invalid user input.
   *
   * @param exception The thrown exception
   * @return The ResponseEntity that should be returned to the user
   */
  @ExceptionHandler(InvalidInputDataException.class)
  @ResponseStatus(BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ProblemDetail> invalidInputDataException(
      InvalidInputDataException exception) {
    log.info(LOG_ERROR_MESSAGE, exception);
    ProblemDetail problemDetail = ProblemDetail.forStatus(BAD_REQUEST);
    problemDetail.setTitle(ERROR_MESSAGE_INVALID_INPUT);
    problemDetail.setDetail(ERROR_MESSAGE_VALIDATION);
    problemDetail.setProperties(Map.of("errorReport", exception.getErrors()));
    return ResponseEntity.badRequest().body(problemDetail);
  }

  /**
   * Handling all problems that extend ErrorResponseException.
   *
   * @param ex Problem that is instance of ErrorResponseException
   * @return Error Response Entity in Problem format
   */
  @ExceptionHandler(ErrorResponseException.class)
  public ResponseEntity<ProblemDetail> handleNotificationSendException(ErrorResponseException ex) {
    return new ResponseEntity<>(ex.getBody(), ex.getStatusCode());
  }
}
