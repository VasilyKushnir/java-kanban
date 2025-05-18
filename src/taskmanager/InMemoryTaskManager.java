package taskmanager;

import taskmanager.exceptions.ManagerCreateException;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;
    Set<Task> prioritizedTasksSet = new TreeSet<>((Task task1, Task task2) -> {
        return task1.getStartTime().compareTo(task2.getStartTime());
    });

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private boolean areTasksIntersected(Task taskForCheck) {
        return prioritizedTasksSet.stream()
                .anyMatch(existingTask -> areIntersect(existingTask, taskForCheck));
    }

    @Override
    public void createTask(Task task) {
        if (areTasksIntersected(task)) {
            throw new ManagerCreateException();
        }
        task.setId(++id);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasksSet.add(task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(++id);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (areTasksIntersected(subtask)) {
            throw new ManagerCreateException();
        }
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(++id);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.addInSubtaskIds(subtask.getId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
            if (subtask.getStartTime() != null) {
                prioritizedTasksSet.add(subtask);
            }
        }
    }

    @Override
    public void updateTask(Task task) {
        if (areTasksIntersected(task)) {
            return;
        }
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                prioritizedTasksSet.add(task);
            }
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
        if (areTasksIntersected(subtask)) {
            return;
        }
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
            if (subtask.getStartTime() != null) {
                prioritizedTasksSet.add(subtask);
            }
        }
    }

    @Override
    public Task getTask(int key) {
        Task task = tasks.get(key);
        if (task == null) return null;
        Task taskForHistory = new Task(task.getId(), task.getName(), task.getDescription(), task.getStatus(),
                task.getDuration(), task.getStartTime());
        historyManager.add(taskForHistory);
        return task;
    }

    @Override
    public Epic getEpic(int key) {
        Epic epic = epics.get(key);
        if (epic == null) return null;
        Epic epicForHistory = new Epic(epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus(),
                epic.getSubtaskIds(), epic.getStartTime(), epic.getEndTime(), epic.getDuration());
        historyManager.add(epicForHistory);
        return epic;
    }

    @Override
    public Subtask getSubtask(int key) {
        Subtask subtask = subtasks.get(key);
        if (subtask == null) return null;
        Subtask subtaskForHistory = new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(),
                subtask.getStatus(), subtask.getEpicId(), subtask.getDuration(), subtask.getStartTime());
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
    public ArrayList<Subtask> getSubtasksForEpic(int epicId) {
        return new ArrayList<>(subtasks.values().stream()
                 .filter(subtask -> subtask.getEpicId().equals(epicId))
                 .collect(Collectors.toList()));
    }

    @Override
    public void removeTask(int key) {
        if (tasks.containsKey(key)) {
            prioritizedTasksSet.remove(tasks.get(key));
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
            prioritizedTasksSet.remove(subtasks.get(key));
            Subtask subtask = getSubtask(key);
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeFromSubtaskIds(subtask.getId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
            subtasks.remove(key);
            historyManager.remove(key);
        }
    }

    @Override
    public void clearTasks() {
        prioritizedTasksSet.removeAll(tasks.values());
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
        prioritizedTasksSet.removeAll(subtasks.values());
        for (Integer key : subtasks.keySet()) {
            historyManager.remove(key);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.eraseSubtaskIds();
            updateEpicStatus(epic);
            updateEpicTime(epic);
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

    private void updateEpicTime(Epic epic) {
        Duration epicDuration = Duration.ZERO;
        LocalDateTime epicStartTime = null;
        LocalDateTime epicEndTime = null;
        Collection<Subtask> subtasksForEpic = getSubtasksForEpic(epic.getId());
        for (Subtask st : subtasksForEpic) {
            if (epicStartTime == null || st.getStartTime().isBefore(epicStartTime)) {
                epicStartTime = st.getStartTime();
            }
            if (epicEndTime == null || st.getEndTime().isAfter(epicEndTime)) {
                epicEndTime = st.getEndTime();
            }
            epicDuration = epicDuration.plus(st.getDuration());
        }
        epic.setStartTime(epicStartTime);
        epic.setEndTime(epicEndTime);
        epic.setDuration(epicDuration);
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasksSet;
    }

    private boolean areIntersect(Task t1, Task t2) {
        if (t1.equals(t2)) {
            return false;
        }
        LocalDateTime t1StartTime = t1.getStartTime();
        LocalDateTime t1EndTime = t1.getEndTime();
        LocalDateTime t2StartTime = t2.getStartTime();
        LocalDateTime t2EndTime = t2.getEndTime();
        return (t2StartTime.isAfter(t1StartTime) && t2StartTime.isBefore(t1EndTime)) ||
                (t2EndTime.isAfter(t1StartTime) && t2EndTime.isBefore(t1EndTime));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}