package org.hollaemor.todo.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.hollaemor.todo.domain.Todo;
import org.hollaemor.todo.domain.TodoId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import jakarta.inject.Inject;

@DataJpaTest
class JpaTodoRepositoryTest {

  @Inject
  private TestEntityManager tem;

  @Inject
  private JpaTodoRepository repository;

  @Test
  void testTodoIsPersisted() {

    // given
    var todo = Todo.builder()
        .description("Create some code")
        .status(Todo.Status.DONE)
        .createdAt(ZonedDateTime.now())
        .dueDateTime(ZonedDateTime.now().plusDays(10))
        .build();

    assertThat(todo.getId()).isNull();

    // when
    var persistedDomain = repository.save(todo);

    // then
    var persistedEntity = tem.find(TodoEntity.class, persistedDomain.getId().value());

    assertThat(persistedEntity).isNotNull();

    assertThat(persistedEntity.toDomain()).usingRecursiveComparison()
        .ignoringFields("id", "createdAt").isEqualTo(todo);

    assertThat(persistedDomain.getId().value()).isEqualTo(persistedEntity.getId());
    assertThat(persistedDomain.getCreatedAt()).isCloseTo(ZonedDateTime.now(), within(Duration.ofSeconds(1)));
  }

  @Test
  void whenTodoWithIdExistsThenItIsReturned() {
    var uuid = UUID.randomUUID();

    var entity = new TodoEntity();
    entity.setId(uuid);
    entity.setDescription("Stop the rain");
    entity.setStatus(Todo.Status.DONE);

    tem.persistAndFlush(entity);

    var optTodo = repository.findById(new TodoId(uuid));

    assertThat(optTodo).isPresent();

  }

  @Test
  void whenIdDoesNotExistThenNothingIsReturned() {

    var optTodo = repository.findById(new TodoId(UUID.randomUUID()));
    assertThat(optTodo).isNotPresent();

  }

  @Test
  void testFindByIdForUpdate() {

    var uuid = UUID.randomUUID();

    var entity = new TodoEntity();
    entity.setId(uuid);
    entity.setDescription("Stop the rain");
    entity.setStatus(Todo.Status.DONE);

    tem.persistAndFlush(entity);

    var optTodo = repository.findByIdForUpdate(new TodoId(uuid));

    assertThat(optTodo).isPresent();
  }

  @Test
  void testUpdateOverdueTodos() {

    var entity = new TodoEntity();
    entity.setId(UUID.randomUUID());
    entity.setDescription("The Procastinator");
    entity.setStatus(Todo.Status.NOT_DONE);
    entity.setDueDateTime(ZonedDateTime.now().minusDays(2));

    tem.persistAndFlush(entity);

    var updatedRecords = repository.updateOverdueTodos();
    assertThat(updatedRecords).isEqualTo(1);


    tem.refresh(entity);

    assertThat(entity.getStatus()).isEqualTo(Todo.Status.PAST_DUE);

  }

}
