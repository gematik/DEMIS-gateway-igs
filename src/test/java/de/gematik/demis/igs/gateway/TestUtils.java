package de.gematik.demis.igs.gateway;

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

import de.gematik.demis.igs.gateway.csv.futs.CodeDisplay;
import java.util.List;

public class TestUtils {

  public static final String UPLOAD_STATUS_ACCEPTED = "Accepted (qualifier value)";
  public static final String UPLOAD_STATUS_ACCEPTED_CODE = "385645004";
  public static final String UPLOAD_STATUS_OTHER = "Other (qualifier value)";
  public static final String UPLOAD_STATUS_OTHER_CODE = "74964007";
  public static final String UPLOAD_STATUS_PLANNED = "Planned (qualifier value)";
  public static final String UPLOAD_STATUS_PLANNED_CODE = "397943006";
  public static final String UPLOAD_STATUS_DENIED = "Denied (qualifier value)";
  public static final String UPLOAD_STATUS_DENIED_CODE = "441889009";

  public static final String SEQUENCING_REASON_RANDOM = "Random (qualifier value)";
  public static final String SEQUENCING_REASON_RANDOM_CODE = "255226008";
  public static final String SEQUENCING_REASON_REQUESTED = "Requested (qualifier value)";
  public static final String SEQUENCING_REASON_REQUESTED_CODE = "385644000";
  public static final String SEQUENCING_REASON_OTHER = "Other (qualifier value)";
  public static final String SEQUENCING_REASON_OTHER_CODE = "74964007";
  public static final String SEQUENCING_REASON_CLINICAL = "Clinical (qualifier value)";
  public static final String SEQUENCING_REASON_CLINICAL_CODE = "58147004";
  public static final String CODE_SYSTEM = "http://snomed.info/sct";

  public static List<CodeDisplay> LIST_UPLOAD_STATUS_CODE_DISPLAY =
      List.of(
          new CodeDisplay(UPLOAD_STATUS_ACCEPTED_CODE, UPLOAD_STATUS_ACCEPTED, CODE_SYSTEM),
          new CodeDisplay(UPLOAD_STATUS_OTHER_CODE, UPLOAD_STATUS_OTHER, CODE_SYSTEM),
          new CodeDisplay(UPLOAD_STATUS_PLANNED_CODE, UPLOAD_STATUS_PLANNED, CODE_SYSTEM),
          new CodeDisplay(UPLOAD_STATUS_DENIED_CODE, UPLOAD_STATUS_DENIED, CODE_SYSTEM));
  public static List<CodeDisplay> LIST_SEQUENCING_REASON_CODE_DISPLAY =
      List.of(
          new CodeDisplay(SEQUENCING_REASON_CLINICAL_CODE, SEQUENCING_REASON_CLINICAL, CODE_SYSTEM),
          new CodeDisplay(SEQUENCING_REASON_OTHER_CODE, SEQUENCING_REASON_OTHER, CODE_SYSTEM),
          new CodeDisplay(SEQUENCING_REASON_RANDOM_CODE, SEQUENCING_REASON_RANDOM, CODE_SYSTEM),
          new CodeDisplay(
              SEQUENCING_REASON_REQUESTED_CODE, SEQUENCING_REASON_REQUESTED, CODE_SYSTEM));
}
