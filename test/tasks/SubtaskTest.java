package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    @Test
    void subtasksAreEqualIfSameId() {
        Subtask firstSubtask = new Subtask(
                1,
                "First subtask name",
                "First subtask description",
                TaskStatus.NEW
        );
        firstSubtask.setId(1);

        Task secondSubtask = new Subtask(
                5,
                "Second subtask name",
                "Second subtask description",
                TaskStatus.DONE
        );
        secondSubtask.setId(1);
        assertEquals(firstSubtask, secondSubtask);
    }
}