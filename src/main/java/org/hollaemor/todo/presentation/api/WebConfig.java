package org.hollaemor.todo.presentation.api;


import org.hollaemor.todo.domain.Todo;
import org.hollaemor.todo.domain.Todo.Status;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

// FIXME: conversion isn't working as expected
@Slf4j
@Configuration
class WebConfig implements WebMvcConfigurer {

    @PostConstruct
    void constructed() {
        log.info("WEB-MVC constructed");
    }

    static class TodoStatusToStringConverter implements Converter<Status, String> {

        @Override
        public String convert(Status source) {
            log.info("Todo formatter called for status {}", source);
            return switch (source) {
                case null -> null;
                case Status.DONE -> "done";
                case Status.NOT_DONE -> "not done";
                case Status.PAST_DUE -> "past due";
            };
        }
    }

    static class StringToTodoStatusConverter implements Converter<String, Status> {

        @Override
        public Status convert(String source) {
            if (null == source) {
                return null;
            }
            return switch (source.toLowerCase()) {
                case "done" -> Todo.Status.DONE;
                case "not done" -> Status.NOT_DONE;
                case "past due" -> Status.PAST_DUE;
                default -> throw new IllegalArgumentException("Invalid status: %s".formatted(source));
            };
        }

    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new TodoStatusToStringConverter());
        registry.addConverter(new StringToTodoStatusConverter());
    }
}
