package org.hollaemor.todo.domain;

public class InvalidTodoCreationException  extends RuntimeException {

    public InvalidTodoCreationException(String message) {
        super(message);
    }
}
