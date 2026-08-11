package org.hollaemor.todo.domain;

public record TodoCreatedEvent(TodoId todoId) implements TodoEvent {
}
