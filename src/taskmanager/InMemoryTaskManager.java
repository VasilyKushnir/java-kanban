package taskmanager;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
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
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(++id);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.addInSubtaskIds(subtask.getId());
            updateEpicStatus(epic);
        }
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
            ArrayList<Integer> subtasksIds = epics.get(epic.getId()).getSubtaskIds();
            for (Integer subtaskId : subtasksIds) {
                epic.addInSubtaskIds(subtaskId);
            }
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
        if (task == null) return null;
        Task taskForHistory = new Task(task.getId(), task.getName(), task.getDescription(), task.getStatus());
        historyManager.add(taskForHistory);
        return task;
    }

    @Override
    public Epic getEpic(int key) {
        Epic epic = epics.get(key);
        if (epic == null) return null;
        Epic epicForHistory = new Epic(epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus(),
                epic.getSubtaskIds());
        historyManager.add(epicForHistory);
        return epic;
    }

    @Override
    public Subtask getSubtask(int key) {
        Subtask subtask = subtasks.get(key);
        if (subtask == null) return null;
        Subtask subtaskForHistory = new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(),
                subtask.getStatus(), subtask.getEpicId());
        historyManager.add(subtaskForHistory);
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

    protected ArrayList<Task> getMergedList() {
        List<Task> merged = new ArrayList<>(getTaskList());
        merged.addAll(getEpicList());
        merged.addAll(getSubtaskList());
        return new ArrayList<>(merged);
    }

    @Override
    public ArrayList<Subtask> getSubtasksForEpic(int key) {
        ArrayList<Subtask> subtasksForEpic = new ArrayList<>();
        Epic epic = epics.get(key);
        if (epic == null) return null;
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        for (int subtaskId : subtaskIds) {
            subtasksForEpic.add(subtasks.get(subtaskId));
        }
        return subtasksForEpic;
    }

    @Override
    public void removeTask(int key) {
        if (tasks.containsKey(key)) {
            tasks.remove(key);
            historyManager.remove(key);
        }
    }

    @Override
    public void removeEpic(int key) {
        if (epics.containsKey(key)) {
            Epic epic = epics.get(key);
            if (epic == null) return;
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            for (int subtaskId : subtaskIds) {
                subtasks.remove(subtaskId);
            }
            epics.remove(key);
            historyManager.remove(key);
        }
    }

    @Override
    public void removeSubtask(int key) {
        if (subtasks.containsKey(key)) {
            Subtask subtask = getSubtask(key);
            Epic epic = getEpic(subtask.getEpicId());
            epic.removeFromSubtaskIds(subtask.getId());
            updateEpicStatus(epic);
            subtasks.remove(key);
            historyManager.remove(key);
        }
    }

    @Override
    public void clearTasks() {
        for (Integer key : tasks.keySet()) {
            historyManager.remove(key);
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Map.Entry<Integer, Epic> epicEntry : epics.entrySet()) {
            for (Integer subtaskId : epicEntry.getValue().getSubtaskIds()) {
                historyManager.remove(subtaskId);
            }
            historyManager.remove(epicEntry.getKey());
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Integer key : subtasks.keySet()) {
            historyManager.remove(key);
        }
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
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}