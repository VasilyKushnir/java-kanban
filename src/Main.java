import taskmanager.Managers;
import taskmanager.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();
        System.out.println(tm.getPrioritizedTasks());
    }
}