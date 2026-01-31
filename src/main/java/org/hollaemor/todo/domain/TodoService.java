package org.hollaemor.todo.domain;

import java.time.ZoneId;
import java.time.ZonedDateTime;

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

        command.descriptionOptional().ifPresent( todo::setDescription);
        command.statusOptional().ifPresent(status -> {

            switch(status){
                case Todo.Status.DONE:
                    todo.markDone();
                    break;
                case Todo.Status.NOT_DONE:
                    todo.markNotDone();
                    break;
                default:
                    throw new IllegalTodoStatusUpdateException("Updating the todo to this status is forbidden");
            }

        });
        return todoRepository.save(todo);

    }
}
