package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
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