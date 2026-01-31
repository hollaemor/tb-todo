package org.hollaemor.todo.domain;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
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

    public Todo getTodo(TodoId todoId) {
        return todoRepository.findById(todoId).orElseThrow(() -> new TodoNotFoundException(todoId));
    }

    @Transactional
    public Todo updateTodo(TodoId todoId, UpdateTodoCommand command) {

        var todo = todoRepository.findByIdForUpdate(todoId).orElseThrow(() -> new TodoNotFoundException(todoId));

        if (todo.isPastDue()) {
            throw new TodoPastDueException();
        }

        var persistenceRequired = new AtomicBoolean();

        command.descriptionOptional().ifPresent(
                description -> {
                    if (!todo.getDescription().equals(description)) {
                        todo.setDescription(description);
                        persistenceRequired.set(true);
                    }
                });
        command.statusOptional().ifPresent(status -> {

            if (status == Todo.Status.PAST_DUE) {
                throw new IllegalTodoStatusUpdateException("Updating the todo to this status is forbidden");
            }

            if (status == Todo.Status.DONE && todo.isNotDone()) {
                todo.markDone();
                persistenceRequired.set(true);
            } else if (status == Todo.Status.NOT_DONE && todo.isDone()) {
                todo.markNotDone();
                persistenceRequired.set(true);
            }
        });
        if (persistenceRequired.get()) {

            return todoRepository.save(todo);
        }
        return todo;

    }
}
