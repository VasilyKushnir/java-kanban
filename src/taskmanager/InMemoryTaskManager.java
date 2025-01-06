package taskmanager;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void createTask(Task task) {
        task.setId(++id);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(++id);
        epics.put(epic.getId(), epic);
    }
    
    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(++id);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = getEpic(subtask.getEpicId());
        epic.addInSubtaskIds(subtask.getId());
        updateEpicStatus(epic);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(getEpic(subtask.getEpicId()));
        }
    }

    @Override
    public Task getTask(int key) {
        Task task = tasks.get(key);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int key) {
        Epic epic = epics.get(key);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int key) {
        Subtask subtask = subtasks.get(key);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksForEpic(int key) {
        ArrayList<Subtask> subtasksForEpic = new ArrayList<>();
        Epic epic = epics.get(key);
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        for (int subtaskId : subtaskIds) {
            subtasksForEpic.add(subtasks.get(subtaskId));
        }
        return subtasksForEpic;
    }

    @Override
    public void removeTask(int key) {
        tasks.remove(key);
    }

    @Override
    public void removeEpic(int key) {
        Epic epic = epics.get(key);
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        for (int subtaskId : subtaskIds) {
            removeSubtask(subtaskId);
        }
        epics.remove(key);
    }

    @Override
    public void removeSubtask(int key) {
        Subtask subtask = getSubtask(key);
        Epic epic = getEpic(subtask.getEpicId());
        epic.removeFromSubtaskIds(subtask.getId());
        updateEpicStatus(epic);
        subtasks.remove(key);
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.eraseSubtaskIds();
            updateEpicStatus(epic);
        }
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<TaskStatus> subtasksStatusList = new ArrayList<>();
        TaskStatus epicStatus = null;
        ArrayList<Subtask> subtasksForEpic = getSubtasksForEpic(epic.getId());
        if (subtasksForEpic.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        for (Subtask subtask : subtasksForEpic) {
            subtasksStatusList.add(subtask.getStatus());
        }
        for (TaskStatus subtaskStatus : subtasksStatusList) {
            if (subtaskStatus.equals(TaskStatus.NEW)) {
                if (epicStatus == null) {
                    epicStatus = TaskStatus.NEW;
                } else if (!epicStatus.equals(TaskStatus.NEW)) {
                    epicStatus = TaskStatus.IN_PROGRESS;
                }
            } else if (subtaskStatus.equals(TaskStatus.DONE)) {
                if (epicStatus == null) {
                    epicStatus = TaskStatus.DONE;
                } else if (epicStatus.equals(TaskStatus.NEW)) {
                    epicStatus = TaskStatus.IN_PROGRESS;
                }
            } else {
                epicStatus = TaskStatus.IN_PROGRESS;
            }
        }
        epic.setStatus(epicStatus);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
}