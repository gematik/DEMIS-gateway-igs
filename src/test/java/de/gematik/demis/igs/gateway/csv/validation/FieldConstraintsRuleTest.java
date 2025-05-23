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

import static de.gematik.demis.igs.gateway.TestUtils.MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER;
import static de.gematik.demis.igs.gateway.TestUtils.MOCKED_ERROR_MESSAGE_WITH_TWO_PLACEHOLDERS;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_DATE_FORMAT;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_PREFIX;
import static de.gematik.demis.igs.gateway.configuration.MessagesProperties.ERROR_REQUIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.gematik.demis.igs.gateway.configuration.MessageSourceWrapper;
import de.gematik.demis.igs.gateway.csv.model.IgsOverviewCsv;
import de.gematik.demis.igs.gateway.csv.model.OverviewDataCsv;
import de.gematik.demis.igs.gateway.csv.validation.ValidationError.ErrorCode;
import de.gematik.demis.igs.gateway.csv.validation.rules.FieldConstraintsRule;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class FieldConstraintsRuleTest extends SpringValidatorRuleTest {

  @MockitoBean private MessageSourceWrapper messageSourceWrapper;

  @Autowired FieldConstraintsRule rule;

  private final String DATE_WRONG_FORMAT = "2021.01.01";
  private final String CONCAT_REQUIRED_ERROR =
      MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER + MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER;
  private final String CONCAT_DATE_FORMAT_ERROR =
      MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER + MOCKED_ERROR_MESSAGE_WITH_TWO_PLACEHOLDERS;

  @Nested
  class DateFieldConstraintTests {

    @Test
    void shouldFindNoErrors() {
      List<ValidationError> errors =
          getDateErrors(firstRowBuilder.build(), secondRowBuilder.build(), thirdRowBuilder.build());
      assertThat(errors).isEmpty();
    }

    @Test
    void shouldFindNoErrorsWithLocalDateFormat() {
      IgsOverviewCsv firstRow = firstRowBuilder.build();
      firstRow.setDateOfSampling("18.05.2022");
      firstRow.setDateOfReceiving("03.03.2023");
      firstRow.setDateOfSequencing("29.09.2022");
      firstRow.setUploadDate("22.05.2024");

      IgsOverviewCsv secondRow = secondRowBuilder.build();
      secondRow.setDateOfReceiving("03.03.2023");

      List<ValidationError> errors = getDateErrors(firstRow, secondRow, thirdRowBuilder.build());
      assertThat(errors).isEmpty();
    }

    @SneakyThrows
    @ParameterizedTest(name = "{0}")
    @CsvSource({
      "dateOfSampling,DATE_OF_SAMPLING",
      "dateOfReceiving,DATE_OF_RECEIVING",
      "dateOfSequencing,DATE_OF_SEQUENCING",
      "uploadDate,UPLOAD_DATE"
    })
    void shouldFindErrorIfDateFormatedWrong(String datePropertyName, String columnName) {

      IgsOverviewCsv row = firstRowBuilder.build();
      Field rulesField = OverviewDataCsv.class.getDeclaredField(datePropertyName);
      rulesField.setAccessible(true);
      rulesField.set(row, DATE_WRONG_FORMAT);

      mockMessageWithRowNumber(row);

      mockMessageForErrorDateFormat(messageSourceWrapper, columnName, DATE_WRONG_FORMAT);

      List<ValidationError> errors = getDateErrors(row);

      assertThat(errors).hasSize(1);
      assertThat(errors.getFirst().getMsg())
          .isEqualTo(
              CONCAT_DATE_FORMAT_ERROR.formatted(
                  row.getRowNumber(), DATE_WRONG_FORMAT, columnName));
    }

    @SneakyThrows
    @ParameterizedTest(name = "{0}")
    @CsvSource({
      "dateOfSampling,DATE_OF_SAMPLING,2021-01-32", // Invalid day
      "dateOfReceiving,DATE_OF_RECEIVING,2021-13-30", // Invalid month
      "uploadDate,UPLOAD_DATE,2025-02-29" // Invalid leap year date
    })
    void shouldFindErrorOnInvalidDatesWithCorrectFormat(
        String datePropertyName, String columnName, String date) {
      IgsOverviewCsv row = firstRowBuilder.build();

      Field rulesField = OverviewDataCsv.class.getDeclaredField(datePropertyName);
      rulesField.setAccessible(true);
      rulesField.set(row, date);

      mockMessageWithRowNumber(row);

      mockMessageForErrorDateFormat(messageSourceWrapper, columnName, date);

      List<ValidationError> errors = getDateErrors(row);

      assertThat(errors).hasSize(1);
      assertThat(errors.getFirst().getMsg())
          .isEqualTo(CONCAT_DATE_FORMAT_ERROR.formatted(row.getRowNumber(), date, columnName));
    }

    @Test
    void shouldFindErrorIfDateFormatedWrongInOneLine() {

      IgsOverviewCsv row = firstRowBuilder.build();
      row.setDateOfSampling(DATE_WRONG_FORMAT);

      mockMessageWithRowNumber(row);
      mockMessageForErrorDateFormat(messageSourceWrapper, "DATE_OF_SAMPLING", DATE_WRONG_FORMAT);

      List<ValidationError> errors = getDateErrors(row);
      errors = errors.stream().filter(e -> e.getErrorCode() == ErrorCode.DATE_FORMAT).toList();

      assertThat(errors).hasSize(1);
      assertThat(errors.getFirst().getMsg())
          .isEqualTo(
              CONCAT_DATE_FORMAT_ERROR.formatted(
                  row.getRowNumber(), DATE_WRONG_FORMAT, "DATE_OF_SAMPLING"));
      assertThat(errors.getFirst().getRowNumber()).isEqualTo(row.getRowNumber());
      assertThat(errors.getFirst().getColumnName()).isEqualTo("DATE_OF_SAMPLING");
      assertThat(errors.getFirst().getFoundValue()).isEqualTo(DATE_WRONG_FORMAT);
      assertThat(errors.getFirst().getErrorCode()).isEqualTo(ErrorCode.DATE_FORMAT);
    }

    @Test
    void shouldFindTwoErrorsIfDateFormatedWrongInOneLine() {

      IgsOverviewCsv row = firstRowBuilder.build();
      row.setDateOfSampling(DATE_WRONG_FORMAT);
      row.setDateOfReceiving(DATE_WRONG_FORMAT);

      row.setDateOfSampling(DATE_WRONG_FORMAT);

      mockMessageWithRowNumber(row);

      mockMessageForErrorDateFormat(messageSourceWrapper, "DATE_OF_SAMPLING", DATE_WRONG_FORMAT);
      mockMessageForErrorDateFormat(messageSourceWrapper, "DATE_OF_RECEIVING", DATE_WRONG_FORMAT);

      List<ValidationError> errors = getDateErrors(row);

      assertThat(errors).hasSize(2);
      assertThat(errors.stream().map(ValidationError::getMsg))
          .containsExactlyInAnyOrder(
              CONCAT_DATE_FORMAT_ERROR.formatted(
                  row.getRowNumber(), DATE_WRONG_FORMAT, "DATE_OF_SAMPLING"),
              CONCAT_DATE_FORMAT_ERROR.formatted(
                  row.getRowNumber(), DATE_WRONG_FORMAT, "DATE_OF_RECEIVING"));
    }

    @Test
    void shouldFindTwoErrorIfDateFormatedWrongInMultiLine() {

      IgsOverviewCsv row = firstRowBuilder.build();
      IgsOverviewCsv row2 = secondRowBuilder.build();
      row.setDateOfSampling(DATE_WRONG_FORMAT);
      row2.setDateOfSampling(DATE_WRONG_FORMAT);

      mockMessageWithRowNumber(row);
      mockMessageWithRowNumber(row2);

      mockMessageForErrorDateFormat(messageSourceWrapper, "DATE_OF_SAMPLING", DATE_WRONG_FORMAT);
      mockMessageForErrorDateFormat(messageSourceWrapper, "DATE_OF_RECEIVING", DATE_WRONG_FORMAT);

      List<ValidationError> errors = getDateErrors(row, row2);

      assertThat(errors).hasSize(2);
      assertThat(errors.getFirst().getMsg())
          .isEqualTo(
              CONCAT_DATE_FORMAT_ERROR.formatted(
                  row.getRowNumber(), DATE_WRONG_FORMAT, "DATE_OF_SAMPLING"));
      assertThat(errors.getLast().getMsg())
          .isEqualTo(
              CONCAT_DATE_FORMAT_ERROR.formatted(
                  row2.getRowNumber(), DATE_WRONG_FORMAT, "DATE_OF_SAMPLING"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    void shouldFindNoErrorsIfDateIsNull(String date) {
      IgsOverviewCsv row = firstRowBuilder.build();
      row.setDateOfSampling(date);
      row.setDateOfReceiving(date);
      row.setDateOfSequencing(date);
      row.setUploadDate(date);
      List<ValidationError> errors = getDateErrors(row);
      assertThat(errors).isEmpty();
    }

    private List<ValidationError> getDateErrors(IgsOverviewCsv... rows) {
      return Stream.of(rows)
          .map(rule::applyOnValue)
          .flatMap(List::stream)
          .filter(e -> e.getErrorCode() == ErrorCode.DATE_FORMAT)
          .toList();
    }
  }

  @Nested
  class RequiredFieldRuleTests {

    @Test
    void shouldFindNoErrors() {
      List<ValidationError> errors =
          getRequiredFieldsErrors(
              firstRowBuilder.build(), secondRowBuilder.build(), thirdRowBuilder.build());
      assertThat(errors).isEmpty();
    }

    @NullSource
    @EmptySource
    @ParameterizedTest
    void shouldFindOneErrorInOneLine(String value) {
      IgsOverviewCsv row = firstRowBuilder.build();
      row.setMeldetatbestand(value);

      mockMessageWithRowNumber(row);
      mockMessageForErrorRequired(messageSourceWrapper, "MELDETATBESTAND");

      List<ValidationError> errors = getRequiredFieldsErrors(row);

      assertThat(errors).hasSize(1);
      assertThat(errors.getFirst().getMsg())
          .isEqualTo(CONCAT_REQUIRED_ERROR.formatted(row.getRowNumber(), "MELDETATBESTAND"));
      assertThat(errors.getFirst().getErrorCode()).isEqualTo(ErrorCode.REQUIRED_FIELD);
      assertThat(errors.getFirst().getFoundValue()).isNull();
      assertThat(errors.getFirst().getRowNumber()).isEqualTo(row.getRowNumber());
      assertThat(errors.getFirst().getColumnName()).isEqualTo("MELDETATBESTAND");
    }

    @NullSource
    @EmptySource
    @ParameterizedTest
    void shouldFindTwoErrorsInOneLine(String value) {
      IgsOverviewCsv row = firstRowBuilder.build();
      row.setMeldetatbestand(value);
      row.setSpeciesCode(value);

      mockMessageWithRowNumber(row);

      mockMessageForErrorRequired(messageSourceWrapper, "MELDETATBESTAND");
      mockMessageForErrorRequired(messageSourceWrapper, "SPECIES_CODE");

      List<ValidationError> errors = getRequiredFieldsErrors(row);
      assertThat(errors).hasSize(2);
      assertThat(errors.stream().map(ValidationError::getMsg))
          .containsExactlyInAnyOrder(
              CONCAT_REQUIRED_ERROR.formatted(row.getRowNumber(), "SPECIES_CODE"),
              CONCAT_REQUIRED_ERROR.formatted(row.getRowNumber(), "MELDETATBESTAND"));
      assertThat(errors.stream().map(ValidationError::getColumnName))
          .containsExactlyInAnyOrder("SPECIES_CODE", "MELDETATBESTAND");
      assertThat(errors.stream().map(ValidationError::getFoundValue)).allMatch(Objects::isNull);
      assertThat(errors.stream().map(ValidationError::getRowNumber))
          .allMatch(r -> r == row.getRowNumber());
    }

    @NullSource
    @EmptySource
    @ParameterizedTest
    void shouldFindTwoErrorsInTwoLines(String value) {
      IgsOverviewCsv row = firstRowBuilder.build();
      IgsOverviewCsv row2 = secondRowBuilder.build();
      row.setMeldetatbestand(value);
      row2.setMeldetatbestand(value);

      mockMessageWithRowNumber(row);
      mockMessageWithRowNumber(row2);

      mockMessageForErrorRequired(messageSourceWrapper, "MELDETATBESTAND");
      mockMessageForErrorRequired(messageSourceWrapper, "SPECIES_CODE");

      List<ValidationError> errors = getRequiredFieldsErrors(row, row2);

      assertThat(errors).hasSize(2);
      assertThat(errors.getFirst().getMsg())
          .isEqualTo(CONCAT_REQUIRED_ERROR.formatted(row.getRowNumber(), "MELDETATBESTAND"));
      assertThat(errors.getFirst().getErrorCode()).isEqualTo(ErrorCode.REQUIRED_FIELD);
      assertThat(errors.getFirst().getFoundValue()).isNull();
      assertThat(errors.getFirst().getRowNumber()).isEqualTo(row.getRowNumber());
      assertThat(errors.getFirst().getColumnName()).isEqualTo("MELDETATBESTAND");
      assertThat(errors.getLast().getMsg())
          .isEqualTo(CONCAT_REQUIRED_ERROR.formatted(row2.getRowNumber(), "MELDETATBESTAND"));
      assertThat(errors.getLast().getErrorCode()).isEqualTo(ErrorCode.REQUIRED_FIELD);
      assertThat(errors.getLast().getFoundValue()).isNull();
      assertThat(errors.getLast().getRowNumber()).isEqualTo(row2.getRowNumber());
      assertThat(errors.getLast().getColumnName()).isEqualTo("MELDETATBESTAND");
    }

    private List<ValidationError> getRequiredFieldsErrors(IgsOverviewCsv... rows) {
      return Stream.of(rows)
          .map(rule::applyOnValue)
          .flatMap(List::stream)
          .filter(e -> e.getErrorCode() == ErrorCode.REQUIRED_FIELD)
          .toList();
    }
  }

  @Nested
  class TotalRuleTests {

    @Test
    void shouldFindNoErrors() {
      List<ValidationError> errors = new ArrayList<>(rule.applyOnValue(firstRowBuilder.build()));
      errors.addAll(rule.applyOnValue(secondRowBuilder.build()));
      errors.addAll(rule.applyOnValue(thirdRowBuilder.build()));
      assertThat(errors).isEmpty();
    }

    @Test
    @SneakyThrows
    void shouldFindErrorsOfBothTypes() {
      IgsOverviewCsv row = firstRowBuilder.build();
      IgsOverviewCsv row2 = secondRowBuilder.build();
      row.setMeldetatbestand("");
      row.setDateOfSequencing(DATE_WRONG_FORMAT);
      row2.setLabSequenceId(null);
      row2.setUploadDate("SomeWrongValue");
      List<ValidationError> errors =
          Stream.of(row, row2).map(rule::applyOnValue).flatMap(List::stream).toList();
      assertThat(errors).hasSize(4);
      assertThat(errors.stream().filter(e -> e.getErrorCode() == ErrorCode.DATE_FORMAT).toList())
          .hasSize(2);
      assertThat(errors.stream().filter(e -> e.getErrorCode() == ErrorCode.REQUIRED_FIELD).toList())
          .hasSize(2);
    }
  }

  private void mockMessageWithRowNumber(IgsOverviewCsv row) {
    String rowNumber = String.valueOf(row.getRowNumber());
    when(messageSourceWrapper.getMessage(ERROR_PREFIX, rowNumber))
        .thenReturn(MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER.formatted(rowNumber));
  }

  private void mockMessageForErrorDateFormat(
      MessageSourceWrapper messageSourceWrapper, String columnName, String invalidValue) {
    when(messageSourceWrapper.getMessage(ERROR_DATE_FORMAT, columnName, invalidValue))
        .thenReturn(MOCKED_ERROR_MESSAGE_WITH_TWO_PLACEHOLDERS.formatted(invalidValue, columnName));
  }

  private void mockMessageForErrorRequired(
      MessageSourceWrapper messageSourceWrapper, String columnName) {
    when(messageSourceWrapper.getMessage(ERROR_REQUIRED, columnName))
        .thenReturn(MOCKED_ERROR_MESSAGE_WITH_PLACEHOLDER.formatted(columnName));
  }
}
