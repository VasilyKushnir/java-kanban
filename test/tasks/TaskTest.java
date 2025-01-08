package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    void tasksAreEqualIfSameId() {
        Task firstTask = new Task(
                1,
                "First task name",
                "First task description",
                TaskStatus.NEW
        );

        Task secondTask = new Task(
                1,
                "Second task name",
                "Second task description",
                TaskStatus.IN_PROGRESS
        );
        assertEquals(firstTask, secondTask);
    }
}