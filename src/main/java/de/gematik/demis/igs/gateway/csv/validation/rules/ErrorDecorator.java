package de.gematik.demis.igs.gateway.csv.validation.rules;

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

import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorMessage;

public interface ErrorDecorator {
  default ValidationError createError(IgsOverviewCsv csvRow, String message, ErrorCode errorCode) {
    return createError(csvRow, message, null, null, errorCode);
  }

  default ValidationError createError(
      IgsOverviewCsv csvRow, String message, String columnName, ErrorCode errorCode) {
    return createError(csvRow, message, null, columnName, errorCode);
  }

  default ValidationError createError(
      IgsOverviewCsv csvRow,
      String message,
      String foundValue,
      String columnName,
      ErrorCode errorCode) {
    return ValidationError.builder()
        .msg((ErrorMessage.PREFIX.msg() + message).formatted(csvRow.getRowNumber()))
        .rowNumber(csvRow.getRowNumber())
        .errorCode(errorCode)
        .foundValue(foundValue)
        .columnName(columnName)
        .build();
  }
}
