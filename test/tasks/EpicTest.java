package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    @Test
    void epicsAreEqualIfSameId() {
        Epic firstEpic = new Epic("NewEpic1", "Description newEpic1");
        firstEpic.setId(1);
        Epic secondEpic = new Epic("NewEpic2", "Description newEpic2");
        secondEpic.setId(1);
        assertEquals(firstEpic, secondEpic);
    }
}