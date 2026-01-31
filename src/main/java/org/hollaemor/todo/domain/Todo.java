package org.hollaemor.todo.domain;

import java.time.ZoneId;
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

    public boolean isPastDue() {
        return status == Status.PAST_DUE;
    }

    public void markDone() {
        this.status = Status.DONE;
        this.doneDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public void markNotDone() {
        this.status = Status.NOT_DONE;
        this.doneDateTime = null;
    }

    public boolean isDone() {
        return status == Status.DONE;
    }

    public boolean isNotDone() {
        return !isDone();
    }

}
