package org.hollaemor.todo.presentation.api;

import java.util.Optional;

import org.hollaemor.todo.domain.Todo;
import org.hollaemor.todo.domain.UpdateTodoCommand;

record UpdateTodo(String description, String status) {

    public UpdateTodoCommand toDomain() {
        return new UpdateTodoCommand(Optional.ofNullable(description),
                Optional.ofNullable(status).map(Todo.Status::from));
    }

}
