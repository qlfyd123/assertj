/*
 * Copyright 2012-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.assertj.core.error;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.error.ShouldSatisfyOnlyOnce.shouldSatisfyOnlyOnce;
import static org.assertj.core.presentation.StandardRepresentation.STANDARD_REPRESENTATION;
import static org.assertj.core.testkit.TestData.someInfo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.AssertionInfo;
import org.assertj.core.description.TextDescription;
import org.junit.jupiter.api.Test;

/**
 * Tests for <code>{@link ShouldSatisfyOnlyOnce#create(org.assertj.core.description.Description)}</code>.
 */
class ShouldSatisfyOnlyOnce_create_Test {

  private static final AssertionInfo INFO = someInfo();

  @SuppressWarnings("ConstantConditions")
  @Test
  void should_create_error_message_when_no_elements_were_satisfied() {
    // GIVEN
    List<String> actual = of("Luke", "Leia");
    UnsatisfiedRequirement lukeRequirement = unsatisfiedRequirement("Luke", "Vader");
    UnsatisfiedRequirement leiaRequirement = unsatisfiedRequirement("Leia", "Vader");
    Map<Integer, UnsatisfiedRequirement> unsatisfiedRequirements = new LinkedHashMap<>();
    unsatisfiedRequirements.put(0, lukeRequirement);
    unsatisfiedRequirements.put(1, leiaRequirement);
    ErrorMessageFactory factory = shouldSatisfyOnlyOnce(actual, of(), unsatisfiedRequirements, INFO);
    // WHEN
    String message = factory.create(new TextDescription("Test"), STANDARD_REPRESENTATION);
    // THEN
    String expected = String.format("[Test] %n" +
                                    "Expecting exactly one element of actual:%n" +
                                    "  [\"Luke\", \"Leia\"]%n" +
                                    "to satisfy the requirements but none did:%n" +
                                    "%n" +
                                    "\"Luke\"%n" +
                                    "- element index: 0%n" +
                                    "- error:%n" +
                                    "expected: \"Vader\"%n" +
                                    " but was: \"Luke\"%n" +
                                    "%n" +
                                    "\"Leia\"%n" +
                                    "- element index: 1%n" +
                                    "- error:%n" +
                                    "expected: \"Vader\"%n" +
                                    " but was: \"Leia\"");
    then(normalizeMessage(message)).isEqualToNormalizingWhitespace(expected);
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  void should_create_error_message_when_more_than_one_element_was_satisfied() {
    // GIVEN
    List<String> actual = of("Luke", "Leia", "Yoda");
    UnsatisfiedRequirement yodaRequirement = unsatisfiedRequirement("Yoda", "Luke");
    Map<Integer, UnsatisfiedRequirement> unsatisfiedRequirements = Map.of(2, yodaRequirement);
    ErrorMessageFactory factory = shouldSatisfyOnlyOnce(actual, of("Luke", "Leia"), unsatisfiedRequirements, INFO);
    // WHEN
    String message = factory.create(new TextDescription("Test"), STANDARD_REPRESENTATION);
    // THEN
    String expected = String.format("[Test] %n" +
                                    "Expecting exactly one element of actual:%n" +
                                    "  [\"Luke\", \"Leia\", \"Yoda\"]%n" +
                                    "to satisfy the requirements but these 2 elements did:%n" +
                                    "  [\"Luke\", \"Leia\"]%n" +
                                    "%n" +
                                    "\"Yoda\"%n" +
                                    "- element index: 2%n" +
                                    "- error:%n" +
                                    "expected: \"Luke\"%n" +
                                    " but was: \"Yoda\"");
    then(normalizeMessage(message)).isEqualToNormalizingWhitespace(expected);
  }

  private UnsatisfiedRequirement unsatisfiedRequirement(Object element, Object expected) {
    try {
      assertThat(element).isEqualTo(expected);
      return null; // Should not happen
    } catch (AssertionError e) {
      return new UnsatisfiedRequirement(element, e);
    }
  }

  // remove stacktrace for test
  private String normalizeMessage(String message) {
    return message.replaceAll("(?m)^\\t.*(?:\\r?\\n)?", "")
      .replaceAll("- error: .*?(?=\\r?\\n)", "- error:");
  }

}
