package org.hollaemor.todo.presentation.api;

import java.util.Optional;
import java.util.UUID;

import org.hollaemor.todo.domain.GetTodosCommand;
import org.hollaemor.todo.domain.Todo;
import org.hollaemor.todo.domain.TodoId;
import org.hollaemor.todo.domain.TodoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("todos")
@RequiredArgsConstructor
class TodoController {

    private final TodoService todoService;

    @PostMapping
    ResponseEntity<GetTodo> createTodo(@RequestBody @Valid CreateTodo createTodo) {
        var todo = todoService.createTodo(createTodo.toDomain());

        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(todo.getId().value()).toUri())
                .body(GetTodo.from(todo));
    }

    @GetMapping
    GetTodosList getTodos(@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "100") int pageSize,
            @RequestParam("status") Optional<String> status) {
        return GetTodosList
                .from(todoService.getTodos(GetTodosCommand.from(page, pageSize, status.map(Todo.Status::from))));

    }

    @GetMapping("/{id}")
    GetTodo getById(@PathVariable UUID id) {
        return GetTodo.from(todoService.getTodo(TodoId.of(id)));
    }

    @PatchMapping("/{id}")
    GetTodo updateById(@PathVariable UUID id, @RequestBody UpdateTodo updateTodo) {
        return GetTodo.from(todoService.updateTodo(TodoId.of(id), updateTodo.toDomain()));
    }

}
