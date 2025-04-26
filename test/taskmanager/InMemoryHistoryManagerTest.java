package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import tasks.Task;
import tasks.TaskStatus;

class InMemoryHistoryManagerTest {
    static HistoryManager historyManager;
    static Task task;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("Task name", "Task description", TaskStatus.NEW);
    }

    @Test
    void addSingleTask() {
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size(), "Expected 1");
    }

    @Test
    void removeTask() {
        for (int i = 0; i < 3; i++) {
            Task newTask = new Task(i+1, "Task name", "Task description", TaskStatus.NEW);
            historyManager.add(newTask);
        }
        assertEquals(3, historyManager.getHistory().size(), "Expected 3");
        historyManager.remove(2);
        assertEquals(2, historyManager.getHistory().size(), "Expected 2");
    }

    @Test
    void firstBecomeLast() {
        for (int i = 0; i < 3; i++) {
            Task newTask = new Task(i+1, "Task name", "Task description", TaskStatus.NEW);
            historyManager.add(newTask);
        }
        assertEquals(3, historyManager.getHistory().getLast().getId(), "Expected 3");
        historyManager.add(new Task(1, "Task name", "Task description", TaskStatus.NEW));
        assertEquals(1, historyManager.getHistory().getLast().getId(), "Expected 1");
    }
}
