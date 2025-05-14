package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final Integer epicId;
    private final TaskType type = TaskType.SUBTASK;

    public Subtask(int epicId, String name, String description, TaskStatus taskStatus, Duration duration,
                   LocalDateTime startTime) {
        super(name, description, taskStatus, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus taskStatus, Integer epicId, Duration duration,
                   LocalDateTime startTime) {
        super(id, name, description, taskStatus, duration, startTime);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "Id='" + getId().toString() + "', " +
                "TaskType='" + TaskType.SUBTASK + "', " +
                "Name='" + getName() + "', " +
                "Status='" + getStatus() + "', " +
                "Description='" + getDescription() + "', " +
                "EpicId='" + getEpicId() + "', " +
                "StartTime='" + getStartTime() + "', " +
                "EndTime='" + getEndTime() + "', " +
                "Duration='" + getDuration().toMinutes() +
                "'}";
    }

    public String serialize() {
        return getId().toString() + ',' +
                TaskType.SUBTASK + ',' +
                getName() + ',' +
                getStatus() + ',' +
                getDescription() + ',' +
                getEpicId() + ',' +
                getStartTime() + ',' +
                getEndTime() + ',' +
                getDuration().toMinutes();
    }
}