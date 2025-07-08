package taskmanager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.exceptions.ManagerCreateException;
import taskmanager.exceptions.ManagerUpdateException;
import tasks.Subtask;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = getMethod(exchange);
            switch (method) {
                case "GET":
                    get(exchange);
                    break;
                case "POST":
                    post(exchange);
                    break;
                case "DELETE":
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
            ArrayList<Subtask> subtaskList = taskManager.getSubtaskList();
            String subtasksArrayJson = gson.toJson(subtaskList);
            sendText(h, subtasksArrayJson);
        } else if (hasId(h)) {
            Subtask subtask = taskManager.getSubtask(getId(h));
            if (subtask != null) {
                sendText(h, gson.toJson(subtask));
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
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (subtask.getId() == null) {
                try {
                    taskManager.createSubtask(subtask);
                    sendCreated(h);
                } catch (ManagerCreateException e) {
                    sendNotAcceptable(h);
                }
            } else {
                try {
                    taskManager.updateSubtask(subtask);
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
            int subtaskId = getId(h);
            taskManager.removeSubtask(subtaskId);
            sendText(h, "Subtask was deleted");
        } else {
            sendBadRequest(h);
        }
    }
}