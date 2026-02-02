package org.hollaemor.todo.presentation.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.hollaemor.todo.domain.TodoService;
import org.hollaemor.todo.domain.UpdateTodoCommand;
import org.hollaemor.todo.domain.GetTodosCommand;
import org.hollaemor.todo.domain.GetTodosResult;
import org.hollaemor.todo.domain.IllegalTodoStatusUpdateException;
import org.hollaemor.todo.domain.InvalidTodoCreationException;
import org.hollaemor.todo.domain.Todo;
import org.hollaemor.todo.domain.TodoId;
import org.hollaemor.todo.domain.TodoNotFoundException;
import org.hollaemor.todo.domain.TodoPastDueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import jakarta.inject.Inject;

@AutoConfigureRestTestClient
@WebMvcTest(TodoController.class)
@ExtendWith(MockitoExtension.class)
class TodoControllerTest {

    static final Todo TODO_RESPONSE = Todo.builder()
            .status(Todo.Status.NOT_DONE)
            .id(new TodoId(UUID.randomUUID()))
            .description("Save the world")
            .createdAt(ZonedDateTime.now())
            .doneDateTime(ZonedDateTime.now().plusDays(3))
            .dueDateTime(ZonedDateTime.now().plusDays(10)).build();

    @Inject
    private RestTestClient restTestClient;

    @MockitoBean
    private TodoService todoService;

    @Captor
    private ArgumentCaptor<GetTodosCommand> getTodosCaptor;

    @Captor
    private ArgumentCaptor<UpdateTodoCommand> updateTodoCaptor;



    @Nested
    class TodoCreationTest {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Unprocessible  entity is returned for null or empty description")
        void whenDescriptionIsNullOrEmpty(String description) {

            restTestClient.post().uri("/todos")
                    .body(new CreateTodo(description, ZonedDateTime.now().plusDays(1)))
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatusCode.valueOf(422))
                    .expectBody().json("""
                            {

                                "detail": "Invalid request",
                                "errors": [
                                    {
                                        "field": "description",
                                        "message": "description is required"
                                     }
                                ]
                            }
                                """);
            verifyNoInteractions(todoService);
        }

