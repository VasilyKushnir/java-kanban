package taskmanager;

import tasks.Epic;
import tasks.Task;
import tasks.Subtask;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public abstract class TaskManagerTest<T extends TaskManager>{
    private static Task task;
    private static Task changedTask;
    private static Epic epic;
    private static Epic changedEpic;
    private static Subtask subtask;
    private static Subtask changedSubtask;
    private static TaskManager taskManager;

    abstract T getTaskManager();

    @BeforeEach
    void beforeEach() {
        taskManager = getTaskManager();
        task = new Task("Task1", "Description task1", TaskStatus.NEW, Duration.ofMinutes(90),
                LocalDateTime.of(2025, Month.JUNE, 2, 12, 0));
        changedTask = new Task("ChangedTask1", "Description changedTask1", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(110), LocalDateTime.of(2025, Month.JUNE, 2, 13, 30));
        epic = new Epic("Epic1", "Description epic1");
        changedEpic = new Epic("ChangedEpic1", "Description changedEpic1");
        subtask = new Subtask(1, "Subtask1", "Description subtask1", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(45), LocalDateTime.of(2025, Month.JUNE, 9, 7, 30));
        changedSubtask = new Subtask(1, "ChangedSubtask1", "Description changedSubtask1",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(65),
                LocalDateTime.of(2025, Month.JUNE, 9, 23, 20));
    }

    @Test
    void createTask() {
        assertEquals(0, taskManager.getTaskList().size());
        taskManager.createTask(task);
        assertEquals(1, taskManager.getTaskList().size());
        assertEquals("Task1", taskManager.getTask(task.getId()).getName());
        assertEquals("Description task1", taskManager.getTask(task.getId()).getDescription());
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 2, 12, 0), task.getStartTime());
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 2, 13, 30), task.getEndTime());
        assertEquals(Duration.ofMinutes(90), task.getDuration());
        assertEquals(TaskStatus.NEW, taskManager.getTask(task.getId()).getStatus());
    }

    @Test
    void createEpic() {
        assertEquals(0, taskManager.getEpicList().size());
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getEpicList().size());
        assertEquals("Epic1", taskManager.getEpic(epic.getId()).getName());
        assertEquals("Description epic1", taskManager.getEpic(epic.getId()).getDescription());
    }

    @Test
    void createSubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertEquals(1, taskManager.getSubtaskList().size());
        Subtask firstEpicSubtask = taskManager.getSubtasksForEpic(1).getFirst();
        assertEquals("Subtask1", firstEpicSubtask.getName());
        assertEquals("Description subtask1", firstEpicSubtask.getDescription());
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 9, 7, 30), subtask.getStartTime());
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 9, 8, 15), subtask.getEndTime());
        assertEquals(Duration.ofMinutes(45), subtask.getDuration());
    }

    @Test
    void createSubtaskWillChangeEpicStatus() {
        taskManager.createEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus());
        taskManager.createSubtask(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void createSubtaskWillChangeEpicTime() {
        taskManager.createEpic(epic);
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertEquals(Duration.ZERO, epic.getDuration());
        taskManager.createSubtask(subtask);
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 9, 7, 30), epic.getStartTime());
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 9, 8, 15), epic.getEndTime());
        assertEquals(Duration.ofMinutes(45), epic.getDuration());
        taskManager.createSubtask(changedSubtask);
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 9, 7, 30), epic.getStartTime());
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 10, 0, 25), epic.getEndTime());
        assertEquals(Duration.ofMinutes(110), epic.getDuration());
    }

    @Test
    void createSubtaskIsImpossibleIfEpicIdIsIncorrect() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask(5, "NewSubtask1", "Description newSubtask1",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(90),
                LocalDateTime.of(2025, Month.JUNE, 8, 17, 30)));
        assertNull(taskManager.getSubtasksForEpic(5));
        assertEquals(0, taskManager.getSubtaskList().size());
    }

    @Test
    void removeTask() {
        taskManager.createTask(task);
        assertEquals(1, taskManager.getTaskList().size());
        taskManager.removeTask(1);
        assertEquals(0, taskManager.getTaskList().size());
    }

    @Test
    void removeEpicWithNoSubtasks() {
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getEpicList().size());
        taskManager.removeEpic(1);
        assertEquals(0, taskManager.getEpicList().size());
    }

    @Test
    void removeEpicWithSubtasks() {
        taskManager.createEpic(epic);
        for (int i = 0; i < 3; i++) {
            taskManager.createSubtask(new Subtask(1, "NewSubtask" + (i+1),
                    "Description newSubtask" + (i+1), TaskStatus.IN_PROGRESS, Duration.ofMinutes(90),
                    LocalDateTime.of(2025, Month.JUNE, i+1, 17, 30)));
        }
        ArrayList<Subtask> subtasksForEpic = taskManager.getSubtasksForEpic(1);
        assertEquals(3, subtasksForEpic.size());
        taskManager.removeEpic(1);
        assertEquals(0, taskManager.getEpicList().size());
        assertNull(taskManager.getSubtasksForEpic(1));
        assertEquals(0, taskManager.getSubtaskList().size());
    }

    @Test
    void removeSubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(changedSubtask);
        assertEquals(2, taskManager.getSubtasksForEpic(1).size());
        taskManager.removeSubtask(2);
        assertEquals(1, taskManager.getSubtasksForEpic(1).size());
        assertEquals("ChangedSubtask1", taskManager.getSubtasksForEpic(1).getFirst().getName());
    }

    @Test
    void removeSubtaskWillChangeEpicStatus() {
        taskManager.createEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus());
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(new Subtask(1, "NewSubtask1", "Description newSubtask1",
                TaskStatus.DONE, Duration.ofMinutes(90),
                LocalDateTime.of(2025, Month.JUNE, 12, 17, 30)));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
        taskManager.removeSubtask(2);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void removeSubtaskWillChangeEpicTime() {
        taskManager.createEpic(epic);
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertEquals(Duration.ZERO, epic.getDuration());
        taskManager.createSubtask(subtask);
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 9, 7, 30), epic.getStartTime());
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 9, 8, 15), epic.getEndTime());
        assertEquals(Duration.ofMinutes(45), epic.getDuration());
        taskManager.createSubtask(changedSubtask);
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 9, 7, 30), epic.getStartTime());
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 10, 0, 25), epic.getEndTime());
        assertEquals(Duration.ofMinutes(110), epic.getDuration());
        taskManager.removeSubtask(2);
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 9, 23, 20), epic.getStartTime());
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 10, 0, 25), epic.getEndTime());
        assertEquals(Duration.ofMinutes(65), epic.getDuration());
    }

    @Test
    void clearTasks() {
        for (int i = 0; i < 10; i++) {
            taskManager.createTask(new Task("NewTask" + (i+1), "Description newTask" + (i+1),
                    TaskStatus.NEW, Duration.ofMinutes(60),
                    LocalDateTime.of(2025, Month.JUNE, i+1, 12, 0)));
        }
        assertEquals(10, taskManager.getTaskList().size());
        taskManager.clearTasks();
        assertEquals(0, taskManager.getTaskList().size());
    }

    @Test
    void clearEpicsWithNoSubtasks() {
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getEpicList().size());
        taskManager.clearEpics();
        assertEquals(0, taskManager.getEpicList().size());
    }

    @Test
    void clearEpicsWithSubtasks() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createEpic(changedEpic);
        taskManager.createSubtask(new Subtask(3, "NewSubtask1", "Description newSubtask1",
                TaskStatus.DONE, Duration.ofMinutes(120),
                LocalDateTime.of(2025, Month.AUGUST, 19, 12, 21)));
        assertEquals(1, taskManager.getSubtasksForEpic(1).size());
        assertEquals(1, taskManager.getSubtasksForEpic(3).size());
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
            taskManager.createSubtask(new Subtask(2, "NewSubtask1", "Description newSubtask1",
                    TaskStatus.IN_PROGRESS, Duration.ofMinutes(120),
                    LocalDateTime.of(2025, Month.AUGUST, i+1, 7, 18)));
        }
        assertEquals(4, taskManager.getSubtaskList().size());
        taskManager.clearSubtasks();
        assertEquals(0, taskManager.getSubtaskList().size());
    }

    @Test
    void clearSubtasksWillChangeEpicStatus() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createEpic(changedEpic);
        taskManager.createSubtask(new Subtask(3, "NewSubtask1", "Description newSubtask1",
                TaskStatus.DONE, Duration.ofMinutes(120),
                LocalDateTime.of(2025, Month.AUGUST, 6, 7, 18)));
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicList().getFirst().getStatus());
        assertEquals(TaskStatus.DONE, taskManager.getEpicList().getLast().getStatus());
        taskManager.clearSubtasks();
        assertEquals(TaskStatus.NEW, taskManager.getEpicList().getFirst().getStatus());
        assertEquals(TaskStatus.NEW, taskManager.getEpicList().getLast().getStatus());
    }

    @Test
    void getTask() {
        taskManager.createTask(task);
        Task returnedTask = taskManager.getTask(1);
        assertEquals(task, returnedTask);
        assertEquals("Task1", returnedTask.getName());
        assertEquals("Description task1", returnedTask.getDescription());
        assertEquals(TaskStatus.NEW, returnedTask.getStatus());
    }

    @Test
    void getEpic() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        Epic returnedEpic = taskManager.getEpic(1);
        assertEquals(epic, returnedEpic);
        assertEquals("Epic1", returnedEpic.getName());
        assertEquals("Description epic1", returnedEpic.getDescription());
        assertEquals(2, returnedEpic.getSubtaskIds().getFirst());
    }

    @Test
    void getSubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        Subtask returnedSubtask = taskManager.getSubtask(2);
        assertEquals(subtask, returnedSubtask);
        assertEquals("Subtask1", returnedSubtask.getName());
        assertEquals("Description subtask1", returnedSubtask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, returnedSubtask.getStatus());
    }

    @Test
    void getSubtasksForEpic() {
        taskManager.createEpic(epic);
        for (int i = 0; i < 3; i++) {
            taskManager.createSubtask(new Subtask(1, "NewSubtask" + (i+1),
                    "Description newSubtask" + (i+1), TaskStatus.IN_PROGRESS, Duration.ofMinutes(120),
                    LocalDateTime.of(2025, Month.AUGUST, i+1, 7, 18)));
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
            taskManager.createSubtask(subtask);
        }
        ArrayList<Subtask> subtasksForEpic = taskManager.getSubtasksForEpic(5);
        assertNull(subtasksForEpic);
    }

    @Test
    void getTaskList() {
        for (int i = 0; i < 3; i++) {
            taskManager.createTask(new Task("NewTask" + (i+1), "Description newTask" + (i+1),
                    TaskStatus.NEW, Duration.ofMinutes(60),
                    LocalDateTime.of(2025, Month.JUNE, i+1, 12, 0)));
        }
        assertEquals(3, taskManager.getTaskList().size());
    }

    @Test
    void getEpicList() {
        for (int i = 0; i < 3; i++) {
            taskManager.createEpic(new Epic("NewEpic" + (i+1), "Description newEpic" + (i+1)));
        }
        assertEquals(3, taskManager.getEpicList().size());
    }

    @Test
    void getSubtaskList() {
        taskManager.createEpic(epic);
        for (int i = 0; i < 3; i++) {
            taskManager.createSubtask(new Subtask(1, "NewSubtask" + (i+1),
                    "Description newSubtask" + (i+1), TaskStatus.IN_PROGRESS, Duration.ofMinutes(120),
                    LocalDateTime.of(2025, Month.AUGUST, i+1, 7, 18)));
        }
        assertEquals(3, taskManager.getSubtaskList().size());
    }

    @Test
    void updateTask() {
        taskManager.createTask(task);
        changedTask.setId(1);
        taskManager.updateTask(changedTask);
        assertEquals("ChangedTask1", taskManager.getTask(changedTask.getId()).getName());
        assertEquals("Description changedTask1", taskManager.getTask(changedTask.getId()).getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTask(changedTask.getId()).getStatus());
    }

    @Test
    void updateEpic() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertEquals("Subtask1", taskManager.getSubtasksForEpic(1).getFirst().getName());
        changedEpic.setId(1);
        taskManager.updateEpic(changedEpic);
        assertEquals("ChangedEpic1", taskManager.getEpic(1).getName());
        assertEquals("Description changedEpic1", taskManager.getEpic(1).getDescription());
        assertEquals("Subtask1", taskManager.getSubtasksForEpic(1).getFirst().getName());
    }

    @Test
    void updateSubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertEquals("Subtask1", taskManager.getSubtask(2).getName());
        changedSubtask.setId(2);
        taskManager.updateSubtask(changedSubtask);
        assertEquals("ChangedSubtask1", taskManager.getSubtask(2).getName());
    }

    @Test
    void updateSubtaskWillChangeEpicStatus() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(1).getStatus());
        Subtask newSubtask = new Subtask(1, "NewSubtask1", "Description newSubtask1",
                TaskStatus.DONE, Duration.ofMinutes(120),
                LocalDateTime.of(2025, Month.AUGUST, 13, 7, 18));
        newSubtask.setId(2);
        taskManager.updateSubtask(newSubtask);
        assertEquals(TaskStatus.DONE, taskManager.getEpic(1).getStatus());
    }

    @Test
    void updateSubtaskWillChangeEpicTime() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 9, 7, 30), epic.getStartTime());
        assertEquals(LocalDateTime.of(2025, Month.JUNE, 9, 8, 15), epic.getEndTime());
        assertEquals(Duration.ofMinutes(45), epic.getDuration());
        Subtask newSubtask = new Subtask(1, "NewSubtask1", "Description newSubtask1",
                TaskStatus.DONE, Duration.ofMinutes(120),
                LocalDateTime.of(2025, Month.AUGUST, 13, 7, 18));
        newSubtask.setId(2);
        taskManager.updateSubtask(newSubtask);
        assertEquals(LocalDateTime.of(2025, Month.AUGUST, 13, 7, 18), epic.getStartTime());
        assertEquals(LocalDateTime.of(2025, Month.AUGUST, 13, 9, 18), epic.getEndTime());
        assertEquals(Duration.ofMinutes(120), epic.getDuration());
    }

    @Test
    void updateTaskIsImpossibleIfKeyIsIncorrect() {
        taskManager.createTask(task);
        changedTask.setId(5);
        taskManager.updateTask(changedTask);
        assertNull(taskManager.getTask(changedTask.getId()));
    }

    @Test
    void updateEpicIsImpossibleIfKeyIsIncorrect() {
        taskManager.createEpic(epic);
        taskManager.updateEpic(new Epic(5, "NewEpic1", "Description newEpic1", TaskStatus.DONE,
                new ArrayList<Integer>(),
                LocalDateTime.of(2025, Month.AUGUST, 20, 12, 0),
                LocalDateTime.of(2025, Month.AUGUST, 21, 12, 0),
                Duration.ofMinutes(1440)
        ));
        assertEquals("Epic1", taskManager.getEpic(1).getName());
        assertEquals("Description epic1", taskManager.getEpic(1).getDescription());
    }

    @Test
    void updateSubtaskIsImpossibleIfIdIsIncorrect() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        changedTask.setId(5);
        taskManager.updateSubtask(changedSubtask);
        assertNotEquals("ChangedSubtask1", taskManager.getSubtasksForEpic(1).getFirst().getName());
    }

    @Test
    void updateSubtaskIsImpossibleIfEpicIdIsIncorrect() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.updateSubtask(new Subtask(1, "NewSubtask1", "Description newSubtask1",
                TaskStatus.DONE, Duration.ofMinutes(120),
                LocalDateTime.of(2025, Month.AUGUST, 13, 7, 18)));
        assertEquals("Subtask1", taskManager.getSubtasksForEpic(1).getFirst().getName());
        assertEquals(1, taskManager.getSubtaskList().size());
    }

    @Test
    void returnCorrectHistoryList() {
        taskManager.createTask(new Task(1, "NewTask1", "Description newTask1",
                TaskStatus.DONE, Duration.ofMinutes(120),
                LocalDateTime.of(2025, Month.AUGUST, 13, 7, 18)));
        taskManager.getTask(1);
        taskManager.updateTask(new Task(1, "NewTask2", "Description newTask2", TaskStatus.DONE,
                Duration.ofMinutes(120), LocalDateTime.of(2025, Month.AUGUST, 13, 7, 18)));
        Task historyTask = taskManager.getHistory().getFirst();
        assertEquals("NewTask1", historyTask.getName());
        assertEquals("Description newTask1", historyTask.getDescription());
        assertEquals(TaskStatus.DONE, historyTask.getStatus());
    }
}