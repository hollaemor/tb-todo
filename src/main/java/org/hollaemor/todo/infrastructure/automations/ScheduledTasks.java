package org.hollaemor.todo.infrastructure.automations;

import org.hollaemor.todo.domain.TodoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class ScheduledTasks {

    private final TodoRepository todoRepository;

    @Transactional
    @Scheduled(fixedDelayString = "${todo.overdue.fixed.delay.seconds}000")
    void updateOverdueItems() {
        try {
            var updateCount = todoRepository.updateOverdueTodos();
            log.info("{} overdue todo(s) updated", updateCount);

        } catch (Exception ex) {
            log.error("An error occured while updating overdue todos", ex);

        }

    }

}
