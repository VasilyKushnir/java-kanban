package taskmanager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    private static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}