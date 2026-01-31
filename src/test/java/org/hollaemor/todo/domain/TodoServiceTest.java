package org.hollaemor.todo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.BDDMockito.given;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    TodoRepository todoRepository;

    @InjectMocks
    TodoService todoService;

    @Test
    void testTodoCreated() {

        // given
        given(todoRepository.save(BDDMockito.any()))
                .willAnswer(answer -> {
                    Todo todo = answer.getArgument(0);
                    todo.setId(new TodoId(UUID.randomUUID()));
                    return todo;
                });

        // when
        var todo = todoService.createTodo(new CreateTodoCommand("Water the plants", ZonedDateTime.now().plusDays(3)));

        // then
        assertThat(todo.getId().value()).isNotNull();
        assertThat(todo.getDoneDateTime()).isNull();
        assertThat(todo.getStatus()).isEqualTo(Todo.Status.NOT_DONE);
        assertThat(todo.getDescription()).isEqualTo("Water the plants");
        assertThat(todo.getCreatedAt()).isCloseTo(ZonedDateTime.now(), within(Duration.ofSeconds(1)));
    }

}
