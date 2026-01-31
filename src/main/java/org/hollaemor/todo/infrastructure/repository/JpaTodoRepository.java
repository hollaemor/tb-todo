package org.hollaemor.todo.infrastructure.repository;

import java.util.UUID;

import org.hollaemor.todo.domain.TodoRepository;
import org.hollaemor.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Objects;
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

}
