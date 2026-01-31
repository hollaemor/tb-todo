package org.hollaemor.todo.presentation.api;

import java.time.ZonedDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import jakarta.inject.Inject;

@JsonTest
 class CreateTodoJsonTest {

    @Inject
    private JacksonTester<CreateTodo> json;

    @Test
    void serialize() throws Exception {
        var createTodo = new CreateTodo("Feed the cow", ZonedDateTime.now());

        Assertions.assertThat(json.write(createTodo)).extractingJsonPathStringValue("@.description").isEqualTo("Feed the cow");
        Assertions.assertThat(json.write(createTodo)).extractingJsonPathStringValue("@.dueDateTime").isEqualTo("2026-01-30");
    }

}
