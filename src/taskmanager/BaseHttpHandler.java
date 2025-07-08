package taskmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

abstract class BaseHttpHandler {
    protected String getMethod(HttpExchange h) {
        return h.getRequestMethod();
    }

    public Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }

    protected String[] getPathArray(HttpExchange h) {
        String path = h.getRequestURI().getPath();
        return path.split("/");
    }

    protected boolean uriContainsId(HttpExchange h) {
        return getPathArray(h).length > 2;
    }

    protected boolean hasId(HttpExchange h) {
        if (getPathArray(h).length > 2) {
            String thirdElement = getPathArray(h)[2];
            try {
                Integer.parseInt(thirdElement);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    protected int getId(HttpExchange h) {
        return Integer.parseInt(getPathArray(h)[2]);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendCreated(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201, -1);
        h.close();
    }

    protected void sendBadRequest(HttpExchange h) throws IOException {
        String text = "400 Bad Request";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(400, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        String text = "404 Not Found";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotAcceptable(HttpExchange h) throws IOException {
        String text = "406 Not Acceptable";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendInternalServerError(HttpExchange h, Exception e) throws IOException {
        e.printStackTrace();
        String text = "500 Internal Server Error";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(500, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}