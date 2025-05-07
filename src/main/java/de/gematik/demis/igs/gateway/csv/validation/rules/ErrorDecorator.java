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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_PREFIX;

import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.configuration.MessagesProperties;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;

public interface ErrorDecorator {

  default ValidationError createError(
      IgsOverviewCsv csvRow,
      ErrorCode errorCode,
      MessageSourceWrapper messageSourceWrapper,
      MessagesProperties messagesProperties,
      String... formatArgs) {
    return createError(
        csvRow, null, null, errorCode, messageSourceWrapper, messagesProperties, formatArgs);
  }

  default ValidationError createError(
      IgsOverviewCsv csvRow,
      String columnName,
      ErrorCode errorCode,
      MessageSourceWrapper messageSourceWrapper,
      MessagesProperties messagesProperties,
      String... formatArgs) {
    return createError(
        csvRow, null, columnName, errorCode, messageSourceWrapper, messagesProperties, formatArgs);
  }

  default ValidationError createError(
      IgsOverviewCsv csvRow,
      String foundValue,
      String columnName,
      ErrorCode errorCode,
      MessageSourceWrapper messageSourceWrapper,
      MessagesProperties messagesProperties,
      String... formatArgs) {
    String fullMessage =
        createMessage(csvRow, messageSourceWrapper, messagesProperties, formatArgs);

    return ValidationError.builder()
        .msg(fullMessage)
        .rowNumber(csvRow.getRowNumber())
        .errorCode(errorCode)
        .foundValue(foundValue)
        .columnName(columnName)
        .build();
  }

  private static String createMessage(
      IgsOverviewCsv csvRow,
      MessageSourceWrapper messageSourceWrapper,
      MessagesProperties messagesProperties,
      String[] formatArgs) {
    String errorMessage;
    if (formatArgs != null && formatArgs.length > 0) {
      errorMessage = messageSourceWrapper.getMessage(messagesProperties, formatArgs);
    } else {
      errorMessage = messageSourceWrapper.getMessage(messagesProperties);
    }
    return messageSourceWrapper.getMessage(ERROR_PREFIX, String.valueOf(csvRow.getRowNumber()))
        + errorMessage;
  }
}
