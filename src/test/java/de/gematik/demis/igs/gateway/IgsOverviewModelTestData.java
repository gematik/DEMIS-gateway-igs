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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.igs.gateway.notification.IgsOverviewModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class IgsOverviewModelTestData {

  public static final SimpleDateFormat formatter =
      new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
  public static IgsOverviewModel.IgsOverviewModelBuilder firstRow;
  public static IgsOverviewModel.IgsOverviewModelBuilder secondRow;
  public static IgsOverviewModel.IgsOverviewModelBuilder thirdRow;
  public static final String FAKE_DEMIS_NOTIFICATION_ID = "00000000-0000-0000-0000-000000000000";

  static {
    try {
      firstRow =
          IgsOverviewModel.builder()
              .meldetatbestand("SPNP")
              .speciesCode("840533007")
              .species("Severe acute respiratory syndrome coronavirus 2 (organism)")
              .labSequenceId("Sample12346")
              .demisNotificationId("6cb7099d-8d53-4ee4-96ca-c55761b347d4")
              .status("final")
              .dateOfSampling(formatter.parse("2022-05-18"))
              .dateOfReceiving(formatter.parse("2023-03-03"))
              .dateOfSequencing(formatter.parse("2022-09-29"))
              .sequencingInstrument("NextSeq_500")
              .sequencingPlatform("ILLUMINA")
              .adapterSubstance("CTGTCTCTTATACACATCT+ATGTGTATAAGAGACA")
              .primerSchemeSubstance("SchemeXYZ v3.5")
              .sequencingStrategy("WGS")
              .isolationSourceCode("309164002")
              .isolationSource("Upper respiratory swab specimen (specimen)")
              .hostSex("male")
              .hostBirthMonth("11")
              .hostBirthYear("1970")
              .sequencingReason("other")
              .geographicLocation("104")
              .isolate("Beta_123")
              .author("Babara Muster")
              .nameAmpProtocol("AmpProtocol Alpha_7")
              .primeDiagnosticLabDemisLabId("DEMIS-10666")
              .primeDiagnosticLabName("Lab Ernst")
              .primeDiagnosticLabAddress("Steinstr. 5")
              .primeDiagnosticLabPostalCode("10407")
              .primeDiagnosticLabCity("Berlin")
              .primeDiagnosticLabFederalState("Berlin")
              .primeDiagnosticLabCountry("DE")
              .primeDiagnosticLabEmail("my@email.com")
              .sequencingLabDemisLabId("10234")
              .sequencingLabName("Labor Buchstabensalat")
              .sequencingLabAddress("Lehmstr. 12")
              .sequencingLabPostalCode("42653")
              .sequencingLabCity("Muenchen")
              .sequencingLabFederalState("Bayern")
              .sequencingLabCountry("DE")
              .sequencingLabEmail("my@email.com")
              .repositoryName("PubMLST")
              .repositoryLink("https://pubmlst.org/1230423")
              .repositoryId("1230423")
              .uploadDate(formatter.parse("2024-05-22"))
              .uploadStatus("Planned")
              .uploadSubmitter("Thomas Stern")
              .fileOneName("Sample12346_R1.fastq")
              .fileOneSha256Sum("7ecb8f9f902ed8b45ccf83a7bc974b36a4df8e7d2d87a981fc8bea2f68a323a6")
              .fileOneDocumentReference(null)
              .fileTwoName("Sample12346_R2.fastq")
              .fileTwoSha256Sum("f9a32e1d7c7554d2db2fafe53c239bd6de968979390ab3b914d5e179cec6ff13")
              .fileTwoDocumentReference(null)
              .rowNumber(1);
      secondRow =
          IgsOverviewModel.builder()
              .meldetatbestand("SPNP")
              .speciesCode("840533007")
              .species("Severe acute respiratory syndrome coronavirus 2 (organism)")
              .labSequenceId("Sample12347")
              .demisNotificationId("25cae3da-a8ae-4363-8475-6c24c897f099")
              .status("preliminary")
              .dateOfSampling(formatter.parse("2022-07-06"))
              .dateOfReceiving(formatter.parse("2023-03-03"))
              .dateOfSequencing(formatter.parse("2022-09-29"))
              .sequencingInstrument("NextSeq_500")
              .sequencingPlatform("ILLUMINA")
              .adapterSubstance("CTGTCTCTTATACACATCT+ATGTGTATAAGAGACA")
              .primerSchemeSubstance("SchemeXYZ v3.5")
              .sequencingStrategy("WGS")
              .isolationSourceCode("309164002")
              .isolationSource("Upper respiratory swab specimen (specimen)")
              .hostSex("female")
              .hostBirthMonth("10")
              .hostBirthYear("1980")
              .sequencingReason("other")
              .geographicLocation("413")
              .isolate("Beta_124")
              .author("Babara Muster")
              .nameAmpProtocol("AmpProtocol Alpha_7")
              .primeDiagnosticLabDemisLabId("DEMIS-10666")
              .primeDiagnosticLabName("Lab Ernst")
              .primeDiagnosticLabAddress("Steinstr. 5")
              .primeDiagnosticLabPostalCode("10407")
              .primeDiagnosticLabCity("Berlin")
              .primeDiagnosticLabFederalState("Berlin")
              .primeDiagnosticLabCountry("DE")
              .primeDiagnosticLabEmail("my@email.com")
              .sequencingLabDemisLabId("10234")
              .sequencingLabName("Labor Buchstabensalat")
              .sequencingLabAddress("Lehmstr. 12")
              .sequencingLabPostalCode("42653")
              .sequencingLabCity("Muenchen")
              .sequencingLabFederalState("Bayern")
              .sequencingLabCountry("DE")
              .sequencingLabEmail("my@email.com")
              .repositoryName("PubMLST")
              .repositoryLink("https://pubmlst.org/2141234")
              .repositoryId("2141234")
              .uploadDate(formatter.parse("2024-05-22"))
              .uploadStatus("Planned")
              .uploadSubmitter("Thomas Stern")
              .fileOneName("Sample12347_R1.fastq")
              .fileOneSha256Sum("090a78cf3ff533272b22a1c853a1aff1f60f1aed99305262552824de36a4d06e")
              .fileOneDocumentReference(null)
              .fileTwoName("Sample12347_R2.fastq")
              .fileTwoSha256Sum("d740f315040bc8421b00419b30f7aba9d01f239f7efaa9677b4b1ff3b4aaa44e")
              .fileTwoDocumentReference(null)
              .rowNumber(2);
      thirdRow =
          IgsOverviewModel.builder()
              .meldetatbestand("SPNP")
              .speciesCode("840533007")
              .species("Severe acute respiratory syndrome coronavirus 2 (organism)")
              .labSequenceId("Sample12348")
              .demisNotificationId("92687a30-8f6a-4e80-beca-78defe7af5e8")
              .status("amended")
              .dateOfSampling(formatter.parse("2023-01-24"))
              .dateOfReceiving(formatter.parse("2023-03-03"))
              .dateOfSequencing(formatter.parse("2023-08-24"))
              .sequencingInstrument("NextSeq_500")
              .sequencingPlatform("ILLUMINA")
              .adapterSubstance("CTGTCTCTTATACACATCT+ATGTGTATAAGAGACA")
              .primerSchemeSubstance("SchemeXYZ v3.5")
              .sequencingStrategy("WGS")
              .isolationSourceCode("309164002")
              .isolationSource("Upper respiratory swab specimen (specimen)")
              .hostSex("diverse")
              .hostBirthMonth("9")
              .hostBirthYear("1990")
              .sequencingReason("other")
              .geographicLocation("820")
              .isolate("Beta_125")
              .author("Babara Muster")
              .nameAmpProtocol("AmpProtocol Alpha_7")
              .primeDiagnosticLabDemisLabId("DEMIS-10666")
              .primeDiagnosticLabName("Lab Ernst")
              .primeDiagnosticLabAddress("Steinstr. 5")
              .primeDiagnosticLabPostalCode("10407")
              .primeDiagnosticLabCity("Berlin")
              .primeDiagnosticLabFederalState("Berlin")
              .primeDiagnosticLabCountry("DE")
              .primeDiagnosticLabEmail("my@email.com")
              .sequencingLabDemisLabId("10234")
              .sequencingLabName("Labor Buchstabensalat")
              .sequencingLabAddress("Lehmstr. 12")
              .sequencingLabPostalCode("42653")
              .sequencingLabCity("Muenchen")
              .sequencingLabFederalState("Bayern")
              .sequencingLabCountry("DE")
              .sequencingLabEmail("my@email.com")
              .repositoryName("PubMLST")
              .repositoryLink("https://pubmlst.org/2348234")
              .repositoryId("2348234")
              .uploadDate(formatter.parse("2024-05-22"))
              .uploadStatus("Accepted")
              .uploadSubmitter("Thomas Stern")
              .fileOneName("Sample12348_R1.fq.gz")
              .fileOneSha256Sum("198b3a461414526830704c5c60c89a8a43348e5e28a0cfe4e2e83567dabefca8")
              .fileOneDocumentReference(null)
              .fileTwoName("Sample12348_R2.fq.gz")
              .fileTwoSha256Sum("777a95cd97382080bfeb78a5b0b56a6f43aa6b9f02618be37f7cd156edffe5da")
              .fileTwoDocumentReference(null)
              .rowNumber(3);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Determines the test overview data from igs-batch-fastq-testdata_reduced.csv.
   *
   * @return The expected overview data.
   */
  public static List<IgsOverviewModel> determineTestOverviewData() {
    return List.of(firstRow.build(), secondRow.build(), thirdRow.build());
  }

  /**
   * Determines the expected overview data from igs-batch-fastq-testdata_reduced.csv.
   *
   * @return The expected overview data.
   */
  public static List<IgsOverviewModel> determineExpectedOverviewData() throws ParseException {
    final IgsOverviewModel firstRow =
        IgsOverviewModel.builder()
            .meldetatbestand("SPNP")
            .speciesCode("840533007")
            .species("Severe acute respiratory syndrome coronavirus 2 (organism)")
            .labSequenceId("Sample12346")
            .demisNotificationId("6cb7099d-8d53-4ee4-96ca-c55761b347d4")
            .status("final")
            .dateOfSampling(formatter.parse("2022-05-18"))
            .dateOfReceiving(formatter.parse("2023-03-03"))
            .dateOfSequencing(formatter.parse("2022-09-29"))
            .sequencingInstrument("nextseq_500")
            .sequencingPlatform("illumina")
            .adapterSubstance("CTGTCTCTTATACACATCT+ATGTGTATAAGAGACA")
            .primerSchemeSubstance("SchemeXYZ v3.5")
            .sequencingStrategy("WGS")
            .isolationSourceCode("309164002")
            .isolationSource("Upper respiratory swab specimen (specimen)")
            .hostSex("male")
            .hostBirthMonth("11")
            .hostBirthYear("1970")
            .sequencingReason("other")
            .geographicLocation("104")
            .isolate("Beta_123")
            .author("Babara Muster")
            .nameAmpProtocol("AmpProtocol Alpha_7")
            .primeDiagnosticLabDemisLabId("DEMIS-10666")
            .primeDiagnosticLabName("Lab Ernst")
            .primeDiagnosticLabAddress("Steinstr. 5")
            .primeDiagnosticLabPostalCode("10407")
            .primeDiagnosticLabCity("Berlin")
            .primeDiagnosticLabFederalState("Berlin")
            .primeDiagnosticLabCountry("DE")
            .primeDiagnosticLabEmail("my@email.com")
            .sequencingLabDemisLabId("10234")
            .sequencingLabName("Labor Buchstabensalat")
            .sequencingLabAddress("Lehmstr. 12")
            .sequencingLabPostalCode("42653")
            .sequencingLabCity("Muenchen")
            .sequencingLabFederalState("Bayern")
            .sequencingLabCountry("DE")
            .sequencingLabEmail("my@email.com")
            .repositoryName("pubmlst")
            .repositoryLink("https://pubmlst.org/1230423")
            .repositoryId("1230423")
            .uploadDate(formatter.parse("2024-05-22"))
            .uploadStatus("Planned")
            .uploadSubmitter("Thomas Stern")
            .fileOneName("Sample12346_R1.fastq")
            .fileOneSha256Sum("7ecb8f9f902ed8b45ccf83a7bc974b36a4df8e7d2d87a981fc8bea2f68a323a6")
            .fileOneDocumentReference(null)
            .fileTwoName("Sample12346_R2.fastq")
            .fileTwoSha256Sum("f9a32e1d7c7554d2db2fafe53c239bd6de968979390ab3b914d5e179cec6ff13")
            .fileTwoDocumentReference(null)
            .rowNumber(1)
            .build();
    final IgsOverviewModel secondRow =
        IgsOverviewModel.builder()
            .meldetatbestand("SPNP")
            .speciesCode("840533007")
            .species("Severe acute respiratory syndrome coronavirus 2 (organism)")
            .labSequenceId("Sample12347")
            .demisNotificationId("25cae3da-a8ae-4363-8475-6c24c897f099")
            .status("preliminary")
            .dateOfSampling(formatter.parse("2022-07-06"))
            .dateOfReceiving(formatter.parse("2023-03-03"))
            .dateOfSequencing(formatter.parse("2022-09-29"))
            .sequencingInstrument("nextseq_500")
            .sequencingPlatform("illumina")
            .adapterSubstance("CTGTCTCTTATACACATCT+ATGTGTATAAGAGACA")
            .primerSchemeSubstance("SchemeXYZ v3.5")
            .sequencingStrategy("WGS")
            .isolationSourceCode("309164002")
            .isolationSource("Upper respiratory swab specimen (specimen)")
            .hostSex("female")
            .hostBirthMonth("10")
            .hostBirthYear("1980")
            .sequencingReason("other")
            .geographicLocation("413")
            .isolate("Beta_124")
            .author("Babara Muster")
            .nameAmpProtocol("AmpProtocol Alpha_7")
            .primeDiagnosticLabDemisLabId("DEMIS-10666")
            .primeDiagnosticLabName("Lab Ernst")
            .primeDiagnosticLabAddress("Steinstr. 5")
            .primeDiagnosticLabPostalCode("10407")
            .primeDiagnosticLabCity("Berlin")
            .primeDiagnosticLabFederalState("Berlin")
            .primeDiagnosticLabCountry("DE")
            .primeDiagnosticLabEmail("my@email.com")
            .sequencingLabDemisLabId("10234")
            .sequencingLabName("Labor Buchstabensalat")
            .sequencingLabAddress("Lehmstr. 12")
            .sequencingLabPostalCode("42653")
            .sequencingLabCity("Muenchen")
            .sequencingLabFederalState("Bayern")
            .sequencingLabCountry("DE")
            .sequencingLabEmail("my@email.com")
            .repositoryName("pubmlst")
            .repositoryLink("https://pubmlst.org/2141234")
            .repositoryId("2141234")
            .uploadDate(formatter.parse("2024-05-22"))
            .uploadStatus("Planned")
            .uploadSubmitter("Thomas Stern")
            .fileOneName("Sample12347_R1.fastq")
            .fileOneSha256Sum("090a78cf3ff533272b22a1c853a1aff1f60f1aed99305262552824de36a4d06e")
            .fileOneDocumentReference(null)
            .fileTwoName("Sample12347_R2.fastq")
            .fileTwoSha256Sum("d740f315040bc8421b00419b30f7aba9d01f239f7efaa9677b4b1ff3b4aaa44e")
            .fileTwoDocumentReference(null)
            .rowNumber(2)
            .build();
    final IgsOverviewModel thirdRow =
        IgsOverviewModel.builder()
            .meldetatbestand("SPNP")
            .speciesCode("840533007")
            .species("Severe acute respiratory syndrome coronavirus 2 (organism)")
            .labSequenceId("Sample12348")
            .demisNotificationId("92687a30-8f6a-4e80-beca-78defe7af5e8")
            .status("amended")
            .dateOfSampling(formatter.parse("2023-01-24"))
            .dateOfReceiving(formatter.parse("2023-03-03"))
            .dateOfSequencing(formatter.parse("2023-08-24"))
            .sequencingInstrument("nextseq_500")
            .sequencingPlatform("illumina")
            .adapterSubstance("CTGTCTCTTATACACATCT+ATGTGTATAAGAGACA")
            .primerSchemeSubstance("SchemeXYZ v3.5")
            .sequencingStrategy("WGS")
            .isolationSourceCode("309164002")
            .isolationSource("Upper respiratory swab specimen (specimen)")
            .hostSex("diverse")
            .hostBirthMonth("9")
            .hostBirthYear("1990")
            .sequencingReason("other")
            .geographicLocation("820")
            .isolate("Beta_125")
            .author("Babara Muster")
            .nameAmpProtocol("AmpProtocol Alpha_7")
            .primeDiagnosticLabDemisLabId("DEMIS-10666")
            .primeDiagnosticLabName("Lab Ernst")
            .primeDiagnosticLabAddress("Steinstr. 5")
            .primeDiagnosticLabPostalCode("10407")
            .primeDiagnosticLabCity("Berlin")
            .primeDiagnosticLabFederalState("Berlin")
            .primeDiagnosticLabCountry("DE")
            .primeDiagnosticLabEmail("my@email.com")
            .sequencingLabDemisLabId("10234")
            .sequencingLabName("Labor Buchstabensalat")
            .sequencingLabAddress("Lehmstr. 12")
            .sequencingLabPostalCode("42653")
            .sequencingLabCity("Muenchen")
            .sequencingLabFederalState("Bayern")
            .sequencingLabCountry("DE")
            .sequencingLabEmail("my@email.com")
            .repositoryName("pubmlst")
            .repositoryLink("https://pubmlst.org/2348234")
            .repositoryId("2348234")
            .uploadDate(formatter.parse("2024-05-22"))
            .uploadStatus("Accepted")
            .uploadSubmitter("Thomas Stern")
            .fileOneName("Sample12348_R1.fq.gz")
            .fileOneSha256Sum("198b3a461414526830704c5c60c89a8a43348e5e28a0cfe4e2e83567dabefca8")
            .fileOneDocumentReference(null)
            .fileTwoName("Sample12348_R2.fq.gz")
            .fileTwoSha256Sum("777a95cd97382080bfeb78a5b0b56a6f43aa6b9f02618be37f7cd156edffe5da")
            .fileTwoDocumentReference(null)
            .rowNumber(3)
            .build();
    return List.of(firstRow, secondRow, thirdRow);
  }
}
