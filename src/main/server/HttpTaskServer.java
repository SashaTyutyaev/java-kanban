package main.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import main.manager.Managers;
import main.manager.tasks.TaskManager;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;


public class HttpTaskServer {

    public static final int PORT = 5000;

    private final HttpServer server;
    private final Gson gson;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException, InterruptedException {
        taskManager = Managers.getDefault();
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/task", this::handleTask);
        server.createContext("/tasks/subtask", this::handleSubtask);
        server.createContext("/tasks/epic", this::handleEpic);
        server.createContext("/tasks/subtask/epic", this::handleSubtaskByEpicId);
        server.createContext("/tasks/history", this::handleHistory);
        server.createContext("/tasks", this::handlePrioritizedTasks);
    }

    private void handlePrioritizedTasks(HttpExchange h) {
        try {
            String path = h.getRequestURI().getPath();
            String requestMethod = h.getRequestMethod();

            switch (requestMethod) {
                case "GET":
                    getPrioritizedTasks(h, path);
                case "DELETE":
                    deletePrioritizedTasks(h, path);
                default:
                    System.out.println("Ожидается запрос GET или DELETE, получен неккоректный запрос " + requestMethod);
                    h.sendResponseHeaders(405, 0);
                    break;
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            h.close();
        }
    }

    private void getPrioritizedTasks(HttpExchange h, String path) throws IOException {
        if (Pattern.matches("^/tasks$", path)) {
            String response = gson.toJson(taskManager.getPrioritizedTasks());
            sendText(h, response);
        }
    }

    private void deletePrioritizedTasks(HttpExchange h, String path) throws IOException {
        if (Pattern.matches("/tasks", path)) {
            taskManager.deleteAllTasks();
            h.sendResponseHeaders(200, 0);
        }
    }

    private void handleTask(HttpExchange h) {
        try {
            String method = h.getRequestMethod();
            String query = h.getRequestURI().getQuery();

            switch (method) {
                case "GET":
                    if (query != null) {
                        String queryId = query.substring(3);
                        int id = parsePathId(queryId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getTask(id));
                            sendText(h, response);
                            break;
                        } else {
                            System.out.println("Получен неккоретный id - " + id);
                            h.sendResponseHeaders(405, 0);
                            break;
                        }
                    } else {
                        String response = gson.toJson(taskManager.getTasks());
                        sendText(h, response);
                        break;
                    }

                case "POST":
                    InputStream inputStream = h.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);

                    if (query != null) {
                        taskManager.updateTask(task);
                        System.out.println("Обновили задачу под идентификатором - " + task.getId());
                        h.sendResponseHeaders(201, 0);
                        break;
                    } else {
                        taskManager.addNewTask(task);
                        System.out.println("Добавили новую задачу типа TASK");
                        h.sendResponseHeaders(201, 0);
                        break;
                    }

                case "DELETE":
                    if (query != null) {
                        String queryId = query.substring(3);
                        int id = parsePathId(queryId);
                        if (id != -1) {
                            taskManager.deleteTask(id);
                            System.out.println("Удалили задачу под идентификатором - " + id);
                            h.sendResponseHeaders(200, 0);
                            break;
                        } else {
                            System.out.println("Получен неккоретный id - " + id);
                            h.sendResponseHeaders(405, 0);
                            break;
                        }
                    } else {
                        taskManager.deleteAllTasks();
                        System.out.println("Удалили все задачи типа TASK");
                        h.sendResponseHeaders(200, 0);
                        break;
                    }
                default:
                    System.out.println("Ожидается GET/POST/DELETE запрос, получен неккоректный запрос " + method);
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            h.close();
        }
    }

    private void handleEpic(HttpExchange h) {
        try {
            String method = h.getRequestMethod();
            String query = h.getRequestURI().getQuery();

            switch (method) {
                case "GET":
                    if (query != null) {
                        String queryId = query.substring(3);
                        int id = parsePathId(queryId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getEpic(id));
                            sendText(h, response);
                            break;
                        } else {
                            System.out.println("Получен неккоретный id - " + id);
                            h.sendResponseHeaders(405, 0);
                            break;
                        }
                    } else {
                        String response = gson.toJson(taskManager.getEpics());
                        sendText(h, response);
                        break;
                    }

                case "POST":
                    InputStream inputStream = h.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Epic task = gson.fromJson(body, Epic.class);
                    if (query != null) {
                        taskManager.updateEpic(task);
                        System.out.println("Обновили задачу под идентификатором - " + task.getId());
                        h.sendResponseHeaders(201, 0);
                        break;
                    } else {
                        taskManager.addNewEpic(task);
                        h.sendResponseHeaders(201, 0);
                        System.out.println("Добавили новую задачу типа EPIC");
                        break;
                    }

                case "DELETE":
                    if (query != null) {
                        String queryId = query.substring(3);
                        int id = parsePathId(queryId);
                        if (id != -1) {
                            taskManager.deleteEpic(id);
                            System.out.println("Удалили задачу под идентификатором - " + id);
                            h.sendResponseHeaders(200, 0);
                            break;
                        } else {
                            System.out.println("Получен неккоретный id - " + id);
                            h.sendResponseHeaders(405, 0);
                            break;
                        }
                    } else {
                        taskManager.deleteAllEpics();
                        System.out.println("Удалили все задачи типа EPIC");
                        h.sendResponseHeaders(200, 0);
                        break;
                    }
                default:
                    System.out.println("Ожидается GET/POST/DELETE запрос, получен неккоректный запрос " + method);
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            h.close();
        }
    }

