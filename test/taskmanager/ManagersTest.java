package taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {
    @Test
    void managersReturnCorrectTaskManager() {
        TaskManager tm = Managers.getDefault();
        Assertions.assertInstanceOf(TaskManager.class, tm);
    }
}