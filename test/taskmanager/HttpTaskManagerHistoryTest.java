package taskmanager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerHistoryTest {
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
    void getHistory_Returns200_Always() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse firstResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> firstHistoryFromManager = manager.getHistory();
        assertEquals(200, firstResponse.statusCode());
        assertEquals(0, firstHistoryFromManager.size());
        Task firstTask = new Task("FirstTaskName", "FirstTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2020, 10, 5, 12, 0));
        manager.createTask(firstTask);
        Task secondTask = new Task("SecondTaskName", "SecondTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2020, 10, 5, 20, 0));
        manager.createTask(secondTask);
        Epic epic = new Epic("EpicName", "EpicDescription");
        manager.createEpic(epic);
        manager.getEpic(epic.getId());
        manager.getTask(secondTask.getId());
        manager.getTask(firstTask.getId());
        HttpResponse secondResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> secondHistoryFromManager = manager.getHistory();
        assertEquals(200, secondResponse.statusCode());
        assertEquals(3, secondHistoryFromManager.size());
        assertEquals(3, secondHistoryFromManager.getFirst().getId());
        assertEquals(2, secondHistoryFromManager.get(1).getId());
        assertEquals(1, secondHistoryFromManager.getLast().getId());
    }
}