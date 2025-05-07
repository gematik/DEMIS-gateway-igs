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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import lombok.Getter;

@Getter
public enum MessagesProperties {
  ERROR_EMPTY_FILE("error.empty.file"),
  ERROR_ILLEGAL_HEADER("error.illegal.header"),
  ERROR_MISSING_HEADER("error.missing.header"),
  ERROR_SEQUENCING_LAB("error.sequencing.lab"),
  ERROR_PRIME_DIAGNOSTIC_LAB("error.prime.diagnostic.lab"),
  ERROR_DUPLICATE_FILE_NAME("error.duplicateFileName"),
  ERROR_UNKNOWN("error.unknown"),
  ERROR_DATE_FORMAT("error.date.format"),
  ERROR_REQUIRED("error.required"),
  ERROR_PREFIX("error.prefix"),
  ERROR_LOINC_CODE("error.loinc.code"),
  ERROR_INVALID_FILE_FORMAT("error.invalid.file.format");

  private final String key;

  MessagesProperties(String key) {
    this.key = key;
  }
}
