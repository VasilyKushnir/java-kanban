package taskmanager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasks.Task;
import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = getMethod(exchange);
            if (method.equals("GET")) {
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
        List<Task> historyList = taskManager.getHistory();
        String historyJson = gson.toJson(historyList);
        sendText(h,historyJson);
    }
}