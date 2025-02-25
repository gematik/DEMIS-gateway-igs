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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/** Generates test data for IGS notifications. */
public class NotificationTestDataGenerator {

  public static final String MELDETATBESTAND = "cvdp";
  public static final String AUTHOR =
      "Ralf Dürrwald, Stephan Fuchs, Stefan Kroeger, Marianne Wedde, Oliver Drechsel, Aleksandar Radonic, Rene Kmiecinski, Thorsten Wolff";
  public static final String SEQUENCING_REASON = "255226008";
  public static final String FILE_ONE_ID = "ecd3f1f0-b6b6-46e0-b721-2d9869ab8195";
  public static final String NOTIFICATION_ID = "f8585efb-1872-4a4f-b88d-8c889e93487b";
  public static final String LAB_IDENTIFIER_VALUE = "A384";
  public static final String SEQUENCING_LAB_IDENTIFIER_VALUE = "10285";
  public static final String SEQUENCING_LAB_NAME =
      "Nationales Referenzzentrum für Influenza, FG17, RKI";
  public static final String SEQUENCING_LAB_ADDRESS_LINE = "Seestr. 10";
  public static final String SEQUENCING_LAB_ADDRESS_POSTAL_CODE = "13353";
  public static final String SEQUENCING_LAB_ADDRESS_CITY = "Berlin";
  public static final String SEQUENCING_LAB_ADDRESS_COUNTRY = "DE";
  public static final String SEQUENCING_LAB_EMAIL = "NRZ-Influenza@rki.de";
  public static final String REPOSITORY_EXTENSION_VALUE_DATE_TIME = "2023-02-10";
  public static final String REPOSITORY_EXTENSION_VALUE_STRING = "O Drechsel";
  public static final String REPOSITORY_EXTENSION_VALUE_CODING_CODE = "385645004";
  public static final String REPOSITORY_NAME = "gisaid";
  public static final String REPOSITORY_DATASET_ID = "EPI_ISL_16883504";
  public static final String PRIME_DIAGNOSTIC_LAB_DEMIS_LAB_ID = "987654321";
  public static final String PRIME_DIAGNOSTIC_LAB_ADDRESS = "Dingsweg 321";
  public static final String PRIME_DIAGNOSTIC_LAB_POSTAL_CODE = "13055";
  public static final String PRIME_DIAGNOSTIC_LAB_FEDERAL_STATE = "Berlin";
  public static final String PRIME_DIAGNOSTIC_LAB_COUNTRY = "DE";
  public static final String PRIME_DIAGNOSTIC_LAB_NAME = "Primärlabor";
  public static final String PRIME_DIAGNOSTIC_LAB_EMAIL = "primeDiag@mail.de";
  public static final String DEVICE_NAME_NAME = "GridION";
  public static final String SAMPLING_DATE = "2022-12-29T09:50:00+01:00";
  public static final String RECEIVED_DATE = "2022-12-29T15:40:00+01:00";
  public static final String SEQUENCING_DATE = "2023-01-13T10:40:00+01:00";
  public static final String SUBMISSION_DATE = "2023-12-01T10:40:00+01:00";
  public static final String STATUS = "final";
  public static final String ADAPTER_SUBSTANCE = "TAGCGAGT";
  public static final String SEQUENCING_PLATFORM = "oxford_nanopore";
  public static final String SPECIES =
      "Multiple drug-resistant Streptococcus pneumoniae (organism)";
  public static final String IGS_SEQUENCE_ID =
      "IGS-10243-SPNP-6cb7099d-8d53-4ee4-96ca-c55761b347d4";
  public static final String PRIMER_SCHEME_SUBSTANCE = "SchemeXYZ v3.5";
  public static final String SEQUENCING_STRATEGY = "WGS";
  public static final String ISOLATION_SOURCE = "Cerebrospinal fluid specimen (specimen)";

  private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
  public static final String HOST_SEX = "male";
  public static final String HOST_BIRTH_MONTH = "11";
  public static final String HOST_BIRTH_YEAR = "1970";
  public static final String GEOGRAPHIC_LOCATION = "104";
  public static final String ISOLATE = "Beta_123";
  public static final String NAME_AMP_PROTOCOL = "AmpProtocol Alpha_7";
  public static final String REPOSITORY_LINK = "https://pubmlst.org/1230423";
  public static final String FILE_ONE_NAME = "Sample12346_R1.fastq";
  public static final String FILE_ONE_SHA_256_SUM =
      "7ecb8f9f902ed8b45ccf83a7bc974b36a4df8e7d2d87a981fc8bea2f68a323a6";
  private static final DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
  private static final String DATE_PATTERN = "yyyy-MM-dd";
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

