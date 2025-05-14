package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;
    private final TaskType type = TaskType.EPIC;
    LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, Duration.ZERO, null);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(Integer id, String name, String description, TaskStatus taskStatus, ArrayList<Integer> subtaskIds,
                LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        super(id, name, description, taskStatus, duration, startTime);
        this.subtaskIds = subtaskIds;
        this.endTime = endTime;
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
    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "Id='" + getId().toString() + "', " +
                "TaskType='" + TaskType.EPIC + "', " +
                "Name='" + getName() + "', " +
                "Status='" + getStatus() + "', " +
                "Description='" + getDescription() + "', " +
                "StartTime='" + getStartTime() + "', " +
                "EndTime='" + getEndTime() + "', " +
                "Duration='" + getDuration().toMinutes() +
                "'}";
    }

    public String serialize() {
        return getId().toString() + ',' +
                TaskType.EPIC + ',' +
                getName() + ',' +
                getStatus() + ',' +
                getDescription() + ',' +
                "-1," +
                getStartTime() + ',' +
                getEndTime() + ',' +
                getDuration().toMinutes();
    }
}