package org.hollaemor.todo.presentation.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Stream;

import org.hollaemor.todo.domain.TodoService;
import org.hollaemor.todo.domain.InvalidTodoCreationException;
import org.hollaemor.todo.domain.Todo;
import org.hollaemor.todo.domain.TodoId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.BDDMockito;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import jakarta.inject.Inject;

@WebMvcTest(TodoController.class)
@AutoConfigureRestTestClient
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
            BDDMockito.verifyNoInteractions(todoService);
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
            BDDMockito.verifyNoInteractions(todoService);
        }

        @Test
        @MethodSource("invalidDates")
        @DisplayName("Bad request is returned for invalid due date")
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
            BDDMockito.verifyNoInteractions(todoService);
        }

        static Stream<ZonedDateTime> invalidDates() {
            return Stream.of(
                    ZonedDateTime.now().minusDays(2),
                    ZonedDateTime.now());
        }

        @Test
        @DisplayName("Todo is successfully created when request is valid")
        void whenRequestIsValidThenTodoIsCreated() {
            BDDMockito.given(todoService.createTodo(any()))
                    .willReturn(TODO_RESPONSE);

            restTestClient.post().uri("/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""
                            {
                                "description": "Drink some water",
                                "dueDateTime": "%s"
                            }

                            """
                            .formatted(ZonedDateTime.now().plusDays(5).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
                    .exchange().expectStatus().isCreated()
                    .expectHeader().exists("Location")
                    .expectBody(GetTodo.class)
                    .consumeWith(result -> {
                        var todo = result.getResponseBody();
                        assertThat(todo.getId()).isEqualTo(TODO_RESPONSE.getId().value());
                        assertThat(todo.getStatus()).isEqualTo(TODO_RESPONSE.getStatus());
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

            BDDMockito.given(todoService.createTodo(any())).willThrow(new InvalidTodoCreationException("Oops!"));

            restTestClient.post().uri("/todos")
                    .body(new CreateTodo("Play football", ZonedDateTime.now().plusHours(4)))
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatusCode.valueOf(422))
                    .expectBody().jsonPath("$.detail").isEqualTo("Oops!");

            verify(todoService).createTodo(any());
        }
    }

}
