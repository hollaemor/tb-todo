package org.hollaemor.todo.presentation.api;

import java.util.List;

import org.hollaemor.todo.domain.IllegalTodoStatusUpdateException;
import org.hollaemor.todo.domain.InvalidTodoCreationException;
import org.hollaemor.todo.domain.TodoNotFoundException;
import org.hollaemor.todo.domain.TodoPastDueException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
class TodoExceptionHandler {

    record FieldErrorDetail(String field, String message) {
    }

    record ErrorDetail(String detail, List<FieldErrorDetail> errors) {
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    ErrorDetail handleValidationException(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getAllErrors().stream()
                .map(e -> new FieldErrorDetail(((FieldError) e).getField(), e.getDefaultMessage()))
                .toList();
        return new ErrorDetail("Invalid request", errors);
    }

    @ExceptionHandler(InvalidTodoCreationException.class)
    ProblemDetail invalidCreationException(InvalidTodoCreationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), ex.getMessage());
    }

    @ExceptionHandler(TodoNotFoundException.class)
    ProblemDetail todoNotFound(TodoNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), ex.getMessage());
    }

    @ExceptionHandler(TodoPastDueException.class)
    ProblemDetail pastDueException(TodoPastDueException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleConstraintException(DataIntegrityViolationException ex) {
        log.error("Database constraint violated", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(409),
                "A todo already exists with the same status, description and due datetime");
    }

    @ExceptionHandler(IllegalTodoStatusUpdateException.class)
    ProblemDetail handlerIllegalStatusUpdate(IllegalTodoStatusUpdateException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    ProblemDetail catchAll(RuntimeException ex) {
        log.error("Exception while processing request", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500),
                "Sorry, an exception occurred. Please try again later");
    }

}
