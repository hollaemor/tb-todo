package org.hollaemor.todo.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.Duration;
import java.time.ZonedDateTime;

import org.hollaemor.todo.domain.Todo;
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

}
