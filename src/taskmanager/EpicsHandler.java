package taskmanager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasks.Epic;
import tasks.Subtask;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
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

    protected boolean requiresEpicSubtasks(HttpExchange h) {
        String[] pathArray = getPathArray(h);
        return pathArray.length == 4 && "subtasks".equals(pathArray[3]);
    }

    public void get(HttpExchange h) throws IOException {
        Gson gson = getGson();
        if (!uriContainsId(h)) {
            ArrayList<Epic> epicList = taskManager.getEpicList();
            String epicsArrayJson = gson.toJson(epicList);
            sendText(h, epicsArrayJson);
        } else if (hasId(h) && requiresEpicSubtasks(h)) {
            Epic epic = taskManager.getEpic(getId(h));
            if (epic != null) {
                ArrayList<Subtask> epicSubtasksList = taskManager.getSubtasksForEpic(getId(h));
                String epicSubtasksJson = gson.toJson(epicSubtasksList);
                sendText(h, epicSubtasksJson);
            } else {
                sendNotFound(h);
            }
        } else if (hasId(h)) {
            Epic epic = taskManager.getEpic(getId(h));
            if (epic != null) {
                sendText(h, gson.toJson(epic));
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
            Epic epic = gson.fromJson(body, Epic.class);
            Epic epicForCreation = new Epic(epic.getName(), epic.getDescription());
            taskManager.createEpic(epicForCreation);
            sendCreated(h);
        } catch (JsonSyntaxException e) {
            sendBadRequest(h);
        }
    }

    public void delete(HttpExchange h) throws IOException {
        if (hasId(h)) {
            int epicId = getId(h);
            taskManager.removeEpic(epicId);
            sendText(h, "Epic was deleted");
        } else {
            sendBadRequest(h);
        }
    }
}