    private void handleSubtask(HttpExchange h) {
        try {
            String method = h.getRequestMethod();
            String query = h.getRequestURI().getQuery();

            switch (method) {
                case "GET":
                    if (query != null) {
                        String queryId = query.substring(3);
                        int id = parsePathId(queryId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getSubtask(id));
                            sendText(h, response);
                            break;
                        } else {
                            System.out.println("Получен неккоретный id - " + id);
                            h.sendResponseHeaders(405, 0);
                            break;
                        }
                    } else {
                        String response = gson.toJson(taskManager.getSubtasks());
                        sendText(h, response);
                        break;
                    }

                case "POST":
                    InputStream inputStream = h.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Subtask task = gson.fromJson(body, Subtask.class);
                    if (query != null) {
                        taskManager.updateSubtask(task);
                        h.sendResponseHeaders(201, 0);
                        System.out.println("Обновили задачу под идентификатором - " + task.getId());
                        break;
                    } else {
                        taskManager.addNewSubtask(task);
                        h.sendResponseHeaders(201, 0);
                        System.out.println("Добавили новую задачу типа SUBTASK");
                        break;
                    }

                case "DELETE":
                    if (query != null) {
                        String queryId = query.substring(3);
                        int id = parsePathId(queryId);
                        if (id != -1) {
                            taskManager.deleteSubtask(id);
                            System.out.println("Удалили задачу под идентификатором - " + id);
                            h.sendResponseHeaders(200, 0);
                            break;
                        } else {
                            System.out.println("Получен неккоретный id - " + id);
                            h.sendResponseHeaders(405, 0);
                            break;
                        }
                    } else {
                        taskManager.deleteAllSubtasks();
                        System.out.println("Удалили все задачи типа SUBTASK");
                        h.sendResponseHeaders(200, 0);
                        break;
                    }
                default:
                    System.out.println("Ожидается GET/POST/DELETE запрос, получен неккоректный запрос " + method);
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            h.close();
        }
    }

    private void handleHistory(HttpExchange h) {
        try {
            String path = h.getRequestURI().getPath();
            String method = h.getRequestMethod();

            switch (method) {
                case "GET":
                    getHistory(h, path);
                default:
                    System.out.println("Ожидается GET запрос, получен неккоректный запрос " + method);
                    h.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            h.close();
        }
    }

    private void getHistory(HttpExchange h, String path) throws IOException {
        if (Pattern.matches("^/tasks/history$", path)) {
            String response = gson.toJson(taskManager.getHistory());
            sendText(h, response);
        }
    }

    private void handleSubtaskByEpicId(HttpExchange h) {
        try {
            String method = h.getRequestMethod();
            String query = h.getRequestURI().getQuery();

            switch (method) {
                case "GET":
                    getEpicSubtasks(h, query);
                case "DELETE":
                    deleteEpicSubtasks(h, query);
                default:
                    System.out.println("Ожидается GET запрос, получен неккоректный запрос " + method);
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            h.close();
        }
    }

    private void getEpicSubtasks(HttpExchange h, String query) throws IOException {
        if (query != null) {
            String queryId = query.substring(3);
            int id = parsePathId(queryId);
            if (id != -1) {
                List<Subtask> subs = taskManager.getEpicSubtasks(id);
                String response = gson.toJson(subs);
                sendText(h, response);
            } else {
                System.out.println("Получен неккоретный id - " + id);
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void deleteEpicSubtasks(HttpExchange h, String query) throws IOException {
        if (query != null) {
            String queryId = query.substring(3);
            int id = parsePathId(queryId);
            if (id != -1) {
                taskManager.getEpic(id).cleanSubtaskId();
                System.out.println("Удалили все SUBTASK у EPIC под идентификатором - " + id);
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("Получен неккоретный id - " + id);
                h.sendResponseHeaders(405, 0);
            }
        }
    }


    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        System.out.println("Остановили сервер на порту " + PORT);
        server.stop(0);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
