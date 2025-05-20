package taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    HistoryManager historyManager;

    Path pathEmpty;
    Path pathFilled;

    FileBackedTaskManager taskManagerWithEmptyFile;
    FileBackedTaskManager taskManagerWithFilledFile;

    String fileTaskString;
    String fileChangedTaskString;
    String fileEpicString;
    String fileChangedEpicString;
    String fileSubtaskString;
    String fileChangedSubtaskString;

    Task fileTask;
    Task fileChangedTask;
    Epic fileEpic;
    Epic fileChangedEpic;
    Subtask fileSubtask;
    Subtask fileChangedSubtask;

    @Override
    FileBackedTaskManager getTaskManager() {
        try {
            return new FileBackedTaskManager(new InMemoryHistoryManager(),
                    Files.createTempFile("test", ".txt"));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @BeforeEach
    void beforeEach() {
        super.beforeEach();
        historyManager = new InMemoryHistoryManager();

        fileTaskString = "1,TASK,FileTask1,NEW,Description fileTask1,-1,2025-05-25T12:00,2025-05-25T13:30,90";
        fileChangedTaskString = "1,TASK,FileChangedTask1,IN_PROGRESS,Description fileChangedTask1,-1," +
                "2025-05-25T12:00,2025-05-25T13:30,90";
        fileEpicString = "2,EPIC,FileEpic1,IN_PROGRESS,Description fileEpic1,-1,2025-05-25T09:00,2025-05-25T10:00,60";
        fileChangedEpicString = "2,EPIC,FileChangedEpic1,DONE,Description fileChangedEpic1,-1," +
                "2025-05-25T09:00,2025-05-25T10:00,60";
        fileSubtaskString = "3,SUBTASK,FileSubtask1,IN_PROGRESS,Description fileSubtask1,2," +
                "2025-05-25T09:00,2025-05-25T10:00,60";
        fileChangedSubtaskString = "3,SUBTASK,FileChangedSubtask1,DONE,Description fileChangedSubtask1,2," +
                "2025-05-25T09:00,2025-05-25T10:00,60";

        try {
            pathEmpty = Files.createTempFile("fileEmpty", ".txt");
            pathFilled = Files.createTempFile("fileFilled", ".txt");
        } catch (IOException e) {
            throw new RuntimeException();
        }

        try (FileWriter fw = new FileWriter(String.valueOf(pathFilled))) {
            fw.write(fileTaskString + "\n");
            fw.write(fileEpicString + "\n");
            fw.write(fileSubtaskString + "\n");
        } catch (IOException e) {
            throw new RuntimeException();
        }

        taskManagerWithEmptyFile = FileBackedTaskManager.loadFromFile(historyManager, pathEmpty);
        taskManagerWithFilledFile = FileBackedTaskManager.loadFromFile(historyManager, pathFilled);

        fileTask = new Task(1,"FileTask1", "Description fileTask1", TaskStatus.NEW,
                Duration.ofMinutes(90), LocalDateTime.of(2025, Month.MAY, 25, 12, 0));
        fileChangedTask = new Task(1,"FileChangedTask1", "Description fileChangedTask1",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(90),
                LocalDateTime.of(2025, Month.MAY, 25, 12, 0));
        ArrayList<Integer> epicSubtaskIds = new ArrayList<>();
        fileEpic = new Epic(2, "FileEpic1", "Description fileEpic1", TaskStatus.IN_PROGRESS,
                epicSubtaskIds, LocalDateTime.of(2025, Month.MAY, 25, 9, 0),
                LocalDateTime.of(2025, Month.MAY, 25, 10, 0), Duration.ofMinutes(60));
        fileChangedEpic = new Epic(2, "FileChangedEpic1", "Description fileChangedEpic1",
                TaskStatus.DONE, epicSubtaskIds,
                LocalDateTime.of(2025, Month.MAY, 25, 9, 0),
                LocalDateTime.of(2025, Month.MAY, 25, 10, 0), Duration.ofMinutes(60));
        fileSubtask = new Subtask(3, "FileSubtask1", "Description fileSubtask1",
                TaskStatus.IN_PROGRESS, 2, Duration.ofMinutes(60),
                LocalDateTime.of(2025, Month.MAY, 25, 9, 0));
        fileChangedSubtask = new Subtask(3, "FileChangedSubtask1",
                "Description fileChangedSubtask1", TaskStatus.DONE, 2, Duration.ofMinutes(60),
                LocalDateTime.of(2025, Month.MAY, 25, 9, 0));
    }

    @Test
    void addTasksInFile() {
        taskManagerWithEmptyFile.createTask(fileTask);
        taskManagerWithEmptyFile.createEpic(fileEpic);
        taskManagerWithEmptyFile.createSubtask(fileSubtask);
        try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(pathEmpty)))) {
            String taskLine = br.readLine();
            assertEquals(fileTaskString, taskLine);
            String epicLine = br.readLine();
            assertEquals(fileEpicString, epicLine);
            String subtaskLine = br.readLine();
            assertEquals(fileSubtaskString, subtaskLine);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadTasksFromFile() {
        assertEquals(fileTask, taskManagerWithFilledFile.getTask(1));
        assertEquals(fileEpic, taskManagerWithFilledFile.getEpic(2));
        assertEquals(fileSubtask, taskManagerWithFilledFile.getSubtask(3));
    }

    @Test
    void removeTasksFromFile() {
        taskManagerWithFilledFile.clearTasks();
        taskManagerWithFilledFile.clearEpics();
        int data;
        try (FileReader fr = new FileReader(String.valueOf(pathFilled))) {
            data = fr.read();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        assertEquals(-1, data);
    }

    @Test
    void tasksChangingInFile() {
        taskManagerWithFilledFile.updateTask(fileChangedTask);
        taskManagerWithFilledFile.updateEpic(fileChangedEpic);
        taskManagerWithFilledFile.updateSubtask(fileChangedSubtask);
        try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(pathFilled)))) {
            String taskLine = br.readLine();
            assertEquals(fileChangedTaskString, taskLine);
            String epicLine = br.readLine();
            assertEquals(fileChangedEpicString, epicLine);
            String subtaskLine = br.readLine();
            assertEquals(fileChangedSubtaskString, subtaskLine);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void throwExceptionIfPathIsIncorrect() {
        assertThrows(RuntimeException.class, () -> {
            FileBackedTaskManager fileBackedTaskManager
                    = FileBackedTaskManager.loadFromFile(new InMemoryHistoryManager(), null);
        });
    }
}