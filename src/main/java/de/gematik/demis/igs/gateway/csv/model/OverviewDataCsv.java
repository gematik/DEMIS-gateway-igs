package de.gematik.demis.igs.gateway.csv.model;

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

import com.opencsv.bean.CsvBindByName;
import de.gematik.demis.igs.gateway.csv.validation.constraints.IgsDate;
import de.gematik.demis.igs.gateway.csv.validation.constraints.IgsRequired;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** Maps column entries in the CSV file to properties. */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewDataCsv {

  @IgsRequired(column = "MELDETATBESTAND")
  @CsvBindByName(column = "MELDETATBESTAND")
  private String meldetatbestand;

  @IgsRequired(column = "SPECIES_CODE")
  @CsvBindByName(column = "SPECIES_CODE")
  private String speciesCode;

  @CsvBindByName(column = "SPECIES")
  private String species;

  @IgsRequired(column = "LAB_SEQUENCE_ID")
  @CsvBindByName(column = "LAB_SEQUENCE_ID")
  private String labSequenceId;

  @CsvBindByName(column = "DEMIS_NOTIFICATION_ID")
  private String demisNotificationId;

  @IgsRequired(column = "STATUS")
  @CsvBindByName(column = "STATUS")
  private String status;

  @IgsDate(column = "DATE_OF_SAMPLING")
  @CsvBindByName(column = "DATE_OF_SAMPLING")
  private String dateOfSampling;

  @IgsDate(column = "DATE_OF_RECEIVING")
  @IgsRequired(column = "DATE_OF_RECEIVING")
  @CsvBindByName(column = "DATE_OF_RECEIVING")
  private String dateOfReceiving;

  @IgsDate(column = "DATE_OF_SEQUENCING")
  @IgsRequired(column = "DATE_OF_SEQUENCING")
  @CsvBindByName(column = "DATE_OF_SEQUENCING")
  private String dateOfSequencing;

  @IgsRequired(column = "SEQUENCING_INSTRUMENT")
  @CsvBindByName(column = "SEQUENCING_INSTRUMENT")
  private String sequencingInstrument;

  @IgsRequired(column = "SEQUENCING_PLATFORM")
  @CsvBindByName(column = "SEQUENCING_PLATFORM")
  private String sequencingPlatform;

  @CsvBindByName(column = "ADAPTER")
  private String adapterSubstance;

  @CsvBindByName(column = "PRIMER_SCHEME")
  private String primerSchemeSubstance;

  @IgsRequired(column = "SEQUENCING_STRATEGY")
  @CsvBindByName(column = "SEQUENCING_STRATEGY")
  private String sequencingStrategy;

  @IgsRequired(column = "ISOLATION_SOURCE_CODE")
  @CsvBindByName(column = "ISOLATION_SOURCE_CODE")
  private String isolationSourceCode;

  @CsvBindByName(column = "ISOLATION_SOURCE")
  private String isolationSource;

  @CsvBindByName(column = "HOST_SEX")
  private String hostSex;

  @CsvBindByName(column = "HOST_BIRTH_MONTH")
  private String hostBirthMonth;

  @CsvBindByName(column = "HOST_BIRTH_YEAR")
  private String hostBirthYear;

  @IgsRequired(column = "SEQUENCING_REASON")
  @CsvBindByName(column = "SEQUENCING_REASON")
  private String sequencingReason;

  @CsvBindByName(column = "GEOGRAPHIC_LOCATION")
  private String geographicLocation;

  @CsvBindByName(column = "ISOLATE")
  private String isolate;

  @CsvBindByName(column = "AUTHOR")
  private String author;

  @CsvBindByName(column = "NAME_AMP_PROTOCOL")
  private String nameAmpProtocol;

  @CsvBindByName(column = "PRIME_DIAGNOSTIC_LAB.DEMIS_LAB_ID")
  private String primeDiagnosticLabDemisLabId;

  @IgsRequired(column = "PRIME_DIAGNOSTIC_LAB.NAME")
  @CsvBindByName(column = "PRIME_DIAGNOSTIC_LAB.NAME")
  private String primeDiagnosticLabName;

  @IgsRequired(column = "PRIME_DIAGNOSTIC_LAB.ADDRESS")
  @CsvBindByName(column = "PRIME_DIAGNOSTIC_LAB.ADDRESS")
  private String primeDiagnosticLabAddress;

  @IgsRequired(column = "PRIME_DIAGNOSTIC_LAB.POSTAL_CODE")
  @CsvBindByName(column = "PRIME_DIAGNOSTIC_LAB.POSTAL_CODE")
  private String primeDiagnosticLabPostalCode;

  @IgsRequired(column = "PRIME_DIAGNOSTIC_LAB.CITY")
  @CsvBindByName(column = "PRIME_DIAGNOSTIC_LAB.CITY")
  private String primeDiagnosticLabCity;

  @CsvBindByName(column = "PRIME_DIAGNOSTIC_LAB.FEDERAL_STATE")
  private String primeDiagnosticLabFederalState;

  @IgsRequired(column = "PRIME_DIAGNOSTIC_LAB.COUNTRY")
  @CsvBindByName(column = "PRIME_DIAGNOSTIC_LAB.COUNTRY")
  private String primeDiagnosticLabCountry;

  @IgsRequired(column = "PRIME_DIAGNOSTIC_LAB.EMAIL")
  @CsvBindByName(column = "PRIME_DIAGNOSTIC_LAB.EMAIL")
  private String primeDiagnosticLabEmail;

  @IgsRequired(column = "SEQUENCING_LAB.DEMIS_LAB_ID")
  @CsvBindByName(column = "SEQUENCING_LAB.DEMIS_LAB_ID")
  private String sequencingLabDemisLabId;

  @IgsRequired(column = "SEQUENCING_LAB.NAME")
  @CsvBindByName(column = "SEQUENCING_LAB.NAME")
  private String sequencingLabName;

  @CsvBindByName(column = "SEQUENCING_LAB.ADDRESS")
  private String sequencingLabAddress;

  @IgsRequired(column = "SEQUENCING_LAB.POSTAL_CODE")
  @CsvBindByName(column = "SEQUENCING_LAB.POSTAL_CODE")
  private String sequencingLabPostalCode;

  @IgsRequired(column = "SEQUENCING_LAB.CITY")
  @CsvBindByName(column = "SEQUENCING_LAB.CITY")
  private String sequencingLabCity;

  @CsvBindByName(column = "SEQUENCING_LAB.FEDERAL_STATE")
  private String sequencingLabFederalState;

  @IgsRequired(column = "SEQUENCING_LAB.COUNTRY")
  @CsvBindByName(column = "SEQUENCING_LAB.COUNTRY")
  private String sequencingLabCountry;

  @IgsRequired(column = "SEQUENCING_LAB.EMAIL")
  @CsvBindByName(column = "SEQUENCING_LAB.EMAIL")
  private String sequencingLabEmail;

  @CsvBindByName(column = "REPOSITORY_NAME")
  private String repositoryName;

  @CsvBindByName(column = "REPOSITORY_LINK")
  private String repositoryLink;

  @CsvBindByName(column = "REPOSITORY_ID")
  private String repositoryId;

  @IgsDate(column = "UPLOAD_DATE")
  @CsvBindByName(column = "UPLOAD_DATE")
  private String uploadDate;

  @CsvBindByName(column = "UPLOAD_STATUS")
  private String uploadStatus;

  @CsvBindByName(column = "UPLOAD_SUBMITTER")
  private String uploadSubmitter;

  @IgsRequired(column = "FILE_1_NAME")
  @CsvBindByName(column = "FILE_1_NAME")
  private String fileOneName;

  @IgsRequired(column = "FILE_1_SHA256SUM")
  @CsvBindByName(column = "FILE_1_SHA256SUM")
  private String fileOneSha256Sum;

  @CsvBindByName(column = "FILE_2_NAME")
  private String fileTwoName;

  @CsvBindByName(column = "FILE_2_SHA256SUM")
  private String fileTwoSha256Sum;
}
