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

public class HttpTaskManagerEpicsTest {
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
    void getEpics_Returns200_Always() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void getEpic_Returns200_WhenEpicFound() throws IOException, InterruptedException {
        int id = epic.getId();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFromServer = gson.fromJson(response.body().toString(), Epic.class);
        assertEquals(200, response.statusCode());
        assertEquals("EpicName", epicFromServer.getName(), "Incorrect epic name");
    }

    @Test
    void getEpic_Returns404_WhenEpicNotFound() throws IOException, InterruptedException {
        int nonExistingEpicId = 10;
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + nonExistingEpicId);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void getEpicSubtasks_Returns200_WhenEpicFound() throws IOException, InterruptedException {
        Subtask firstSubtask = new Subtask(epic.getId(), "FirstSubtaskName", "FirstSubtaskDescription",
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 12, 0));
        manager.createSubtask(firstSubtask);
        Subtask secondSubtask = new Subtask(epic.getId(), "SecondSubtaskName",
                "SecondSubtaskDescription", TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 20, 0));
        manager.createSubtask(secondSubtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasksFromManager = manager.getSubtasksForEpic(epic.getId());
        assertNotNull(subtasksFromManager, "There are no subtasks for epic");
        assertEquals(2, subtasksFromManager.size(), "Incorrect amount of subtasks");
        assertEquals("FirstSubtaskName", subtasksFromManager.getFirst().getName(),
                "Incorrect subtask name");
        assertEquals("SecondSubtaskDescription", subtasksFromManager.getLast().getDescription(),
                "Incorrect subtask description");
    }

    @Test
    void getEpicSubtasks_Returns404_WhenEpicNotFound() throws IOException, InterruptedException {
        int nonExistingEpicId = 10;
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + nonExistingEpicId + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void postEpic_Returns201_Always() throws IOException, InterruptedException {
        Epic anotherEpic = new Epic("AnotherEpicName", "AnotherEpicDescription");
        String anotherEpicJson = gson.toJson(anotherEpic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(anotherEpicJson))
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Epic> epicsFromManager = manager.getEpicList();
        assertNotNull(epicsFromManager, "There are no epics");
        assertEquals("AnotherEpicDescription", epicsFromManager.getLast().getDescription(),
                "Incorrect epic description");
        assertEquals(2, epicsFromManager.getLast().getId(), "Incorrect epic ID");
    }

    @Test
    void deleteEpic_Returns200_Always() throws IOException, InterruptedException {
        Epic anotherEpic = new Epic("AnotherEpicName", "AnotherEpicDescription");
        manager.createEpic(anotherEpic);
        assertEquals(2, manager.getEpicList().size());
        assertEquals("EpicName", manager.getEpicList().getFirst().getName());
        assertEquals("AnotherEpicDescription", manager.getEpicList().getLast().getDescription());
        int firstEpicId = epic.getId();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + firstEpicId);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Epic> epicsFromManger = manager.getEpicList();
        assertEquals(1, epicsFromManger.size(), "Incorrect amount of epics");
        assertEquals("AnotherEpicName", epicsFromManger.getFirst().getName(), "Incorrect epic name");
    }
}