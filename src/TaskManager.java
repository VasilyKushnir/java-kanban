
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class TaskManager {

    private int id = 1;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void createTask(Task task) {

        task.setId(id);
        tasks.put(task.getId(), task);
        id++;

    }

    public void createEpic(Epic epic) {

        epic.setId(id);
        epics.put(epic.getId(), epic);
        id++;

    }
    
    public void createSubtask(Subtask subtask) {

        subtask.setId(id);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = getEpic(subtask.getEpicId());
        epic.addInSubtaskIds(subtask.getId());

        updateEpicStatus(epic);
        updateEpicStatus(epic);

        id++;

    }

    public void updateTask(int key, Task task) {

        task.setId(key);
        tasks.put(task.getId(), task);

    }

    public void updateEpic(int key, Epic epic) {

        ArrayList<Integer> subtasksIds = epics.get(key).getSubtaskIds();

        epic.setId(key);
        for (int subtaskId : subtasksIds) {
            epic.addInSubtaskIds(subtaskId);
        }
        epics.put(epic.getId(), epic);

        updateEpicStatus(epic);

    }


    public void updateSubtask(int key, Subtask subtask) {

        subtask.setId(key);
        subtasks.put(subtask.getId(),subtask);

        Epic epic = getEpic(subtask.getEpicId());
        updateEpicStatus(epic);
        updateEpicStatus(epic);

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

        ArrayList<Task> taskList = new ArrayList<>();
        for (Task task : tasks.values()) {
            taskList.add(task);
        }
        return taskList;

    }

    public ArrayList<Epic> getEpicList() {

        ArrayList<Epic> epicList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epicList.add(epic);
        }
        return epicList;

    }

    public ArrayList<Subtask> getSubtaskList() {

        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            subtaskList.add(subtask);
        }
        return subtaskList;

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
    }

    public void clearSubtasks() {
        subtasks.clear();
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
