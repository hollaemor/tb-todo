package org.hollaemor.todo.presentation.api;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.hollaemor.todo.domain.Todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
class GetTodo {

    private UUID id;
    private String description;
    private Todo.Status status;
    private ZonedDateTime creationDateTime;
    private ZonedDateTime dueDateTime;
    private ZonedDateTime doneDateTime;

    static GetTodo from(Todo todo) {
        return new GetTodo(
                todo.getId().value(),
                todo.getDescription(),
                todo.getStatus(),
                todo.getCreatedAt(),
                todo.getDueDateTime(),
                todo.getDoneDateTime());

    }
}
