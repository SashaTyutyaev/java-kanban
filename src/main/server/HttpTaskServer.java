package main.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import main.manager.Managers;
import main.manager.tasks.TaskManager;
import main.manager.tasks.file.FileBackedTasksManager;
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
    private final FileBackedTasksManager fileBackedTasksManager;

    public HttpTaskServer() throws IOException, InterruptedException {
        this.taskManager = Managers.getDefault();
        this.fileBackedTasksManager = Managers.getFileManager();
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/task", this::handleTask);
        server.createContext("/tasks/subtask", this::handleSubtask);
        server.createContext("/tasks/epic", this::handleEpic);
        server.createContext("/tasks/subtask/epic", this::handleSubtaskByEpicId);
        server.createContext("/tasks/history", this::handleHistory);
        server.createContext("/tasks", this::handlePrioritizedTasks);
    }

    public FileBackedTasksManager getTaskManager() {
        return fileBackedTasksManager;
    }

    private void handlePrioritizedTasks(HttpExchange h) {
        try {
            String path = h.getRequestURI().getPath();
            String requestMethod = h.getRequestMethod();

            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks$", path)) {
                        String response = gson.toJson(taskManager.getPrioritizedTasks());
                        sendText(h, response);
                        break;
                    }
                case "DELETE":
                    if (Pattern.matches("/tasks", path)) {
                        taskManager.deleteAllTasks();
                        h.sendResponseHeaders(200, 0);
                        break;
                    }
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

    private void handleTask(HttpExchange h) {
        try {
            String path = h.getRequestURI().getPath();
            String method = h.getRequestMethod();

            switch (method) {
                case "GET":
                    if (Pattern.matches("^/tasks/task$", path)) {
                        String response = gson.toJson(taskManager.getTasks());
                        sendText(h, response);
                        break;
                    }

                    if (Pattern.matches("/tasks/task/?id=\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/task/?id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getTask(id));
                            sendText(h, response);
                            break;
                        } else {
                            System.out.println("Получен неккоретный id - " + id);
                            h.sendResponseHeaders(405, 0);
                        }
                    }
                    break;

                case "POST":
                    InputStream inputStream = h.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);
                    if (Pattern.matches("^/tasks/task/?id=\\d+$", path)) {
                        taskManager.updateTask(task);
                        System.out.println("Обновили задачу под идентификатором - " + task.getId());
                    } else {
                        taskManager.addNewTask(task);
                        System.out.println("Добавили новую задачу типа TASK");
                        break;
                    }

                case "DELETE":
                    if (Pattern.matches("^/tasks/task$", path)) {
                        taskManager.deleteAllTasks();
                        System.out.println("Удалили все задачи типа TASK");
                        h.sendResponseHeaders(200, 0);
                        break;
                    }

                    if (Pattern.matches("^/tasks/task/?id=\\d$", path)) {
                        String pathId = path.replaceFirst("/tasks/task/?id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            taskManager.deleteTask(id);
                            System.out.println("Удалили задачу под идентификатором - " + id);
                            break;
                        } else {
                            System.out.println("Получен неккоретный id - " + id);
                            h.sendResponseHeaders(405, 0);
                        }
                    }
                    break;
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
            String path = h.getRequestURI().getPath();
            String method = h.getRequestMethod();

            switch (method) {
                case "GET":
                    if (Pattern.matches("^/tasks/epic$", path)) {
                        String response = gson.toJson(taskManager.getEpics());
                        sendText(h, response);
                        break;
                    }

                    if (Pattern.matches("/tasks/epic/?id=\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/epic/?id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getEpic(id));
                            sendText(h, response);
                            break;
                        } else {
                            System.out.println("Получен неккоретный id - " + id);
                            h.sendResponseHeaders(405, 0);
                        }
                    }
                    break;

                case "POST":
                    InputStream inputStream = h.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Epic task = gson.fromJson(body, Epic.class);
                    if (Pattern.matches("^/tasks/epic/?id=\\d+$", path)) {
                        taskManager.updateEpic(task);
                        System.out.println("Обновили задачу под идентификатором - " + task.getId());
                    } else {
                        taskManager.addNewEpic(task);
                        System.out.println("Добавили новую задачу типа EPIC");
                    }
                    break;

                case "DELETE":
                    if (Pattern.matches("^/tasks/epic$", path)) {
                        taskManager.deleteAllEpics();
                        System.out.println("Удалили все задачи типа EPIC");
                        h.sendResponseHeaders(200, 0);
                        break;
                    }

                    if (Pattern.matches("^/tasks/epic/?id=\\d$", path)) {
                        String pathId = path.replaceFirst("/tasks/epic/?id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            taskManager.deleteEpic(id);
                            System.out.println("Удалили задачу под идентификатором - " + id);
                            break;
                        } else {
                            System.out.println("Получен неккоретный id - " + id);
                            h.sendResponseHeaders(405, 0);
                        }
                    }
                    break;
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
            String path = h.getRequestURI().getPath();
            String method = h.getRequestMethod();

            switch (method) {
                case "GET":
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        String response = gson.toJson(taskManager.getSubtasks());
                        sendText(h, response);
                        break;
                    }

                    if (Pattern.matches("/tasks/subtask/?id=\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/subtask/?id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getSubtask(id));
                            sendText(h, response);
                            break;
                        } else {
                            System.out.println("Получен неккоретный id - " + id);
                            h.sendResponseHeaders(405, 0);
                        }
                    }
                    break;

                case "POST":
                    InputStream inputStream = h.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Subtask task = gson.fromJson(body, Subtask.class);
                    if (Pattern.matches("^/tasks/subtask/?id=\\d+$", path)) {
                        taskManager.updateSubtask(task);
                        System.out.println("Обновили задачу под идентификатором - " + task.getId());
                    } else {
                        taskManager.addNewSubtask(task);
                        System.out.println("Добавили новую задачу типа SUBTASK");
                        break;
                    }

                case "DELETE":
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        taskManager.deleteAllSubtasks();
                        System.out.println("Удалили все задачи типа SUBTASK");
                        h.sendResponseHeaders(200, 0);
                        break;
                    }

                    if (Pattern.matches("^/tasks/subtask/?id=\\d$", path)) {
                        String pathId = path.replaceFirst("/tasks/subtask/?id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            taskManager.deleteSubtask(id);
                            System.out.println("Удалили задачу под идентификатором - " + id);
                            break;
                        } else {
                            System.out.println("Получен неккоретный id - " + id);
                            h.sendResponseHeaders(405, 0);
                        }
                    }
                    break;
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
                    if (Pattern.matches("^/tasks/history$", path)) {
                        String response = gson.toJson(taskManager.getHistory());
                        sendText(h, response);
                        break;
                    }
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

    private void handleSubtaskByEpicId(HttpExchange h) {
        try {
            String path = h.getRequestURI().getPath();
            String method = h.getRequestMethod();

            switch (method) {
                case "GET":
                    if (Pattern.matches("^/tasks/subtask/epic/?id=\\d$", path)) {
                        String pathId = path.replaceFirst("/tasks/subtask/epic/?id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            List<Subtask> subs = taskManager.getEpicSubtasks(id);
                            String response = gson.toJson(subs);
                            sendText(h, response);
                            break;
                        } else {
                            System.out.println("Получен неккоретный id - " + id);
                            h.sendResponseHeaders(405, 0);
                        }
                    }
                default:
                    System.out.println("Ожидается GET запрос, получен неккоректный запрос " + method);
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            h.close();
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
