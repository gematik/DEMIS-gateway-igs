package de.gematik.demis.igs.gateway.csv.validation;

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

import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a validation error encountered during CSV processing.
 *
 * <p>This class encapsulates details about a validation error, including the error message, the
 * value that caused the error, the row number and column name where the error occurred, and the
 * specific error code.
 */
@Data
@Builder
public class ValidationError implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private String msg;
  private String foundValue;
  private long rowNumber;
  private String columnName;
  private ErrorCode errorCode;

  public enum ErrorCode {
    HEADER,
    DATE_FORMAT,
    REQUIRED_FIELD,
    PRIME_DIAGNOSTIC_LAB,
    SEQUENCING_LAB,
    UNIQUE_FILENAME,
    LOINC_CODE,
    INCONSISTENT_ROW_LENGTH,
    EMPTY_FILE,
    INVALID_FILE_FORMAT,
    UNKNOWN
  }
}
