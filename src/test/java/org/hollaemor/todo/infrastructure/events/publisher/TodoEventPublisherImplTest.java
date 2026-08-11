package org.hollaemor.todo.infrastructure.events.publisher;

import static org.mockito.Mockito.verify;

import org.hollaemor.todo.domain.TodoCreatedEvent;
import org.hollaemor.todo.domain.TodoId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class TodoEventPublisherImplTest {

  @Mock
  ApplicationEventPublisher applicationEventPublisher;

  @InjectMocks
  TodoEventPublisherImpl eventPublisherImpl;

  @Test
  void testApplicationPublished() {
    var event = new TodoCreatedEvent(TodoId.newInstance());

    eventPublisherImpl.publishEvent(event);

    verify(applicationEventPublisher).publishEvent(event);
  }

}