  private NotificationTestDataGenerator() {}

  /**
   * Generates test data for IGS notifications.
   *
   * @return Test data for IGS notifications.
   */
  public static IgsOverviewModel generateIgsOverviewModel() {
    return IgsOverviewModel.builder()
        .meldetatbestand(MELDETATBESTAND)
        .species(SPECIES)
        .demisNotificationId(NOTIFICATION_ID)
        .status(STATUS)
        .demisSequenceId(IGS_SEQUENCE_ID)
        .dateOfReceiving(parseDateByDateTimeString(RECEIVED_DATE))
        .dateOfSampling(parseDateByDateTimeString(SAMPLING_DATE))
        .dateOfSequencing(parseDateByDateTimeString(SEQUENCING_DATE))
        .dateOfSubmission(parseDateByDateTimeString(SUBMISSION_DATE))
        .sequencingInstrument(DEVICE_NAME_NAME)
        .sequencingPlatform(SEQUENCING_PLATFORM)
        .adapterSubstance(ADAPTER_SUBSTANCE)
        .primerSchemeSubstance(PRIMER_SCHEME_SUBSTANCE)
        .sequencingStrategy(SEQUENCING_STRATEGY)
        .isolationSource(ISOLATION_SOURCE)
        .hostSex(HOST_SEX)
        .hostBirthMonth(HOST_BIRTH_MONTH)
        .hostBirthYear(HOST_BIRTH_YEAR)
        .sequencingReason(SEQUENCING_REASON)
        .geographicLocation(GEOGRAPHIC_LOCATION)
        .isolate(ISOLATE)
        .author(AUTHOR)
        .nameAmpProtocol(NAME_AMP_PROTOCOL)
        .primeDiagnosticLabDemisLabId(PRIME_DIAGNOSTIC_LAB_DEMIS_LAB_ID)
        .primeDiagnosticLabAddress(PRIME_DIAGNOSTIC_LAB_ADDRESS)
        .primeDiagnosticLabPostalCode(PRIME_DIAGNOSTIC_LAB_POSTAL_CODE)
        .primeDiagnosticLabName(PRIME_DIAGNOSTIC_LAB_NAME)
        .primeDiagnosticLabCountry(PRIME_DIAGNOSTIC_LAB_COUNTRY)
        .primeDiagnosticLabCity(PRIME_DIAGNOSTIC_LAB_FEDERAL_STATE)
        .primeDiagnosticLabFederalState(PRIME_DIAGNOSTIC_LAB_FEDERAL_STATE)
        .primeDiagnosticLabEmail(PRIME_DIAGNOSTIC_LAB_EMAIL)
        .sequencingLabDemisLabId(SEQUENCING_LAB_IDENTIFIER_VALUE)
        .sequencingLabName(SEQUENCING_LAB_NAME)
        .sequencingLabAddress(SEQUENCING_LAB_ADDRESS_LINE)
        .sequencingLabPostalCode(SEQUENCING_LAB_ADDRESS_POSTAL_CODE)
        .sequencingLabCountry(SEQUENCING_LAB_ADDRESS_COUNTRY)
        .sequencingLabCity(SEQUENCING_LAB_ADDRESS_CITY)
        .sequencingLabFederalState(SEQUENCING_LAB_ADDRESS_CITY)
        .sequencingLabEmail(SEQUENCING_LAB_EMAIL)
        .repositoryName(REPOSITORY_NAME)
        .repositoryLink(REPOSITORY_LINK)
        .repositoryId(REPOSITORY_DATASET_ID)
        .uploadDate(parseDateByDateString(REPOSITORY_EXTENSION_VALUE_DATE_TIME))
        .uploadStatus(REPOSITORY_EXTENSION_VALUE_CODING_CODE)
        .uploadSubmitter(REPOSITORY_EXTENSION_VALUE_STRING)
        .fileOneName(FILE_ONE_NAME)
        .fileOneSha256Sum(FILE_ONE_SHA_256_SUM)
        .fileOneDocumentReference(FILE_ONE_ID)
        .labSequenceId(LAB_IDENTIFIER_VALUE)
        .rowNumber(1)
        .build();
  }

  private static Date parseDateByDateTimeString(String dateTime) {
    LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter);
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }

  private static Date parseDateByDateString(String dateTime) {
    LocalDate localDate = LocalDate.parse(dateTime, dateFormatter);
    return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }
}
