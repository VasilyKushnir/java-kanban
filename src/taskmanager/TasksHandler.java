package taskmanager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.exceptions.ManagerCreateException;
import taskmanager.exceptions.ManagerUpdateException;
import tasks.Task;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            HttpMethod method = HttpMethod.valueOf(getMethod(exchange));
            switch (method) {
                case GET:
                    get(exchange);
                    break;
                case POST:
                    post(exchange);
                    break;
                case DELETE:
                    delete(exchange);
                    break;
                default:
                    sendBadRequest(exchange);
            }
        } catch (Exception e) {
            sendInternalServerError(exchange, e);
        }
    }

    public void get(HttpExchange h) throws IOException {
        Gson gson = getGson();
        if (!uriContainsId(h)) {
            ArrayList<Task> taskList = taskManager.getTaskList();
            String tasksArrayJson = gson.toJson(taskList);
            sendText(h, tasksArrayJson);
        } else if (hasId(h)) {
            Task task = taskManager.getTask(getId(h));
            if (task != null) {
                sendText(h, gson.toJson(task));
            } else {
                sendNotFound(h);
            }
        } else {
            sendBadRequest(h);
        }
    }

    public void post(HttpExchange h) throws IOException {
        Gson gson = getGson();
        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            Task task = gson.fromJson(body, Task.class);
            if (task.getId() == null) {
                try {
                    taskManager.createTask(task);
                    sendCreated(h);
                } catch (ManagerCreateException e) {
                    sendNotAcceptable(h);
                }
            } else {
                try {
                    taskManager.updateTask(task);
                    sendCreated(h);
                } catch (ManagerUpdateException e) {
                    sendNotAcceptable(h);
                }
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(h);
        }
    }

    public void delete(HttpExchange h) throws IOException {
        if (hasId(h)) {
            int taskId = getId(h);
            taskManager.removeTask(taskId);
            sendText(h, "Task was deleted");
        } else {
            sendBadRequest(h);
        }
    }
}