package org.hollaemor.todo.domain;

public class IllegalTodoStatusUpdateException extends RuntimeException {

    public IllegalTodoStatusUpdateException(String message) {
        super(message);
    }

}
