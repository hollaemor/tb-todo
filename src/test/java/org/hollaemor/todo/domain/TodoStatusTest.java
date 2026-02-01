package org.hollaemor.todo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.stream.Stream;

import org.hollaemor.todo.domain.Todo.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class TodoStatusTest {

    @Test
    void whenStringIsNullThenNullIsReturned() {
        assertThat(Status.from(null)).isNull();
    }

    @ParameterizedTest
    @CsvSource({
            "not done, NOT_DONE",
            "done, DONE",
            "past due, PAST_DUE"
    })
    void testValidStringAreConverted(String status, Status expected) {
        assertThat(Status.from(status)).isEqualTo(expected);
    }

    @Test
    void exceptionRaisedForInvalidStatusString() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> Status.from("invalid"))
                .withMessageContaining("Invalid Status value");
    }

    @ParameterizedTest
    @MethodSource("statusValues")
    void testStatusHasRightValue(Status status, String expectedValue) {
        assertThat(status.value()).isEqualTo(expectedValue);
    }

    static Stream<Arguments> statusValues() {
        return Stream.of(
                Arguments.of(Status.DONE, "done"),
                Arguments.of(Status.NOT_DONE, "not done"),
                Arguments.of(Status.PAST_DUE, "past due"));
    }
}
