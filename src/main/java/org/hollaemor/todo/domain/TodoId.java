package org.hollaemor.todo.domain;

import java.util.UUID;

public record TodoId(UUID value) {

    public static TodoId newInstance() {
        return new TodoId(UUID.randomUUID());
    }
}
