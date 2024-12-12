package tasks;

public class Subtask extends Task {

    private final Integer epicId;

    public Subtask(int epicId ,String name, String description, TaskStatus taskStatus) {
        super(name, description, taskStatus);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "tasks.Task{ " +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskStatus='" + getStatus() + '\'' +
                ", epicId='" + getEpicId() +
                " }";
    }
}