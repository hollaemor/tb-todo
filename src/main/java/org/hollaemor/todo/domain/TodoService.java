package org.hollaemor.todo.domain;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public Todo createTodo(CreateTodoCommand createTodoCommand) {
        return todoRepository.save(
                Todo.builder().description(createTodoCommand.description()).dueDateTime(createTodoCommand.dueDateTime())
                        .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                        .status(Todo.Status.NOT_DONE).build());
    }
}
