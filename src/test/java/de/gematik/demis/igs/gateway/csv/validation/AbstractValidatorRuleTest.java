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

import de.gematik.demis.igs.gateway.IgsOverviewCsvTestData;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv.IgsOverviewCsvBuilder;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractValidatorRuleTest {

  IgsOverviewCsvBuilder firstRowBuilder;
  IgsOverviewCsvBuilder secondRowBuilder;
  IgsOverviewCsvBuilder thirdRowBuilder;

  @BeforeEach
  void beforeEach() {
    firstRowBuilder = IgsOverviewCsvTestData.firstRow;
    secondRowBuilder = IgsOverviewCsvTestData.secondRow;
    thirdRowBuilder = IgsOverviewCsvTestData.thirdRow;
  }
}
