package taskmanager;

import tasks.Task;
import java.util.ArrayList;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> viewedTasks = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        } else if (viewedTasks.contains(task)) {
            viewedTasks.remove(task);
            viewedTasks.addLast(task);
        }
    }

    @Override
    public void remove(int id) {
        viewedTasks.remove(id);
        for (Task task : viewedTasks) {
            if (task.getId() == id) {
                viewedTasks.remove(task);
            }
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(viewedTasks);
    }
}