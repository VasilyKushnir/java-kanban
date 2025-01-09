package taskmanager;

import tasks.Task;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> viewedTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (viewedTasks.size() >= 10) {
            viewedTasks.removeFirst();
        }
        viewedTasks.addLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(viewedTasks);
    }
}