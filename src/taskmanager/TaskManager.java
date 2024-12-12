package taskmanager;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int id = 0;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void createTask(Task task) {
        task.setId(++id);
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(++id);
        epics.put(epic.getId(), epic);
    }
    
    public void createSubtask(Subtask subtask) {
        subtask.setId(++id);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = getEpic(subtask.getEpicId());
        epic.addInSubtaskIds(subtask.getId());
        updateEpicStatus(epic);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }


    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(getEpic(subtask.getEpicId()));
        }
    }


    public Task getTask(int key) {
        return tasks.get(key);
    }

    public Epic getEpic(int key) {
        return epics.get(key);
    }

    public Subtask getSubtask(int key) {
        return subtasks.get(key);
    }

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getSubtasksForEpic(int key) {
        ArrayList<Subtask> subtasksForEpic = new ArrayList<>();
        Epic epic = epics.get(key);
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        for (int subtaskId : subtaskIds) {
            subtasksForEpic.add(subtasks.get(subtaskId));
        }
        return subtasksForEpic;
    }

    public void removeTask(int key) {
        tasks.remove(key);
    }

    public void removeEpic(int key) {
        Epic epic = epics.get(key);
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        for (int subtaskId : subtaskIds) {
            removeSubtask(subtaskId);
        }
        epics.remove(key);
    }

    public void removeSubtask(int key) { // изменение статуса эпика
        Subtask subtask = getSubtask(key);
        Epic epic = getEpic(subtask.getEpicId());
        epic.removeFromSubtaskIds(subtask.getId());
        updateEpicStatus(epic);
        subtasks.remove(key);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

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
}