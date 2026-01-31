package org.hollaemor.todo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class CreateTodoCommandTest {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Exception is raised when description is null or empty")
    void testExceptionRaisedWhenDescriptionIsNull(String description) {
        assertThatExceptionOfType(InvalidTodoCreationException.class)
                .isThrownBy(() -> new CreateTodoCommand(null, ZonedDateTime.now()))
                .withMessage("Todo description cannot be empty");
    }

    @Test
    @DisplayName("Exception is raised when dueDateTime is null")
    void testExceptionRaisedWhenDueDateTimeIsNull() {
        assertThatExceptionOfType(InvalidTodoCreationException.class)
                .isThrownBy(() -> new CreateTodoCommand("Read a book", null))
                .withMessage("Due datetime cannot be null");
    }

    @ParameterizedTest
    @MethodSource("invalidDueDates")
    @DisplayName("Exception is raised when dueDateTime is not in the future")
    void testExceptionRaisedWhenDueDateTimeIsNull(ZonedDateTime dueDateTime) {
        assertThatExceptionOfType(InvalidTodoCreationException.class)
                .isThrownBy(() -> new CreateTodoCommand("Read a book", dueDateTime))
                .withMessage("Due datetime can only be in the future");
    }

    @Test
    void testCommandCreatedSuccessfully() {
        var dueDateTime = ZonedDateTime.now().plusDays(3);
        var createCommand = new CreateTodoCommand("Read a book", dueDateTime);

        assertThat(createCommand.description()).isEqualTo("Read a book");
        assertThat(createCommand.dueDateTime()).isEqualTo(dueDateTime);
    }

    static Stream<ZonedDateTime> invalidDueDates() {
        return Stream.of(
                ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now());
    }
}
