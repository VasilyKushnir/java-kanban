package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    @Test
    void epicsAreEqualIfSameId() {
        Epic firstEpic = new Epic(
                "First epic name",
                "First epic description"
        );
        firstEpic.setId(1);

        Task secondEpic = new Epic(
                "Second epic name",
                "Second epic description"
        );
        secondEpic.setId(1);
        assertEquals(firstEpic, secondEpic);
    }
}