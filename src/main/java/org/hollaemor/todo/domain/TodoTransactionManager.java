package org.hollaemor.todo.domain;

import java.util.function.Supplier;

public interface TodoTransactionManager<T> {
  T runInTransaction(Supplier<T> supplier);
}
