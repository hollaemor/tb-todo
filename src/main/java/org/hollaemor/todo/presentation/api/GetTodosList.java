package org.hollaemor.todo.presentation.api;

import java.util.List;

import org.hollaemor.todo.domain.GetTodosResult;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class GetTodosList {

    private List<GetTodo> todos;
    private int page;
    private long totalCount;

    static GetTodosList from(GetTodosResult result) {
        return new GetTodosList(
                result.todos().stream().map(GetTodo::from).toList(),

                result.page(), result.totalCount());
    }

}
