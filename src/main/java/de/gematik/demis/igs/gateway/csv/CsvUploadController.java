package de.gematik.demis.igs.gateway.csv;

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

import de.gematik.demis.igs.gateway.IgsGatewayException;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.model.OverviewParsedRowResult;
import de.gematik.demis.igs.gateway.csv.model.OverviewResponse;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class CsvUploadController {

  private CsvService csvService;
  private CsvBytesToStringConverter csvBytesToStringConverter;

  @PostMapping(path = "/csv/upload")
  public OverviewResponse uploadCsv(@RequestParam(name = "csvFile") MultipartFile csvFile)
      throws IgsGatewayException {
    try {
      String csvData = csvBytesToStringConverter.convert(csvFile.getBytes());
      List<IgsOverviewCsv> proceededCsvFile = csvService.processCsv(csvData);
      List<OverviewParsedRowResult> overviewData =
          proceededCsvFile.stream().map(o -> new OverviewParsedRowResult().setData(o)).toList();
      return new OverviewResponse().setItems(overviewData);
    } catch (IOException exception) {
      throw new IgsGatewayException(exception.getMessage(), exception);
    }
  }
}
