package org.hollaemor.todo.domain;

public interface TodoEventPublisher {
  void publishEvent(TodoEvent event);
}
