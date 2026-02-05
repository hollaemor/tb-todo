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
    private Status status;

    public enum Status {

        DONE("done"), NOT_DONE("not done"), PAST_DUE("past due");

        Status(String value) {
            this.value = value;
        }

        private final String value;

        public String value() {
            return value;
        }

        public static Status from(String value) {
            if (null == value) {
                return null;
            }

            return switch(value.toLowerCase()) {
                case "done" -> DONE;
                case "not done" -> NOT_DONE;
                case "past due" -> PAST_DUE;
                default -> throw new IllegalArgumentException("Invalid Status value: %s".formatted(value));
            };
        }
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
