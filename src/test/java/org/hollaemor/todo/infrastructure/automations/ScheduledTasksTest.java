package org.hollaemor.todo.infrastructure.automations;


import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class ScheduledTasksTest {

    @MockitoSpyBean
    ScheduledTasks scheduledTasks;

    @Test
    void testUpdateOverdueItemsSchedule() {

        await()
                .atMost(Durations.TEN_SECONDS)
                .untilAsserted(() ->

                verify(scheduledTasks).updateOverdueItems());

    }
}
