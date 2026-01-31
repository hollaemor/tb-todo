package org.hollaemor.todo.presentation.api;

import org.hollaemor.todo.domain.TodoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("todos")
@RequiredArgsConstructor
class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<GetTodo> createTodo(@RequestBody @Valid CreateTodo createTodo) {
        var todo = todoService.createTodo(createTodo.toDomain());

        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(todo.getId().value()).toUri())
                .body(GetTodo.from(todo));
    }

}
