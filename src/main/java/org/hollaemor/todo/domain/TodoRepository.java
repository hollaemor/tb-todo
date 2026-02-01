package org.hollaemor.todo.domain;

import java.util.Optional;

public interface TodoRepository {

  Todo save(Todo todo);

  Optional<Todo> findById(TodoId todoId);

  Optional<Todo> findByIdForUpdate(TodoId todoId);

  /* Updates todo items that are overdue, returning a count of the updated items */
  long updateOverdueTodos();

  GetTodosResult findTodos(GetTodosCommand command);

}
