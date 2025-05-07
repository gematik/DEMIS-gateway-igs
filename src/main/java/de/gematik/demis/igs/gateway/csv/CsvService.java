package de.gematik.demis.igs.gateway.csv;

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

import de.gematik.demis.igs.gateway.DefaultExceptionHandler;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.validation.CsvContentValidator;
import de.gematik.demis.igs.gateway.csv.validation.CsvHeaderValidator;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Provides processing of CSV data.
 *
 * <p>This service class is responsible for handling CSV data, including validation of headers and
 * content, extraction of overview data, and preparation of the data for response. It uses various
 * validators and extractors to ensure the integrity and correctness of the CSV data.
 *
 * <p>Exception Handling:
 *
 * <ul>
 *   <li>Throws {@link InvalidInputDataException} if any validation errors are found
 * </ul>
 */
@Slf4j
@Component
@AllArgsConstructor
class CsvService {

  private OverviewDataExtractor overviewDataExtractor;
  private CsvDataGroomer csvDataGroomer;
  private CsvContentValidator csvContentValidator;
  private CsvHeaderValidator csvHeaderValidator;

  /**
   * Processes the CSV data and returns the overview data.
   *
   * <p>This method validates the CSV headers and its content, extracts the overview data, and
   * prepares it for response. If any validation errors are found, they are handled by passing them
   * to {@link InvalidInputDataException} which in turn is processed by the global exception handler
   * {@link DefaultExceptionHandler}.
   *
   * @param csv The CSV data as a string.
   * @return The list of {@link IgsOverviewCsv} containing the overview data.
   * @throws InvalidInputDataException if any validation errors are found in the CSV headers or its
   *     content.
   */
  List<IgsOverviewCsv> processCsv(String csv) {
    List<ValidationError> headerValidationErrors = csvHeaderValidator.validate(csv);
    List<IgsOverviewCsv> extractedData = overviewDataExtractor.extractOverviewData(csv);
    List<ValidationError> contentValidationErrors = csvContentValidator.validate(extractedData);
    handleValidationErrors(headerValidationErrors, contentValidationErrors);
    return csvDataGroomer.prepareForResponse(extractedData);
  }

  /**
   * Handles validation errors by combining csv-header and csv-content validation errors, sorting
   * them, and throwing an {@link InvalidInputDataException} if any errors are found, which in turn
   * is processed by the global exception handler {@link DefaultExceptionHandler}.
   *
   * <p>This method ensures that all validation errors are collected and processed together,
   * providing a comprehensive error report.
   *
   * @param headerErrors The list of header validation errors.
   * @param contentErrors The list of content validation errors.
   * @throws InvalidInputDataException if any validation errors are found.
   */
  private void handleValidationErrors(
      List<ValidationError> headerErrors, List<ValidationError> contentErrors) {
    List<ValidationError> allErrors =
        Stream.concat(headerErrors.stream(), contentErrors.stream())
            .sorted(Comparator.comparingLong(ValidationError::getRowNumber))
            .toList();
    if (!allErrors.isEmpty()) {
      throw new InvalidInputDataException(allErrors);
    }
  }
}
