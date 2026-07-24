package org.hollaemor.todo.domain;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {

  private final TodoRepository todoRepository;
  private final TodoTransactionManager<Todo> todoTransactionManager;

  public Todo createTodo(CreateTodoCommand createTodoCommand) {
    return todoRepository.save(
        Todo.builder().description(createTodoCommand.description()).dueDateTime(createTodoCommand.dueDateTime())
            .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
            .status(Todo.Status.NOT_DONE).build());
  }

  public Todo getTodo(TodoId todoId) {
    return todoRepository.findById(todoId).orElseThrow(() -> new TodoNotFoundException(todoId));
  }

  public GetTodosResult getTodos(GetTodosCommand command) {
    return todoRepository.findTodos(command);
  }

  public Todo updateTodo(TodoId todoId, UpdateTodoCommand command) {
    command.statusOptional().ifPresent( status -> {
      if (status == Todo.Status.PAST_DUE) {
        throw new IllegalTodoStatusUpdateException("Updating the todo to this status is forbidden");
      }
    });

    var todo = todoRepository.findByIdForUpdate(todoId).orElseThrow(() -> new TodoNotFoundException(todoId));

    if (todo.isPastDue()) {
      throw new TodoPastDueException();
    }

    return todoTransactionManager.runInTransaction(() -> {
      command.descriptionOptional().ifPresent(todo::setDescription);
      command.statusOptional().ifPresent(status -> {
        if (status == Todo.Status.DONE && todo.isNotDone()) {
          todo.markDone();
        } else if (status == Todo.Status.NOT_DONE && todo.isDone()) {
          todo.markNotDone();
        }
      });
      return todoRepository.save(todo);
    });

  }
}
