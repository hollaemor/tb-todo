package org.hollaemor.todo.presentation.api;

import java.time.ZonedDateTime;

import org.hollaemor.todo.domain.CreateTodoCommand;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class CreateTodo {

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "due_datetime is required")
    @Future(message = "due_datetime should be in the future")
    @JsonProperty("due_datetime")
    private ZonedDateTime dueDateTime;

    CreateTodoCommand toDomain() {
        return new CreateTodoCommand(description, dueDateTime);
    }
}
