package taskmanager;

import tasks.Task;
import java.util.ArrayList;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> viewedTasks = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) return;
        viewedTasks.addLast(task);
    }

    @Override
    public void remove(int id) {
        viewedTasks.remove(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(viewedTasks);
    }
}