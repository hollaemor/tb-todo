package org.hollaemor.todo.domain;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.springframework.util.StringUtils;

// NOTE: the assumption is that a todo is created with the not_done status
public record CreateTodoCommand(String description, ZonedDateTime dueDateTime) {
    public CreateTodoCommand {

        if (!StringUtils.hasText(description)) {
            throw new InvalidTodoCreationException("Todo description cannot be empty");
        }

        if (Objects.isNull(dueDateTime)) {
            throw new InvalidTodoCreationException("Due datetime cannot be null");
        }

        var currentDateTime = ZonedDateTime.now(ZoneId.of("UTC"));

        if (dueDateTime.isBefore(currentDateTime) || dueDateTime.isEqual(currentDateTime)) {
            throw new InvalidTodoCreationException("Due datetime can only be in the future");
        }
    }
}
