package taskmanager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.ArrayList;

public interface TaskManager {
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    Task getTask(int key);

    Epic getEpic(int key);

    Subtask getSubtask(int key);

    ArrayList<Task> getTaskList();

    ArrayList<Epic> getEpicList();

    ArrayList<Subtask> getSubtaskList();

    ArrayList<Subtask> getSubtasksForEpic(int key);

    void removeTask(int key);

    void removeEpic(int key);

    void removeSubtask(int key);

    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    ArrayList<Task> getHistory();
}