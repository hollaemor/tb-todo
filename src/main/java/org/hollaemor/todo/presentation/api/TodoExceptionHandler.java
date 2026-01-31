package org.hollaemor.todo.presentation.api;

import java.util.List;

import org.hollaemor.todo.domain.InvalidTodoCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
