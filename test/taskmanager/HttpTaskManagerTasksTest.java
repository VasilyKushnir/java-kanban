package taskmanager;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {
    private TaskManager manager;
    private HttpTaskServer server;
    private Gson gson;

    @BeforeEach
    void beforeEach() throws IOException {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        server = new HttpTaskServer(manager);
        server.start();
        gson = new TasksHandler(manager).getGson();
    }

    @AfterEach
    void afterEach() {
        server.stop();
    }

    @Test
    void getTasks_Returns200_Always() throws IOException, InterruptedException {
        Task firstTask = new Task("FirstTaskName", "FirstTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2000, 6, 15, 12, 0));
        manager.createTask(firstTask);
        Task secondTask = new Task("SecondTaskName", "SecondTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2000, 6, 16, 12, 0));
        manager.createTask(secondTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = manager.getTaskList();
        assertNotNull(tasksFromManager, "There are no tasks");
        assertEquals(2, tasksFromManager.size(), "Incorrect amount of tasks");
        assertEquals("FirstTaskName", tasksFromManager.getFirst().getName(), "Incorrect task name");
        assertEquals("SecondTaskDescription", tasksFromManager.getLast().getDescription(),
                "Incorrect task description");
    }

    @Test
    void getTask_Returns200_WhenTaskFound() throws IOException, InterruptedException {
        Task firstTask = new Task("FirstTaskName", "FirstTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2000, 6, 15, 12, 0));
        manager.createTask(firstTask);
        Task secondTask = new Task("SecondTaskName", "SecondTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2000, 6, 16, 12, 0));
        manager.createTask(secondTask);
        int id = secondTask.getId();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task taskFromServer = gson.fromJson(response.body().toString(), Task.class);
        assertEquals("SecondTaskName", taskFromServer.getName(), "Incorrect task name");
    }

    @Test
    void getTask_Returns404_WhenTaskNotFound() throws IOException, InterruptedException {
        Task firstTask = new Task("FirstTaskName", "FirstTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2000, 6, 15, 12, 0));
        manager.createTask(firstTask);
        int nonExistingTaskId = 10;
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + nonExistingTaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void postTask_Returns201_WhenTaskCreated() throws IOException, InterruptedException {
        Task task = new Task("TaskName", "TaskDescription", TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = manager.getTaskList();
        assertNotNull(tasksFromManager, "There are no tasks");
        assertEquals(1, tasksFromManager.size(), "Incorrect amount of tasks");
        assertEquals("TaskName", tasksFromManager.getFirst().getName(), "Incorrect task name");
    }

    @Test
    void postTask_Returns406_WhenCreationIsNotAllowed() throws IOException, InterruptedException {
        Task initialTask = new Task("FirstTaskName", "FirstTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2000, 6, 15, 12, 0));
        manager.createTask(initialTask);
        Task clientTask = new Task("FirstTaskName", "FirstTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2000, 6, 15, 12, 0));
        String taskJson = gson.toJson(clientTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    void postTask_Returns201_WhenTaskUpdated() throws IOException, InterruptedException {
        Task firstTask = new Task("FirstTaskName", "FirstTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2000, 6, 15, 12, 0));
        manager.createTask(firstTask);
        Task secondTask = new Task("SecondTaskName", "SecondTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2000, 6, 15, 20, 0));
        manager.createTask(secondTask);
        Task clientTask = new Task(1, "UpdatedFirstTaskName", "UpdatedFirstTaskDescription",
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 14, 0));
        String taskJson = gson.toJson(clientTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + firstTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        Task updatedFirstTask = manager.getTask(firstTask.getId());
        assertEquals("UpdatedFirstTaskName", updatedFirstTask.getName(), "Incorrect task name");
    }

    @Test
    void postTask_Returns406_WhenUpdateIsNotAllowed() throws IOException, InterruptedException {
        Task firstTask = new Task("FirstTaskName", "FirstTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2000, 6, 15, 12, 0));
        manager.createTask(firstTask);
        Task secondTask = new Task("SecondTaskName", "SecondTaskDescription", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2000, 6, 15, 20, 0));
        manager.createTask(secondTask);
        Task clientTask = new Task(1, "UpdatedFirstTaskName", "UpdatedFirstTaskDescription",
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2000, 6, 15, 20, 0));
        String taskJson = gson.toJson(clientTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        Task updatedFirstTask = manager.getTask(firstTask.getId());
        assertEquals("FirstTaskName", updatedFirstTask.getName(), "Incorrect task name");
    }

    @Test
    void deleteTask_Returns200_Always() throws IOException, InterruptedException {
        Task task = new Task("TaskName", "TaskDescription", TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.now());
        manager.createTask(task);
        int id = task.getId();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getTaskList().size(), "Incorrect amount of tasks");
    }
}