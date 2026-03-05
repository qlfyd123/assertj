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

import org.assertj.core.api.AssertionInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.util.Strings.escapePercent;

/**
 * Creates an error message indicating that an assertion that verifies that requirements are not satisfied only once.
 */
public class ShouldSatisfyOnlyOnce extends BasicErrorMessageFactory {

  // @format:off
  private static final String NO_ELEMENT_SATISFIED_REQUIREMENTS = "%nExpecting exactly one element of actual:%n" +
                                                                  "  %s%n" +
                                                                  "to satisfy the requirements but none did:%n%n" +
                                                                  "%s";
  // @format:on

  // @format:off
  private static final String MORE_THAN_ONE_ELEMENT_SATISFIED_REQUIREMENTS = "%n" +
                                                                             "Expecting exactly one element of actual:%n" +
                                                                             "  %s%n" +
                                                                             "to satisfy the requirements but these %s elements did:%n" +
                                                                             "  %s%n%n" +
                                                                              "  %s";
  // @format:on

  /**
   * Creates a new <code>{@link ShouldSatisfyOnlyOnce}</code>.
   *
   * @param <E> the iterable elements type.
   * @param actual the actual iterable in the failed assertion.
   * @param satisfiedElements the elements which satisfied the requirement
   * @param unsatisfiedElements the elements witch unsatisfied the requirement
   * @param info the current assertion info
   * @return the created {@link ErrorMessageFactory}.
   */
  public static <E> ErrorMessageFactory shouldSatisfyOnlyOnce(Iterable<? extends E> actual,
                                                              List<? extends E> satisfiedElements,
                                                              Map<Integer, UnsatisfiedRequirement> unsatisfiedElements,
                                                              AssertionInfo info) {
    if (satisfiedElements.isEmpty()) {
      return new ShouldSatisfyOnlyOnce(actual, unsatisfiedElements, info);
    } else {
      return new ShouldSatisfyOnlyOnce(actual, satisfiedElements, unsatisfiedElements, info);
    }
  }

  private ShouldSatisfyOnlyOnce(Iterable<?> actual, Map<Integer, UnsatisfiedRequirement> unsatisfiedRequirements,
                                AssertionInfo info) {
    super(NO_ELEMENT_SATISFIED_REQUIREMENTS, actual, unquotedString(describeErrors(unsatisfiedRequirements, info)));
  }

  private ShouldSatisfyOnlyOnce(Iterable<?> actual, List<?> satisfiedElements,
                                Map<Integer, UnsatisfiedRequirement> unsatisfiedRequirements, AssertionInfo info) {
    super(MORE_THAN_ONE_ELEMENT_SATISFIED_REQUIREMENTS, actual, satisfiedElements.size(), satisfiedElements,
          unquotedString(describeErrors(unsatisfiedRequirements, info)));
  }

  private static String describeErrors(Map<Integer, UnsatisfiedRequirement> unsatisfiedElements, AssertionInfo info) {
    return escapePercent(unsatisfiedElements.entrySet().stream()
                                            .map(entry -> entry.getValue().describe(entry.getKey(), info))
                                            .collect(joining("%n%n".formatted())));
  }
}
