package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

class InMemoryHistoryManagerTest {
    static HistoryManager historyManager;
    static Task task;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task = new Task(1,"NewTask1", "Description newTask1", TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, Month.AUGUST, 13, 14,0));
    }

    @Test
    void addSingleTask() {
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void removeTask() {
        for (int i = 0; i < 3; i++) {
            Task newTask = new Task(i+1, "NewTask1", "Description newTask1", TaskStatus.NEW,
                    Duration.ofMinutes(60),
                    LocalDateTime.of(2025, Month.AUGUST, i+1, 14,0));
                    historyManager.add(newTask);
        }
        assertEquals(3, historyManager.getHistory().size());
        historyManager.remove(2);
        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void firstBecomeLast() {
        for (int i = 0; i < 3; i++) {
            Task newTask = new Task(i+1, "NewTask1", "Description newTask1", TaskStatus.NEW,
                    Duration.ofMinutes(60),
                    LocalDateTime.of(2025, Month.AUGUST, i+1, 14,0));
                    historyManager.add(newTask);
        }
        assertEquals(3, historyManager.getHistory().getLast().getId(), "Expected 3");
        historyManager.add(new Task(1, "NewTask1", "Description newTask1", TaskStatus.NEW,
                Duration.ofMinutes(60),
                LocalDateTime.of(2025, Month.AUGUST, 1, 14,0)));
        assertEquals(1, historyManager.getHistory().getLast().getId(), "Expected 1");
        assertEquals(3, historyManager.getHistory().size());
    }
}