package taskmanager;

import tasks.Task;
import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);

    ArrayList<Task> getHistory();
}