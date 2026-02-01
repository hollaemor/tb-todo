package org.hollaemor.todo.domain;

import java.util.Optional;

import org.hollaemor.todo.domain.Todo.Status;

public record GetTodosCommand(int page, int pageSize, Optional<Status> optionalStatus) {
    public GetTodosCommand {
        if (page < 0) {
            throw new IllegalArgumentException("page cannot be less than 0");
        }

        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be greater than 0");
        }
    }

}
