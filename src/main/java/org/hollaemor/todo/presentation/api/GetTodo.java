package org.hollaemor.todo.presentation.api;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.hollaemor.todo.domain.Todo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
class GetTodo {

    private UUID id;
    private String description;
    private String status;

    @JsonProperty("creation_datetime")
    private ZonedDateTime creationDateTime;

    @JsonProperty("due_datetime")
    private ZonedDateTime dueDateTime;

    @JsonProperty("done_datetime")
    private ZonedDateTime doneDateTime;

    static GetTodo from(Todo todo) {
        return new GetTodo(
                todo.getId().value(),
                todo.getDescription(),
                todo.getStatus().value(),
                todo.getCreatedAt(),
                todo.getDueDateTime(),
                todo.getDoneDateTime());

    }
}
