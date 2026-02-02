package org.hollaemor.todo.infrastructure.repository;

import java.util.UUID;

import org.hollaemor.todo.domain.TodoRepository;
import org.hollaemor.todo.domain.GetTodosCommand;
import org.hollaemor.todo.domain.GetTodosResult;
import org.hollaemor.todo.domain.Todo;
import org.hollaemor.todo.domain.TodoId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface JpaTodoRepository extends JpaRepository<TodoEntity, UUID>, TodoRepository {

    default Todo save(Todo todo) {
        var entity = TodoEntity.from(todo);
        return this.save(entity).toDomain();
    }

    default Optional<Todo> findById(TodoId todoId) {
        return findById(todoId.value()).map(TodoEntity::toDomain);

    }

    @Query("SELECT t FROM TodoEntity t WHERE t.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TodoEntity> findByIdWithLock(UUID id);

    default Optional<Todo> findByIdForUpdate(TodoId todoId) {
        return findByIdWithLock(todoId.value()).map(TodoEntity::toDomain);

    }

    @Modifying
    @Query("UPDATE TodoEntity t SET t.status = :newStatus WHERE t.dueDateTime < :cutOverDateTime AND t.status = :currentStatus")
    long updateStatuses(Todo.Status currentStatus, ZonedDateTime cutOverDateTime, Todo.Status newStatus);

    @Override
    default long updateOverdueTodos() {
        return updateStatuses(Todo.Status.NOT_DONE, ZonedDateTime.now(ZoneId.of("UTC")), Todo.Status.PAST_DUE);
    }

    @Query(value = "SELECT t FROM TodoEntity t WHERE t.status = :status", countQuery = "SELECT COUNT(t) FROM TodoEntity t WHERE t.status = :status")
    Page<TodoEntity> findAllByStatus(Todo.Status status, Pageable pageable);

    default GetTodosResult findTodos(GetTodosCommand command) {
        var page = PageRequest.of(command.page(), command.pageSize());

        var pagedResult = command.optionalStatus().map(status -> findAllByStatus(status, page)).orElse(findAll(page));

        return new GetTodosResult(
                pagedResult.getContent().stream().map(TodoEntity::toDomain).toList(),
                page.getPageNumber(),
                pagedResult.getTotalElements());
    }

}
