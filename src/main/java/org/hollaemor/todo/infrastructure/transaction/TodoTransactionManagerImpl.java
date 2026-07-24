package org.hollaemor.todo.infrastructure.transaction;

import java.util.function.Supplier;

import org.hollaemor.todo.domain.Todo;
import org.hollaemor.todo.domain.TodoTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
class TodoTransactionManagerImpl implements TodoTransactionManager<Todo> {

  private final TransactionTemplate transactionTemplate;

  public TodoTransactionManagerImpl(PlatformTransactionManager transactionManager) {
    transactionTemplate = new TransactionTemplate(transactionManager);
  }

  @Override
  public Todo runInTransaction(Supplier<Todo> supplier) {
    return transactionTemplate.execute(status -> supplier.get());
  }

}
