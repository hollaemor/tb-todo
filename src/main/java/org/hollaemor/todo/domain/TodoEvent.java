package org.hollaemor.todo.domain;

public sealed interface TodoEvent permits TodoCreatedEvent, TodoUpdatedEvent {

}
