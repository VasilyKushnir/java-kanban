package taskmanager;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerSubtasksTest {
    private TaskManager manager;
    private Epic epic;
    private HttpTaskServer server;
    private Gson gson;

    @BeforeEach
    void beforeEach() throws IOException {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        epic = new Epic("EpicName", "EpicDescription");
        manager.createEpic(epic);
        server = new HttpTaskServer(manager);
        server.start();
        gson = new EpicsHandler(manager).getGson();
    }

    @AfterEach
    void afterEach() {
        server.stop();
    }

    @Test
    void getSubtasks_Returns200_Always() throws IOException, InterruptedException {
        Subtask firstSubtask = new Subtask(epic.getId(), "FirstSubtaskName", "FirstSubtaskDescription",
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 12, 0));
        manager.createSubtask(firstSubtask);
        Subtask secondSubtask = new Subtask(epic.getId(), "SecondSubtaskName",
                "SecondSubtaskDescription", TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 20, 0));
        manager.createSubtask(secondSubtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Subtask> subtasksFromManager = manager.getSubtaskList();
        assertNotNull(subtasksFromManager, "There are no subtasks");
        assertEquals(2, subtasksFromManager.size(), "Incorrect amount of subtasks");
        assertEquals("FirstSubtaskName", subtasksFromManager.getFirst().getName(),
                "Incorrect subtask name");
        assertEquals("SecondSubtaskDescription", subtasksFromManager.getLast().getDescription(),
                "Incorrect subtask description");
    }

    @Test
    void getSubtask_Returns200_WhenSubtaskFound() throws IOException, InterruptedException {
        Subtask firstSubtask = new Subtask(epic.getId(), "FirstSubtaskName", "FirstSubtaskDescription",
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 12, 0));
        manager.createSubtask(firstSubtask);
        Subtask secondSubtask = new Subtask(epic.getId(), "SecondSubtaskName",
                "SecondSubtaskDescription", TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 20, 0));
        manager.createSubtask(secondSubtask);
        HttpClient client = HttpClient.newHttpClient();
        int id = secondSubtask.getId();
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Subtask subtaskFromServer = gson.fromJson(response.body().toString(), Subtask.class);
        assertEquals("SecondSubtaskName", subtaskFromServer.getName(), "Incorrect subtask name");
    }

    @Test
    void getSubtask_Returns404_WhenSubtaskNotFound() throws IOException, InterruptedException {
        Subtask firstSubtask = new Subtask(epic.getId(), "FirstSubtaskName", "FirstSubtaskDescription",
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 12, 0));
        manager.createSubtask(firstSubtask);
        int nonExistingSubtaskId = 10;
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + nonExistingSubtaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void postSubtask_Returns201_WhenSubtaskCreated() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(epic.getId(), "FirstSubtaskName", "FirstSubtaskDescription",
                TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Subtask> subtasksFromManager = manager.getSubtaskList();
        assertNotNull(subtasksFromManager, "There are no subtasks");
        assertEquals(1, subtasksFromManager.size(), "Incorrect amount of subtasks");
        assertEquals("FirstSubtaskName", subtasksFromManager.getFirst().getName(),
                "Incorrect subtask name");
    }

    @Test
    void postSubtask_Returns406_WhenCreationIsNotAllowed() throws IOException, InterruptedException {
        Subtask initialSubtask = new Subtask(epic.getId(), "FirstSubtaskName",
                "FirstSubtaskDescription", TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 12, 0));
        manager.createSubtask(initialSubtask);
        Subtask clientSubtask = new Subtask(epic.getId(), "FirstSubtaskName",
                "FirstSubtaskDescription", TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 12, 0));
        String subtaskJson = gson.toJson(clientSubtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    void postSubtask_Returns201_WhenSubtaskUpdated() throws IOException, InterruptedException {
        Subtask firstSubtask = new Subtask(epic.getId(), "FirstSubtaskName", "FirstSubtaskDescription",
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 12, 0));
        manager.createSubtask(firstSubtask);
        Subtask secondSubtask = new Subtask(epic.getId(), "SecondSubtaskName",
                "SecondSubtaskDescription", TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 20, 0));
        manager.createSubtask(secondSubtask);
        Subtask clientSubtask = new Subtask(firstSubtask.getId(), "UpdatedFirstSubtaskName",
                "UpdatedFirstSubtaskDescription",TaskStatus.NEW, 1, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 14, 0));
        String subtaskJson = gson.toJson(clientSubtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + firstSubtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        Subtask updatedFirstSubtask = manager.getSubtask(firstSubtask.getId());
        assertEquals("UpdatedFirstSubtaskName", updatedFirstSubtask.getName(),
                "Incorrect subtask name");
    }

    @Test
    void postSubtask_Returns406_WhenUpdateIsNotAllowed() throws IOException, InterruptedException {
        Subtask firstSubtask = new Subtask(epic.getId(), "FirstSubtaskName", "FirstSubtaskDescription",
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 12, 0));
        manager.createSubtask(firstSubtask);
        Subtask secondSubtask = new Subtask(epic.getId(), "SecondSubtaskName",
                "SecondSubtaskDescription", TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 20, 0));
        manager.createSubtask(secondSubtask);
        Subtask clientSubtask = new Subtask(firstSubtask.getId(), "UpdatedFirstSubtaskName",
                "UpdatedFirstSubtaskDescription",TaskStatus.NEW, 1, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 20, 0));
        String subtaskJson = gson.toJson(clientSubtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + firstSubtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        Subtask updatedFirstSubtask = manager.getSubtask(firstSubtask.getId());
        assertEquals("FirstSubtaskName", updatedFirstSubtask.getName(),
                "Incorrect subtask name");
    }

    @Test
    void deleteSubtask_Returns200_Always() throws IOException, InterruptedException {
        Subtask initialSubtask = new Subtask(epic.getId(), "SubtaskName",
                "SubtaskDescription", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        manager.createSubtask(initialSubtask);
        int id = initialSubtask.getId();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getSubtaskList().size(), "Incorrect amount of subtasks");
    }
}