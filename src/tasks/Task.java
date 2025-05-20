package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private Integer id;
    private final String name;
    private final String description;
    private TaskStatus status;
    private final TaskType type = TaskType.TASK;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(Integer id, String name, String description, TaskStatus status, Duration duration,
                LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "Id='" + getId() + "', " +
                "TaskType='" + TaskType.TASK + "', " +
                "Name='" + getName() + "', " +
                "Status='" + getStatus() + "', " +
                "Description='" + getDescription() + "', " +
                "StartTime='" + getStartTime() + "', " +
                "EndTime='" + getEndTime() + "', " +
                "Duration='" + getDuration().toMinutes() +
                "'}";
    }

    public String toFileString() {
        return getId().toString() + ',' +
                TaskType.TASK + ',' +
                getName() + ',' +
                getStatus() + ',' +
                getDescription() + ',' +
                "-1," +
                getStartTime() + ',' +
                getEndTime() + ',' +
                getDuration().toMinutes();
    }
}