package tasks;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    void tasksAreEqualIfSameId() {
        Task firstTask = new Task(1, "First task name", "First task description", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, Month.MAY, 25, 12, 0));
        Task secondTask = new Task(1, "Second task name", "Second task description", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, Month.MAY, 25, 12, 0));
        assertEquals(firstTask, secondTask);
    }
}