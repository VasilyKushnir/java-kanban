package taskmanager;

import java.nio.file.Paths;

public class Managers {
    public static TaskManager getDefault() {
        return FileBackedTaskManager.loadFromFile(getDefaultHistory(), Paths.get("file.txt"));
    }

    private static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}