package org.hollaemor.todo.infrastructure.repository;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hollaemor.todo.domain.Todo;
import org.hollaemor.todo.domain.TodoId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "todos")
class TodoEntity implements Serializable {

    @Id
    private UUID id;

    private String description;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "due_date_time")
    private ZonedDateTime dueDateTime;

    @Column(name = "done_date_time")
    private ZonedDateTime doneDateTime;

    @Enumerated(EnumType.STRING)
    private Todo.Status status;

    static TodoEntity from(final Todo todo) {
        var entity = TodoEntity.builder()
                .createdAt(todo.getCreatedAt())
                .description(todo.getDescription())
                .status(todo.getStatus())
                .dueDateTime(todo.getDueDateTime())
                .doneDateTime(todo.getDoneDateTime())
                .build();

        entity.setId(Objects.isNull(todo.getId()) ? null : todo.getId().value());
        return entity;
    }

    Todo toDomain() {
        return Todo.builder()
                .description(description)
                .createdAt(createdAt)
                .dueDateTime(dueDateTime)
                .doneDateTime(doneDateTime)
                .status(status)
                .id(new TodoId(id))
                .build();
    }
}
