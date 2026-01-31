package org.hollaemor.todo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.verify;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Test
    @DisplayName("Exception is raised when todo with ID not found")
    void testExceptionRaisedWhenTodoNotFound() {

        given(todoRepository.findById(any(TodoId.class)))
                .willReturn(Optional.empty());

        assertThatExceptionOfType(TodoNotFoundException.class)
                .isThrownBy(() -> todoService.getTodo(TodoId.newInstance()));
    }

    @Test
    void whenTodoWithIdExistThenItIsReturned() {
        var todo = Todo.builder()
                .id(TodoId.newInstance())
                .status(Todo.Status.PAST_DUE)
                .build();

        given(todoRepository.findById(any(TodoId.class)))
                .willReturn(Optional.of(todo));

        var optTodo = todoRepository.findById(TodoId.newInstance());

        assertThat(optTodo).isPresent();

        assertThat(optTodo.get()).usingRecursiveComparison().ignoringFields("id").isEqualTo(todo);
    }

    @Nested
    class TestUpdates {

        @Test
        void updateFailsWhenTodoNotFound() {
            given(todoRepository.findByIdForUpdate(any()))
                    .willReturn(Optional.empty());

            assertThatExceptionOfType(TodoNotFoundException.class)
                    .isThrownBy(() -> todoService.updateTodo(TodoId.newInstance(), new UpdateTodoCommand(
                            Optional.empty(),
                            Optional.empty())));

            verify(todoRepository, never()).save(any());
        }

        @Test
        void updateFailsWhenTodoIsPastDue() {

            given(todoRepository.findByIdForUpdate(any()))
                    .willReturn(Optional.of(
                            Todo.builder()
                                    .description("Past due guy")
                                    .status(Todo.Status.PAST_DUE)
                                    .build()));

            assertThatExceptionOfType(TodoPastDueException.class)
                    .isThrownBy(() -> todoService.updateTodo(TodoId.newInstance(),
                            new UpdateTodoCommand(Optional.empty(), Optional.empty())));

            verify(todoRepository, never()).save(any());
        }

        @Test
        void updateFailsWhenNewStatusIsPastDue() {
            given(todoRepository.findByIdForUpdate(any()))
                    .willReturn(Optional.of(
                            Todo.builder()
                                    .description("Past due guy")
                                    .status(Todo.Status.DONE)
                                    .build()));

            assertThatExceptionOfType(IllegalTodoStatusUpdateException.class)
                    .isThrownBy(() -> todoService.updateTodo(TodoId.newInstance(),
                            new UpdateTodoCommand(Optional.empty(), Optional.of(Todo.Status.PAST_DUE))));

            verify(todoRepository, never()).save(any());
        }

        @Test
        void testTodoMarkedAsDoneFromUpdate() {
            var todo = Todo.builder()
                    .status(Todo.Status.NOT_DONE)
                    .description("The Verge.com")
                    .dueDateTime(ZonedDateTime.now())
                    .build();

            assertThat(todo.getDoneDateTime()).isNull();

            given(todoRepository.findByIdForUpdate(any())).willReturn(Optional.of(todo));

            given(todoRepository.save(any()))
                    .willAnswer(answer -> answer.getArgument(0));

            var updated = todoService.updateTodo(TodoId.newInstance(),
                    new UpdateTodoCommand(Optional.of("The Onion"), Optional.of(Todo.Status.DONE)));

            assertThat(todo).isEqualTo(updated);

            assertThat(todo.getStatus()).isEqualTo(Todo.Status.DONE);
            assertThat(todo.getDoneDateTime()).isNotNull();
            assertThat(todo.getDescription()).isEqualTo("The Onion");

            verify(todoRepository).save(any());
        }

        @Test
        void testTodoUndoneFromUpdate() {
            var todo = Todo.builder()
                    .status(Todo.Status.DONE)
                    .description("The Verge.com")
                    .dueDateTime(ZonedDateTime.now())
                    .doneDateTime(ZonedDateTime.now())
                    .build();

            assertThat(todo.getDoneDateTime()).isNotNull();

            given(todoRepository.findByIdForUpdate(any())).willReturn(Optional.of(todo));

            given(todoRepository.save(any()))
                    .willAnswer(answer -> answer.getArgument(0));

            var updated = todoService.updateTodo(TodoId.newInstance(),
                    new UpdateTodoCommand(Optional.empty(), Optional.of(Todo.Status.NOT_DONE)));

            assertThat(todo).isEqualTo(updated);

            assertThat(todo.getStatus()).isEqualTo(Todo.Status.NOT_DONE);
            assertThat(todo.getDoneDateTime()).isNull();

            verify(todoRepository).save(any());
        }

        @Test
        void todoNotSavedWhenNoUpdateIsRequired() {

            var todo = Todo.builder()
                    .status(Todo.Status.NOT_DONE)
                    .description("The Verge.com")
                    .dueDateTime(ZonedDateTime.now())
                    .build();

            given(todoRepository.findByIdForUpdate(any())).willReturn(Optional.of(todo));

            todoService.updateTodo(TodoId.newInstance(),
                    new UpdateTodoCommand(Optional.empty(), Optional.of(Todo.Status.NOT_DONE)));

            verify(todoRepository, never()).save(any());
        }

    }

}
