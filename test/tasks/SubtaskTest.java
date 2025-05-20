package tasks;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    @Test
    void subtasksAreEqualIfSameId() {
        Subtask firstSubtask = new Subtask(1, "First subtask name", "First subtask description",
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, Month.MAY, 25, 12, 0));
        firstSubtask.setId(1);

        Task secondSubtask = new Subtask(1, "Second subtask name", "Second subtask description",
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, Month.MAY, 25, 12, 0));
        secondSubtask.setId(1);
        assertEquals(firstSubtask, secondSubtask);
    }
}