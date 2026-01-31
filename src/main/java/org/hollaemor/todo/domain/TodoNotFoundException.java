package org.hollaemor.todo.domain;

public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(TodoId todoId) {
        super("Todo with ID %s was not be found".formatted(todoId.value()));
    }

}
