package org.hollaemor.todo.domain;

import java.time.ZonedDateTime;

public record TodoUpdatedEvent(TodoId todoId, ZonedDateTime updatedAt) implements TodoEvent {
}
