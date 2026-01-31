package org.hollaemor.todo.domain;

import java.time.ZonedDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Todo {

    private TodoId id;
    private String description;
    private ZonedDateTime createdAt;
    private ZonedDateTime dueDateTime;
    private ZonedDateTime doneDateTime;
    Status status;

    public enum Status {
        DONE, NOT_DONE, PAST_DUE
    }

}
