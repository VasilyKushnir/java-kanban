package taskmanager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerPrioritizedTest {
    private TaskManager manager;
    private HttpTaskServer server;

    @BeforeEach
    void beforeEach() throws IOException {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    void afterEach() {
        server.stop();
    }

    @Test
    void getPrioritized_Returns200_Always() throws IOException, InterruptedException {
        Task firstTask = new Task("FirstTaskName", "FirstTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2020, 10, 7, 12, 0));
        manager.createTask(firstTask);
        Task secondTask = new Task("SecondTaskName", "SecondTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2020, 10, 7, 9, 0));
        manager.createTask(secondTask);
        Task thirdTask = new Task("ThirdTaskName", "ThirdTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2020, 10, 5, 21, 0));
        manager.createTask(thirdTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Set<Task> setFromManager = manager.getPrioritizedTasks();
        List<Task> tasks = new ArrayList<>(setFromManager);
        assertEquals("ThirdTaskName", tasks.get(0).getName());
        assertEquals("SecondTaskName", tasks.get(1).getName());
        assertEquals("FirstTaskName", tasks.get(2).getName());
    }
}