package org.hollaemor.todo.domain;

import java.util.Optional;

public record UpdateTodoCommand(Optional<String> descriptionOptional, Optional<Todo.Status> statusOptional) {

}