        @ParameterizedTest
        @MethodSource("invalidDates")
        @DisplayName("Unprocesible entity is returned for invalid due date: {0}")
        void whenDueDateIsInvalid(ZonedDateTime dueDateTime) {
            restTestClient.post().uri("/todos")
                    .body(new CreateTodo("Clear the snow", dueDateTime))
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatusCode.valueOf(422))
                    .expectBody().json("""
                            {
                                "detail": "Invalid request",
                                "errors": [
                                    {
                                        "field": "dueDateTime",
                                        "message": "due_datetime should be in the future"
                                     }
                                ]
                            }
                                """);
            verifyNoInteractions(todoService);
        }

        @Test
        @MethodSource("invalidDates")
        @DisplayName("Unprocessible entity is returned for invalid due date")
        void whenDueDateIsNull() {
            restTestClient.post().uri("/todos")
                    .body(new CreateTodo("Clear the snow", null))
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatusCode.valueOf(422))
                    .expectBody().json("""
                            {
                                "errors": [
                                    {
                                        "field": "dueDateTime",
                                        "message": "due_datetime is required"
                                     }
                                ]
                            }
                                """);
            verifyNoInteractions(todoService);
        }

        static Stream<ZonedDateTime> invalidDates() {
            return Stream.of(
                    ZonedDateTime.now().minusDays(2),
                    ZonedDateTime.now());
        }

        @Test
        @DisplayName("Todo is successfully created when request is valid")
        void whenRequestIsValidThenTodoIsCreated() {
            given(todoService.createTodo(any()))
                    .willReturn(TODO_RESPONSE);

            restTestClient.post().uri("/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""
                            {
                                "description": "Drink some water",
                                "due_datetime": "%s"
                            }

                            """
                            .formatted(ZonedDateTime.now().plusDays(5).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
                    .exchange().expectStatus().isCreated()
                    .expectHeader().exists("Location")
                    .expectBody(GetTodo.class)
                    .consumeWith(result -> {
                        var todo = result.getResponseBody();
                        assertThat(todo.getId()).isEqualTo(TODO_RESPONSE.getId().value());
                        assertThat(todo.getStatus()).isEqualTo(TODO_RESPONSE.getStatus().value());
                        assertThat(todo.getDescription()).isEqualTo(TODO_RESPONSE.getDescription());
                        assertThat(todo.getDueDateTime()).isEqualTo(TODO_RESPONSE.getDueDateTime());
                        assertThat(todo.getCreationDateTime()).isEqualTo(TODO_RESPONSE.getCreatedAt());
                        assertThat(todo.getDoneDateTime()).isEqualTo(TODO_RESPONSE.getDoneDateTime());
                    });

            verify(todoService).createTodo(any());
        }

        @Test
        @DisplayName("Invalid creation exception is mapped to appropriate response")
        void appropriateResponseIsReturnedForInvalidCreationException() {

            given(todoService.createTodo(any())).willThrow(new InvalidTodoCreationException("Oops!"));

            restTestClient.post().uri("/todos")
                    .body(new CreateTodo("Play football", ZonedDateTime.now().plusHours(4)))
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatusCode.valueOf(422))
                    .expectBody().jsonPath("$.detail").isEqualTo("Oops!");

            verify(todoService).createTodo(any());
        }

        @Test
        void testUnhandledExceptionsAreGraciousllyHandled() {

            given(todoService.createTodo(any())).willThrow(new RuntimeException("Verbose stacktrace"));

            restTestClient.post().uri("/todos")
                    .body(new CreateTodo("Play football", ZonedDateTime.now().plusHours(4)))
                    .exchange()
                    .expectStatus().is5xxServerError()
                    .expectBody().jsonPath("$.detail").isEqualTo("Sorry, an exception occurred. Please try again later");

            verify(todoService).createTodo(any());
        }

    }

    @Nested
    class GetTodoTest {

        @Test
        void whenTodoIdExistsThenReturnOk() {

            given(todoService.getTodo(any()))
                    .willReturn(TODO_RESPONSE);

            restTestClient.get().uri("/todos/{id}", TODO_RESPONSE.getId().value())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(GetTodo.class);

            verify(todoService).getTodo(any());
        }

        @Test
        @DisplayName("404 returned when ID not found")
        void whenTodoIdNotFoundThen404IsReturned() {
            given(todoService.getTodo(any()))
                    .willThrow(new TodoNotFoundException(TODO_RESPONSE.getId()));

            restTestClient.get().uri("/todos/{id}", TODO_RESPONSE.getId().value())
                    .exchange()
                    .expectStatus().isEqualTo(404)
                    .expectBody().jsonPath("$.detail")
                    .isEqualTo("Todo with ID %s was not be found".formatted(TODO_RESPONSE.getId().value()));

        }

        @Test
        void testGetTodos() {

            given(todoService.getTodos(any(GetTodosCommand.class)))
                    .willReturn(
                            new GetTodosResult(List.of(TODO_RESPONSE), 0, 1));

            restTestClient.get().uri("/todos")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(GetTodosList.class)
                    .consumeWith(result -> {
                        var todoList = result.getResponseBody();

                        assertThat(todoList.getTotalCount()).isEqualTo(1);
                        assertThat(todoList.getTodos().get(0).getDescription())
                                .isEqualTo(TODO_RESPONSE.getDescription());
                    });

            verify(todoService).getTodos(getTodosCaptor.capture());

            var command = getTodosCaptor.getValue();
            assertThat(command.page()).isZero();
            assertThat(command.pageSize()).isEqualTo(100);
            assertThat(command.optionalStatus()).isEmpty();
        }

        @Test
        void testGetTodosWithStatus() {

            given(todoService.getTodos(any(GetTodosCommand.class)))
                    .willReturn(
                            new GetTodosResult(List.of(TODO_RESPONSE), 0, 1));

            restTestClient.get().uri("/todos?page=1&pageSize=200&status=done")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(GetTodosList.class)
                    .consumeWith(result -> {
                        var todoList = result.getResponseBody();

                        assertThat(todoList.getTotalCount()).isEqualTo(1);
                        assertThat(todoList.getTodos().get(0).getDescription())
                                .isEqualTo(TODO_RESPONSE.getDescription());
                    });

            verify(todoService).getTodos(getTodosCaptor.capture());

            var command = getTodosCaptor.getValue();
            assertThat(command.page()).isEqualTo(1);
            assertThat(command.pageSize()).isEqualTo(200);
            assertThat(command.optionalStatus()).contains(Todo.Status.DONE);
        }

    }

    @Nested
    class UpdateTodoTest {

        @Test
        @DisplayName("404 returned when ID not found for update")
        void whenTodoIdNotFoundForUpdateThen404IsReturned() {
            given(todoService.updateTodo(any(), any()))
                    .willThrow(new TodoNotFoundException(TODO_RESPONSE.getId()));

            restTestClient.patch().uri("/todos/{id}", TODO_RESPONSE.getId().value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""
                            {
                                "description": "Vacuum the apartment"
                            }

                            """)
                    .exchange()
                    .expectStatus().isEqualTo(404)
                    .expectBody().jsonPath("$.detail").exists();

            verify(todoService).updateTodo(any(), updateTodoCaptor.capture());

            var command = updateTodoCaptor.getValue();
            assertThat(command.statusOptional()).isEmpty();
            assertThat(command.descriptionOptional()).contains("Vacuum the apartment");
        }

        @Test
        void whenTodoIdFoundForUpdateThenTodoIsUpdated() {
            given(todoService.updateTodo(any(), any()))
                    .willReturn(TODO_RESPONSE);

            restTestClient.patch().uri("/todos/{id}", TODO_RESPONSE.getId().value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""
                            {
                                "description": "Vacuum the apartment",
                                "status": "not done"
                            }

                            """)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(GetTodo.class);

            verify(todoService).updateTodo(any(), updateTodoCaptor.capture());

            var command = updateTodoCaptor.getValue();
            assertThat(command.statusOptional()).contains(Todo.Status.NOT_DONE);
            assertThat(command.descriptionOptional()).contains("Vacuum the apartment");
        }

        @Test
        @DisplayName("Prevent update of past due todo")
        void whenTodoIsPastDueThenForbiddenIsReturned() {

            given(todoService.updateTodo(any(), any()))
                    .willThrow(new TodoPastDueException());

            restTestClient.patch().uri("/todos/{id}", TODO_RESPONSE.getId().value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""
                            {
                                "description": "Vacuum the apartment"
                            }
                            """)
                    .exchange()
                    .expectStatus().isForbidden()
                    .expectBody().jsonPath("$.detail")
                    .value(String.class, message -> assertThat(message).contains("can no longer be updated"));

        }

        @Test
        @DisplayName("Prevent manual update to past due")
        void whenUpdateStatusIsPastDueThenReturnForbidden() {

            given(todoService.updateTodo(any(), any()))
                    .willThrow(new IllegalTodoStatusUpdateException("don't do that"));

            restTestClient.patch().uri("/todos/{id}", TODO_RESPONSE.getId().value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""
                            {
                                "description": "Vacuum the apartment",
                                "status": "past due"
                            }
                            """)
                    .exchange()
                    .expectStatus().isForbidden()
                    .expectBody().jsonPath("$.detail").isEqualTo("don't do that");

        }

        @Test
        void whenUniqueConstraintIsViolatedThenReturnConflict() {

            given(todoService.updateTodo(any(), any()))
            .willThrow(new DataIntegrityViolationException("Violation!!!"));

            restTestClient.patch().uri("/todos/{id}", TODO_RESPONSE.getId().value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""
                            {
                                "description": "Vacuum the apartment"
                            }
                            """)
                    .exchange()
                    .expectStatus().isEqualTo(409)
                    .expectBody().jsonPath("$.detail")
                    .value(String.class, message -> assertThat(message).contains("A todo already exists"));

        }
    }
}
