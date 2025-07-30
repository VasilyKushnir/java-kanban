package taskmanager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasks.Task;
import java.io.IOException;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            HttpMethod method = HttpMethod.valueOf(getMethod(exchange));
            if (method.equals(HttpMethod.GET)) {
                get(exchange);
            } else {
                sendBadRequest(exchange);
            }
        } catch (Exception e) {
            sendInternalServerError(exchange, e);
        }
    }

    public void get(HttpExchange h) throws IOException {
        Gson gson = getGson();
        Set<Task> prioritizedSet = taskManager.getPrioritizedTasks();
        String historyJson = gson.toJson(prioritizedSet);
        sendText(h,historyJson);
    }
}