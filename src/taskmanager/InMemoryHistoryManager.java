package taskmanager;

import tasks.Task;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    ArrayList<Task> viewedTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (viewedTasks.size() >= 10) {
            viewedTasks.removeFirst();
        }
        viewedTasks.addLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return viewedTasks;
    }
}