package org.hollaemor.todo.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GetTodosCommandTest {

    @Test
    void testExceptionThrownWhenPageIsLessThanZero() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new GetTodosCommand(-1, 0, Optional.empty()))
                .withMessage("page cannot be less than 0");
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 0 })
    void testExceptionThrownWhenPageSizeIsInvalid(int pageSize) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new GetTodosCommand(0, pageSize, Optional.empty()))
                .withMessage("pageSize must be greater than 0");
    }

    @Test
    void testNoExceptionThrownOnValidCreation() {
        assertThatCode(() -> new GetTodosCommand(0, 2, Optional.empty())).doesNotThrowAnyException();
    }

}
