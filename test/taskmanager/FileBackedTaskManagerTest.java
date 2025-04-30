package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    HistoryManager historyManager;

    Path pathEmpty;
    Path pathFilled;

    FileBackedTaskManager taskManagerWithEmptyFile;
    FileBackedTaskManager taskManagerWithFilledFile;

    String fileTaskString;
    String fileTaskStringChanged;
    String fileEpicString;
    String fileEpicStringChanged;
    String fileSubtaskString;
    String fileSubtaskStringChanged;

    Task task;
    Task taskChanged;
    Epic epic;
    Epic epicChanged;
    Subtask subtask;
    Subtask subtaskChanged;

    @BeforeEach
    void beforeEach() throws IOException {
        historyManager = new InMemoryHistoryManager();

        fileTaskString = "1,TASK,Task name,NEW,Task description,";
        fileTaskStringChanged = "1,TASK,Task name,IN_PROGRESS,Task description,";
        fileEpicString = "2,EPIC,Epic name,IN_PROGRESS,Epic description,";
        fileEpicStringChanged = "2,EPIC,Epic name,DONE,Epic description,";
        fileSubtaskString = "3,SUBTASK,Subtask name,IN_PROGRESS,Subtask description,2";
        fileSubtaskStringChanged = "3,SUBTASK,Subtask name,DONE,Subtask description,2";

        pathEmpty = Files.createTempFile("fileEmpty", ".txt");
        pathFilled = Files.createTempFile("fileFilled", ".txt");
        try (FileWriter fw = new FileWriter(String.valueOf(pathFilled))) {
            fw.write(fileTaskString + "\n");
            fw.write(fileEpicString + "\n");
            fw.write(fileSubtaskString + "\n");
        } catch (IOException e) {
            throw new RuntimeException();
        }

        taskManagerWithEmptyFile = FileBackedTaskManager.loadFromFile(historyManager, pathEmpty);
        taskManagerWithFilledFile = FileBackedTaskManager.loadFromFile(historyManager, pathFilled);

        task = new Task(1, "Task name", "Task description", TaskStatus.NEW);
        taskChanged = new Task(1, "Task name", "Task description", TaskStatus.IN_PROGRESS);
        ArrayList<Integer> epicSubtaskIds = new ArrayList<>();
        epicSubtaskIds.add(3);
        epic = new Epic(2, "Epic name", "Epic description", TaskStatus.IN_PROGRESS, epicSubtaskIds);
        epicChanged = new Epic(2, "Epic name", "Epic description", TaskStatus.DONE, epicSubtaskIds);
        subtask = new Subtask(3, "Subtask name", "Subtask description", TaskStatus.IN_PROGRESS,
                2);
        subtaskChanged = new Subtask(3, "Subtask name", "Subtask description", TaskStatus.DONE,
                2);
    }

    @Test
    void addTasksInFile() {
        taskManagerWithEmptyFile.createTask(task);
        taskManagerWithEmptyFile.createEpic(epic);
        taskManagerWithEmptyFile.createSubtask(subtask);
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
        assertEquals(task, taskManagerWithFilledFile.getTask(1));
        assertEquals(epic, taskManagerWithFilledFile.getEpic(2));
        assertEquals(subtask, taskManagerWithFilledFile.getSubtask(3));
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
        taskManagerWithFilledFile.updateTask(taskChanged);
        taskManagerWithFilledFile.updateSubtask(subtaskChanged);
        try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(pathFilled)))) {
            String taskLine = br.readLine();
            assertEquals(fileTaskStringChanged, taskLine);
            String epicLine = br.readLine();
            assertEquals(fileEpicStringChanged, epicLine);
            String subtaskLine = br.readLine();
            assertEquals(fileSubtaskStringChanged, subtaskLine);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}