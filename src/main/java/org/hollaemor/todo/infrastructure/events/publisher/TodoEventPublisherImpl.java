package org.hollaemor.todo.infrastructure.events.publisher;

import org.hollaemor.todo.domain.TodoEvent;
import org.hollaemor.todo.domain.TodoEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
class TodoEventPublisherImpl implements TodoEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public TodoEventPublisherImpl(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public void publishEvent(TodoEvent event) {
    applicationEventPublisher.publishEvent(event);
  }

}
