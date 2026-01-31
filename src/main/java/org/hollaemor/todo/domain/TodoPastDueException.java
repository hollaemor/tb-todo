package org.hollaemor.todo.domain;

public class TodoPastDueException extends RuntimeException{

    public TodoPastDueException() {
        super("Todo is past due and can no longer be updated");
    }

}
