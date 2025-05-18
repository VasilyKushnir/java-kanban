import taskmanager.Managers;
import taskmanager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();
        System.out.println(tm.getPrioritizedTasks());
    }
}