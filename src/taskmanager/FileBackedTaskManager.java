package taskmanager;

import taskmanager.exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final Path path;

    public FileBackedTaskManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    private void load() {
        if (Files.exists(path)) {
            try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(path)))) {
                while (br.ready()) {
                    String stringTask = br.readLine();
                    Task task = fromString(stringTask);
                    if (task.getId() > super.id) {
                        super.id = task.getId();
                    }
                    if (task.getType().equals(TaskType.EPIC)) {
                        Epic epic = (Epic) task;
                        super.epics.put(epic.getId(), epic);
                    } else if (task.getType().equals(TaskType.SUBTASK)) {
                        Subtask subtask = (Subtask) task;
                        super.subtasks.put(subtask.getId(), subtask);
                        int epicId = subtask.getEpicId();
                        Epic epic = super.epics.get(epicId);
                        epic.addInSubtaskIds(subtask.getId());
                    } else {
                        super.tasks.put(task.getId(), task);
                    }
                }
            } catch (IOException e) {
                throw new ManagerSaveException();
            }
        }
    }

    private void save() {
        ArrayList<Task> merged = new ArrayList<>(super.getMergedList()); // все задачи
        try (Writer fw = new FileWriter(path.toString())) {
            for (Task t : merged) {
                fw.write(t.serialize() + '\n');
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private Task fromString(String string) {
        String[] stringTaskArray = string.split(",");
        TaskType taskType = TaskType.valueOf(stringTaskArray[1]); // 1 = TaskType
        int taskId = Integer.parseInt(stringTaskArray[0]); // 0 = id
        String taskName = stringTaskArray[2]; // 2 = String
        String taskDescription = stringTaskArray[4]; // 4 = String
        TaskStatus taskStatus = TaskStatus.valueOf(stringTaskArray[3]); // 3 = TaskStatus
        LocalDateTime startTime = LocalDateTime.parse(stringTaskArray[6]);
        LocalDateTime endTime = LocalDateTime.parse(stringTaskArray[7]);
        Duration duration = Duration.ofMinutes(Integer.parseInt(stringTaskArray[8]));
        switch (taskType) {
            case TaskType.EPIC -> {
                return new Epic(taskId, taskName, taskDescription, taskStatus, new ArrayList<>(), startTime, endTime,
                        duration);
            }
            case TaskType.SUBTASK -> {
                int taskEpicId = Integer.parseInt(stringTaskArray[5]);
                return new Subtask(taskId, taskName, taskDescription, taskStatus, taskEpicId, duration, startTime);
            }
            default -> {
                return new Task(taskId, taskName, taskDescription, taskStatus, duration, startTime);
            }
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(int key) {
        super.removeTask(key);
        save();
    }

    @Override
    public void removeEpic(int key) {
        super.removeEpic(key);
        save();
    }

    @Override
    public void removeSubtask(int key) {
        super.removeSubtask(key);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearEpics();
        save();
    }

    public static FileBackedTaskManager loadFromFile(HistoryManager hm, Path path) {
        FileBackedTaskManager tm = new FileBackedTaskManager(hm, path);
        tm.load();
        return tm;
    }
}