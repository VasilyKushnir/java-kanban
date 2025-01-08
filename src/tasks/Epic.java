package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(Integer id, String name, String description, TaskStatus taskStatus, ArrayList<Integer> subtaskIds) {
        super(id, name, description, taskStatus);
        this.subtaskIds = subtaskIds;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addInSubtaskIds(int id) {
        subtaskIds.add(id);
    }

    public void removeFromSubtaskIds(Integer id) {
        subtaskIds.remove(id);
    }

    public void eraseSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return "tasks.Task{ " +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskStatus='" + getStatus() + '\'' +
                ", subtaskIds='" + getSubtaskIds() +
                " }";
    }
}