package org.hollaemor.todo.infrastructure.repository;

import java.util.UUID;

import org.hollaemor.todo.domain.TodoRepository;
import org.hollaemor.todo.domain.Todo;
import org.hollaemor.todo.domain.TodoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;

import java.util.Objects;
import java.util.Optional;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface JpaTodoRepository extends JpaRepository<TodoEntity, UUID>, TodoRepository {

    default Todo save(Todo todo) {
        var entity = TodoEntity.from(todo);

        if (Objects.isNull(entity.getCreatedAt())) {
            entity.setCreatedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        }

        if (Objects.isNull(entity.getId())) {
            entity.setId(UUID.randomUUID());
        }
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

}
