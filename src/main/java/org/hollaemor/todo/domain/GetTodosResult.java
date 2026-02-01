package org.hollaemor.todo.domain;

import java.util.List;

public record GetTodosResult(List<Todo> todos, int page, long totalCount) {

}
