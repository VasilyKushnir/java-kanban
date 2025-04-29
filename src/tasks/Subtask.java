package tasks;

public class Subtask extends Task {

    private final Integer epicId;

    public Subtask(int epicId, String name, String description, TaskStatus taskStatus) {
        super(name, description, taskStatus);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus taskStatus, Integer epicId) {
        super(id, name, description, taskStatus);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return getId().toString() + ',' +
                TaskType.SUBTASK + ',' +
                getName() + ',' +
                getStatus() + ',' +
                getDescription() + ',' +
                getEpicId();
    }
}