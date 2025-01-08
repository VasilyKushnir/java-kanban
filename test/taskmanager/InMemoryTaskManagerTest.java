package taskmanager;

import tasks.Epic;
import tasks.Task;
import tasks.Subtask;
import tasks.TaskStatus;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InMemoryTaskManagerTest {
    static Task task;
    static Task changedTask;
    static Epic epic;
    static Epic changedEpic;
    static Subtask subtask;
    static Subtask changedSubtask;
    static TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        task = new Task("Task name", "Task description", TaskStatus.NEW);
        changedTask = new Task("Changed task name", "Changed task description", TaskStatus.IN_PROGRESS);
        epic = new Epic("Epic name", "Epic description");
        changedEpic = new Epic("Changed epic name", "Changed epic description");
        subtask = new Subtask(1, "Subtask name", "Subtask description", TaskStatus.IN_PROGRESS);
        changedSubtask = new Subtask(
                1,
                "Changed subtask name",
                "Changed subtask description",
                TaskStatus.IN_PROGRESS
        );
    }

    @Test
    void createTask() {
        assertEquals(0, taskManager.getTaskList().size(), "Expected 0");
        taskManager.createTask(task);
        assertEquals(1, taskManager.getTaskList().size(), "Expected 1");
        assertEquals("Task name", taskManager.getTask(task.getId()).getName());
        assertEquals("Task description", taskManager.getTask(task.getId()).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getTask(task.getId()).getStatus());
    }

    @Test
    void createEpic() {
        assertEquals(0, taskManager.getEpicList().size(), "Expected 0");
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getEpicList().size(), "Expected 1");
        assertEquals("Epic name", taskManager.getEpic(epic.getId()).getName());
        assertEquals("Epic description", taskManager.getEpic(epic.getId()).getDescription());
    }

    @Test
    void createSubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertEquals(1, taskManager.getSubtaskList().size());
        Subtask firstEpicSubtask = taskManager.getSubtasksForEpic(1).getFirst();
        assertEquals("Subtask name", firstEpicSubtask.getName());
        assertEquals("Subtask description", firstEpicSubtask.getDescription());
    }

    @Test
    void createSubtaskWillChangeEpicStatus() {
        taskManager.createEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus());
        taskManager.createSubtask(new Subtask(
                1,
                "Subtask name",
                "Subtask description",
                TaskStatus.IN_PROGRESS
        ));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void createSubtaskIsImpossibleIfEpicIdIsIncorrect() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask(
                5,
                "Subtask name",
                "Subtask description",
                TaskStatus.IN_PROGRESS
        ));
        assertNull(taskManager.getSubtasksForEpic(5));
    }

    @Test
    void removeTask() {
        taskManager.createTask(task);
        assertEquals(1, taskManager.getTaskList().size(), "Expected 1");
        taskManager.removeTask(1);
        assertEquals(0, taskManager.getTaskList().size(), "Expected 0");
    }

    @Test
    void removeEpicWithNoSubtasks() {
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getEpicList().size(), "Expected 1");
        taskManager.removeEpic(1);
        assertEquals(0, taskManager.getEpicList().size(), "Expected 0");
    }

    @Test
    void removeEpicWithSubtasks() {
        taskManager.createEpic(epic);
        for (int i = 0; i < 3; i++) {
            taskManager.createSubtask(new Subtask(
                    1,
                    "Subtask name",
                    "Subtask description",
                    TaskStatus.IN_PROGRESS
            ));
        }
        ArrayList<Subtask> subtasksForEpic = taskManager.getSubtasksForEpic(1);
        assertEquals(3, subtasksForEpic.size(), "Expected 3");
        taskManager.removeEpic(1);
        assertEquals(0, taskManager.getEpicList().size(), "Expected 0");
        assertNull(taskManager.getSubtasksForEpic(1));
    }

    @Test
    void removeSubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(changedSubtask);
        assertEquals(2, taskManager.getSubtasksForEpic(1).size());
        taskManager.removeSubtask(2);
        assertEquals(1, taskManager.getSubtasksForEpic(1).size());
        assertEquals("Changed subtask name", taskManager.getSubtasksForEpic(1).getFirst().getName());
    }

    @Test
    void removeSubtaskWillChangeEpicStatus() {
        taskManager.createEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus());
        taskManager.createSubtask(new Subtask(
                1,
                "Subtask name",
                "Subtask description",
                TaskStatus.DONE
        ));
        taskManager.createSubtask(new Subtask(
                1,
                "Subtask name",
                "Subtask description",
                TaskStatus.IN_PROGRESS
        ));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
        taskManager.removeSubtask(3);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void removeTaskIsImpossibleIfKeyIsIncorrect() {
        taskManager.createTask(task);
        assertEquals(1, taskManager.getTaskList().size(), "Expected 1");
        taskManager.removeTask(5);
        assertEquals(1, taskManager.getTaskList().size(), "Expected 1");
    }

    @Test
    void removeEpicIsImpossibleIfKeyIsIncorrect() {
        taskManager.createEpic(epic);
        taskManager.removeEpic(5);
        assertEquals(1, taskManager.getEpicList().size(), "Expected 1");
    }

    @Test
    void removeSubtaskIsImpossibleIfIdIsIncorrect() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(changedSubtask);
        taskManager.removeSubtask(5);
        assertFalse(taskManager.getSubtaskList().contains(5));
    }

    @Test
    void clearTasks() {
        for (int i = 0; i < 10; i++) {
            taskManager.createTask(new Task("Task name", "Task description", TaskStatus.NEW));
        }
        assertEquals(10, taskManager.getTaskList().size(), "Expected 10");
        taskManager.clearTasks();
        assertEquals(0, taskManager.getTaskList().size(), "Expected 0");
    }

    @Test
    void clearEpicsWithNoSubtasks() {
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getEpicList().size(), "Expected 1");
        taskManager.clearEpics();
        assertEquals(0, taskManager.getEpicList().size(), "Expected 0");
    }

    @Test
    void clearEpicsWithSubtasks() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createEpic(new Epic("New epic name", "New epic description"));
        taskManager.createSubtask(new Subtask(
                3,
                "Another subtask name",
                "Another subtask description",
                TaskStatus.DONE
        ));
        assertEquals(1, taskManager.getSubtasksForEpic(1).size(), "Expected 1");
        assertEquals(1, taskManager.getSubtasksForEpic(3).size(), "Expected 1");
        taskManager.clearEpics();
        assertNull(taskManager.getSubtasksForEpic(1));
        assertNull(taskManager.getSubtasksForEpic(3));
    }

    @Test
    void clearSubtasks() {
        taskManager.createEpic(epic);
        taskManager.createEpic(changedEpic);
        taskManager.createSubtask(subtask);
        for (int i = 0; i < 3; i++) {
            taskManager.createSubtask(new Subtask(
                    2,
                    "Changed subtask name",
                    "Changed subtask description",
                    TaskStatus.IN_PROGRESS
            ));
        }
        assertEquals(4, taskManager.getSubtaskList().size());
        taskManager.clearSubtasks();
        assertEquals(0, taskManager.getSubtaskList().size());
    }

    @Test
    void clearSubtasksWillChangeEpicStatus() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask(
                1,
                "Subtask name",
                "Subtask description",
                TaskStatus.DONE
        ));
        taskManager.createEpic(new Epic(
                "New epic name",
                "New epic description"
        ));
        taskManager.createSubtask(new Subtask(
                3,
                "New subtask name",
                "New subtask description",
                TaskStatus.IN_PROGRESS
        ));
        assertEquals(TaskStatus.DONE, taskManager.getEpicList().getFirst().getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicList().getLast().getStatus());
        taskManager.clearSubtasks();
        assertEquals(TaskStatus.NEW, taskManager.getEpicList().getFirst().getStatus());
        assertEquals(TaskStatus.NEW, taskManager.getEpicList().getLast().getStatus());
    }

    @Test
    void getTask() {
        taskManager.createTask(task);
        Task returnedTask = taskManager.getTask(1);
        assertEquals(task, returnedTask);
        assertEquals("Task name", returnedTask.getName());
        assertEquals("Task description", returnedTask.getDescription());
        assertEquals(TaskStatus.NEW, returnedTask.getStatus());
    }

    @Test
    void getEpic() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        Epic returnedEpic = taskManager.getEpic(1);
        assertEquals(epic, returnedEpic);
        assertEquals("Epic name", returnedEpic.getName());
        assertEquals("Epic description", returnedEpic.getDescription());
        assertEquals(2, returnedEpic.getSubtaskIds().getFirst());
    }

    @Test
    void getSubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        Subtask returnedSubtask = taskManager.getSubtask(2);
        assertEquals(subtask, returnedSubtask);
        assertEquals("Subtask name", returnedSubtask.getName());
        assertEquals("Subtask description", returnedSubtask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, returnedSubtask.getStatus());
    }

    @Test
    void getSubtasksForEpic() {
        taskManager.createEpic(epic);
        for (int i = 0; i < 3; i++) {
            taskManager.createSubtask(new Subtask(
                    1,
                    "Subtask name",
                    "Subtask description",
                    TaskStatus.IN_PROGRESS
            ));
        }
        ArrayList<Subtask> subtasksForEpic = taskManager.getSubtasksForEpic(1);
        assertEquals(3, subtasksForEpic.size());
    }

    @Test
    void getTaskIsImpossibleIfKeyIsIncorrect() {
        taskManager.createTask(task);
        Task returnedTask = taskManager.getTask(5);
        assertNull(returnedTask, "Null expected");
    }

    @Test
    void getEpicIsImpossibleIfKeyIsIncorrect() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        Epic returnedEpic = taskManager.getEpic(5);
        assertNull(returnedEpic);
    }

    @Test
    void getSubtaskIsImpossibleIfIdIsIncorrect() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertNull(taskManager.getSubtask(5));
    }

    @Test
    void getSubtasksForEpicIsImpossibleIfEpicIdIsIncorrect() {
        taskManager.createEpic(epic);
        for (int i = 0; i < 3; i++) {
            taskManager.createSubtask(new Subtask(
                    1,
                    "Subtask name",
                    "Subtask description",
                    TaskStatus.IN_PROGRESS
            ));
        }
        ArrayList<Subtask> subtasksForEpic = taskManager.getSubtasksForEpic(5);
        assertNull(subtasksForEpic);
    }

    @Test
    void getTaskList() {
        for (int i = 0; i < 3; i++) {
            taskManager.createTask(new Task(
                    "Task name",
                    "Task description",
                    TaskStatus.NEW
            ));
        }
        assertEquals(3, taskManager.getTaskList().size());
    }

    @Test
    void getEpicList() {
        for (int i = 0; i < 3; i++) {
            taskManager.createEpic(new Epic(
                    "Epic name",
                    "Epic description"
            ));
        }
        assertEquals(3, taskManager.getEpicList().size());
    }

    @Test
    void getSubtaskList() {
        taskManager.createEpic(epic);
        for (int i = 0; i < 3; i++) {
            taskManager.createSubtask(new Subtask(
                    1,
                    "Epic name",
                    "Epic description",
                    TaskStatus.IN_PROGRESS
            ));
        }
        assertEquals(3, taskManager.getSubtaskList().size());
    }

    @Test
    void updateTask() {
        taskManager.createTask(task);
        changedTask.setId(1);
        taskManager.updateTask(changedTask);
        assertEquals("Changed task name", taskManager.getTask(changedTask.getId()).getName());
        assertEquals("Changed task description", taskManager.getTask(changedTask.getId()).getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTask(changedTask.getId()).getStatus());
    }

    @Test
    void updateEpic() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertEquals("Subtask name", taskManager.getSubtasksForEpic(1).getFirst().getName());
        changedEpic.setId(1);
        taskManager.updateEpic(changedEpic);
        assertEquals("Changed epic name", taskManager.getEpic(1).getName());
        assertEquals("Changed epic description", taskManager.getEpic(1).getDescription());
        assertEquals("Subtask name", taskManager.getSubtasksForEpic(1).getFirst().getName());
    }

    @Test
    void updateSubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertEquals("Subtask name", taskManager.getSubtask(2).getName());
        changedSubtask.setId(2);
        taskManager.updateSubtask(changedSubtask);
        assertEquals("Changed subtask name", taskManager.getSubtask(2).getName());
    }

    @Test
    void updateSubtaskWillChangeEpicStatus() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask(
                1,
                "Subtask name",
                "Subtask description",
                TaskStatus.NEW
        ));
        assertEquals(TaskStatus.NEW, taskManager.getEpic(1).getStatus());

        Subtask firstNewSubtask = new Subtask(
                1,
                "Subtask name",
                "Subtask description",
                TaskStatus.IN_PROGRESS
        );
        firstNewSubtask.setId(2);
        taskManager.updateSubtask(firstNewSubtask);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(1).getStatus());

        Subtask secondNewSubtask = new Subtask(
                1,
                "Subtask name",
                "Subtask description",
                TaskStatus.DONE
        );
        secondNewSubtask.setId(2);
        taskManager.updateSubtask(secondNewSubtask);
        assertEquals(TaskStatus.DONE, taskManager.getEpic(1).getStatus());
    }

    @Test
    void updateTaskIsImpossibleIfKeyIsIncorrect() {
        taskManager.createTask(task);
        changedTask.setId(5);
        taskManager.updateTask(changedTask);
        assertNull(taskManager.getTask(changedTask.getId()), "Null expected");
    }

    @Test
    void updateEpicIsImpossibleIfKeyIsIncorrect() {
        taskManager.createEpic(epic);
        taskManager.updateEpic(changedEpic);
        assertEquals("Epic name", taskManager.getEpic(1).getName());
        assertEquals("Epic description", taskManager.getEpic(1).getDescription());
    }

    @Test
    void updateSubtaskIsImpossibleIfIdIsIncorrect() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        changedTask.setId(5);
        taskManager.updateSubtask(changedSubtask);
        assertNotEquals("Changed subtask name", taskManager.getSubtasksForEpic(1).getFirst().getName());
    }

    @Test
    void updateSubtaskIsImpossibleIfEpicIdIsIncorrect() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.updateSubtask(new Subtask(
                5,
                "New subtask name",
                "New subtask description",
                TaskStatus.IN_PROGRESS
        ));
        assertEquals("Subtask name", taskManager.getSubtasksForEpic(1).getFirst().getName());
    }

    @Test
    void returnCorrectHistoryList() {
        taskManager.createTask(new Task(
                "Task name",
                "Task description",
                TaskStatus.IN_PROGRESS
        ));
        taskManager.getTask(1);
        taskManager.updateTask(new Task(
                "Changed task name",
                "Changed task description",
                TaskStatus.DONE
        ));
        Task historyTask = taskManager.getHistory().getFirst();
        assertEquals("Task name", historyTask.getName());
        assertEquals("Task description", historyTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, historyTask.getStatus());
    }
}