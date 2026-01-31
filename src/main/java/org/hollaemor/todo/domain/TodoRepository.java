package org.hollaemor.todo.domain;

import java.util.Optional;

public interface TodoRepository {

  Todo save(Todo todo);

  Optional<Todo> findById(TodoId todoId);

  Optional<Todo> findByIdForUpdate(TodoId todoId);

}
