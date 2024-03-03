package main.tests;

import com.google.gson.Gson;
import main.manager.Managers;
import main.manager.tasks.TaskManager;
import main.server.HttpTaskServer;
import main.server.KVServer;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private HttpTaskServer taskServer;
    private KVServer kvServer;
    private HttpClient client;
    private Gson gson;
    private TaskManager taskManager;


    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        taskServer = new HttpTaskServer();
        taskServer.start();
        client = HttpClient.newHttpClient();
        gson = Managers.getGson();
        taskManager = Managers.getDefault();
    }

    @AfterEach
    void afterEach() {
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    void addNewTask_Return201StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/task");
        Task task = new Task("TaskName", "TaskDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void addNewEpic_Return201StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void addNewSubtask_Return201StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/tasks/subtask");
        Subtask subtask = new Subtask("SubName", "SubDescription", TaskStatus.NEW, 1, 2);
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());
    }

    @Test
    void UpdateTask_Return201StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/task");
        Task task = new Task("TaskName", "TaskDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());


        Task updTask = new Task("UpdTaskName", "UpdTaskDescription", TaskStatus.NEW, task.getId());
        String jsonUpdTask = gson.toJson(updTask);

        URI updUrl = URI.create("http://localhost:5000/tasks/task/?id=" + updTask.getId());


        final HttpRequest.BodyPublisher updBody = HttpRequest.BodyPublishers.ofString(jsonUpdTask);
        HttpRequest updRequest = HttpRequest.newBuilder()
                .POST(updBody)
                .uri(updUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> updResponse = client.send(updRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, updResponse.statusCode());

    }

    @Test
    void UpdateEpic_Return201StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());


        Task updEpic = new Epic("UpdTaskName", "UpdTaskDescription", TaskStatus.NEW, epic.getId());
        String jsonUpdEpic = gson.toJson(updEpic);

        URI updUrl = URI.create("http://localhost:5000/tasks/epic/?id=" + updEpic.getId());


        final HttpRequest.BodyPublisher updBody = HttpRequest.BodyPublishers.ofString(jsonUpdEpic);
        HttpRequest updRequest = HttpRequest.newBuilder()
                .POST(updBody)
                .uri(updUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> updResponse = client.send(updRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, updResponse.statusCode());

    }

    @Test
    void updateSubtask_Return201StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/tasks/subtask");
        Subtask subtask = new Subtask("SubName", "SubDescription", TaskStatus.NEW, 2, 1);
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());


        Subtask updSubtask = new Subtask("UpdSubtaskName", "UpdSubtaskDescription", TaskStatus.NEW, 2, 1);
        String jsonUpdSubtask = gson.toJson(updSubtask);

        URI updUrl = URI.create("http://localhost:5000/tasks/task/?id=" + updSubtask.getId());


        final HttpRequest.BodyPublisher updBody = HttpRequest.BodyPublishers.ofString(jsonUpdSubtask);
        HttpRequest updRequest = HttpRequest.newBuilder()
                .POST(updBody)
                .uri(updUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> updResponse = client.send(updRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, updResponse.statusCode());
    }

    @Test
    void getAllTasks_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/task");
        Task task = new Task("TaskName", "TaskDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task task2 = new Task("Task2Name", "Task2Description", TaskStatus.NEW, 2);
        String jsonTask2 = gson.toJson(task2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String tasks = gson.toJson(taskManager.getTasks());

        assertEquals(200, getResponse.statusCode());
        assertEquals(tasks, getResponse.body());
    }

    @Test
    void getTaskById_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/task");
        Task task = new Task("TaskName", "TaskDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task task2 = new Task("Task2Name", "Task2Description", TaskStatus.NEW, 2);
        String jsonTask2 = gson.toJson(task2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        URI uri = URI.create("http://localhost:5000/tasks/task/?id=2");

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String ExpectedTask = gson.toJson(task2);

        assertEquals(200, getResponse.statusCode());
        assertEquals(ExpectedTask, getResponse.body());
    }

    @Test
    void getAllEpics_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Epic epic2 = new Epic("Epic2Name", "Epic2Description", TaskStatus.NEW, 2);
        String jsonEpic2 = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonEpic2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String tasks = gson.toJson(taskManager.getEpics());

        assertEquals(200, getResponse.statusCode());
        assertEquals(tasks, getResponse.body());
    }

    @Test
    void getEpicById_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Epic epic2 = new Epic("Epic2Name", "Epic2Description", TaskStatus.NEW, 2);
        String jsonEpic2 = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonEpic2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        URI uri = URI.create("http://localhost:5000/tasks/epic/?id=" + epic.getId());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String expectedEpic = gson.toJson(taskManager.getEpic(epic.getId()));

        assertEquals(200, getResponse.statusCode());
        assertEquals(expectedEpic, getResponse.body());
    }

    @Test
    void getAllSubtasks_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/tasks/subtask");
        Subtask subtask = new Subtask("SubName", "SubDescription", TaskStatus.NEW, epic.getId());
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());

        Subtask subtask2 = new Subtask("Sub2Name", "Sub2Description", TaskStatus.NEW, epic.getId());
        String jsonSubtask2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher subBody2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest subRequest2 = HttpRequest.newBuilder()
                .POST(subBody2)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse2 = client.send(subRequest2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse2.statusCode());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(subUrl)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String subtasks = gson.toJson(taskManager.getSubtasks());

        assertEquals(200, getResponse.statusCode());
        assertEquals(subtasks, getResponse.body());
    }

    @Test
    void getSubtaskById_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/tasks/subtask");
        Subtask subtask = new Subtask("SubName", "SubDescription", TaskStatus.NEW, 2, epic.getId());
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());

        Subtask subtask2 = new Subtask("Sub2Name", "Sub2Description", TaskStatus.NEW, 3, epic.getId());
        String jsonSubtask2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher subBody2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest subRequest2 = HttpRequest.newBuilder()
                .POST(subBody2)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse2 = client.send(subRequest2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse2.statusCode());

        URI uri = URI.create("http://localhost:5000/tasks/subtask/?id=" + subtask2.getId());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String subtaskJson = gson.toJson(taskManager.getSubtask(subtask2.getId()));

        assertEquals(200, getResponse.statusCode());
        assertEquals(subtaskJson, getResponse.body());
    }

    @Test
    void getEpicSubtasks_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/tasks/subtask");
        Subtask subtask = new Subtask("SubName", "SubDescription", TaskStatus.NEW, 2, epic.getId());
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());

        Subtask subtask2 = new Subtask("Sub2Name", "Sub2Description", TaskStatus.NEW, 3, epic.getId());
        String jsonSubtask2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher subBody2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest subRequest2 = HttpRequest.newBuilder()
                .POST(subBody2)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse2 = client.send(subRequest2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse2.statusCode());

        URI uri = URI.create("http://localhost:5000/tasks/subtask/epic/?id=" + epic.getId());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String subtaskJson = gson.toJson(taskManager.getEpicSubtasks(epic.getId()));

        assertEquals(200, getResponse.statusCode());
        assertEquals(subtaskJson, getResponse.body());
    }

    @Test
    void deleteAllTasks_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/task");
        Task task = new Task("TaskName", "TaskDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task task2 = new Task("Task2Name", "Task2Description", TaskStatus.NEW, 2);
        String jsonTask2 = gson.toJson(task2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, getResponse.statusCode());
        assertEquals(0, taskManager.getTasks().size());

    }

    @Test
    void deleteTaskById_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/task");
        Task task = new Task("TaskName", "TaskDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task task2 = new Task("Task2Name", "Task2Description", TaskStatus.NEW, 2);
        String jsonTask2 = gson.toJson(task2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        URI uri = URI.create("http://localhost:5000/tasks/task/?id=" + task2.getId());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, getResponse.statusCode());
        assertFalse(taskManager.getTasks().contains(task2));
    }

    @Test
    void deleteAllEpics_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Epic epic2 = new Epic("Epic2Name", "Epic2Description", TaskStatus.NEW, 2);
        String jsonEpic2 = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonEpic2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, getResponse.statusCode());
        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    void deleteEpicById_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Epic epic2 = new Epic("Epic2Name", "Epic2Description", TaskStatus.NEW, 2);
        String jsonEpic2 = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonEpic2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        URI uri = URI.create("http://localhost:5000/tasks/epic/?id=" + epic.getId());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, getResponse.statusCode());
        assertFalse(taskManager.getEpics().contains(epic));
    }

    @Test
    void deleteAllSubtasks_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/tasks/subtask");
        Subtask subtask = new Subtask("SubName", "SubDescription", TaskStatus.NEW, epic.getId());
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());

        Subtask subtask2 = new Subtask("Sub2Name", "Sub2Description", TaskStatus.NEW, epic.getId());
        String jsonSubtask2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher subBody2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest subRequest2 = HttpRequest.newBuilder()
                .POST(subBody2)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse2 = client.send(subRequest2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse2.statusCode());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(subUrl)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, getResponse.statusCode());
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void deleteSubtaskById_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/tasks/subtask");
        Subtask subtask = new Subtask("SubName", "SubDescription", TaskStatus.NEW, 2, epic.getId());
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());

        Subtask subtask2 = new Subtask("Sub2Name", "Sub2Description", TaskStatus.NEW, 3, epic.getId());
        String jsonSubtask2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher subBody2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest subRequest2 = HttpRequest.newBuilder()
                .POST(subBody2)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse2 = client.send(subRequest2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse2.statusCode());

        URI deleteSubUrl = URI.create("http://localhost:5000/tasks/subtask/?id=" + subtask2.getId());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteSubUrl)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, getResponse.statusCode());
        assertFalse(taskManager.getSubtasks().contains(subtask2));
    }

    @Test
    void deleteEpicSubtasks_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/epic");
        Epic epic = new Epic("EpicName", "EpicDescription", TaskStatus.NEW, 1);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/tasks/subtask");
        Subtask subtask = new Subtask("SubName", "SubDescription", TaskStatus.NEW, 2, epic.getId());
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());

        Subtask subtask2 = new Subtask("Sub2Name", "Sub2Description", TaskStatus.NEW, 3, epic.getId());
        String jsonSubtask2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher subBody2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest subRequest2 = HttpRequest.newBuilder()
                .POST(subBody2)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse2 = client.send(subRequest2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse2.statusCode());

        URI uri = URI.create("http://localhost:5000/tasks/subtask/epic/?id=" + epic.getId());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, getResponse.statusCode());
        assertEquals(0, taskManager.getEpic(epic.getId()).getSubtaskId().size());
    }


    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/task");
        Task task = new Task("TaskName",
                "TaskDescription",
                TaskStatus.NEW,
                1,
                30,
                LocalDateTime.of(2022, 1, 1, 1, 1, 1));
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("TaskName2",
                "TaskDescription2",
                TaskStatus.NEW,
                2,
                30,
                LocalDateTime.of(2021, 2, 1, 1, 1, 1));
        String jsonTask2 = gson.toJson(task2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        URI priorityUrl = URI.create("http://localhost:5000/tasks");
        HttpRequest priorityRequest = HttpRequest.newBuilder()
                .GET()
                .uri(priorityUrl)
                .build();
        HttpResponse<String> priorityResponse = client.send(priorityRequest, HttpResponse.BodyHandlers.ofString());

        List<Task> actual = new ArrayList<>();
        actual.add(task2);
        actual.add(task);

        String expectedTasks = gson.toJson(actual);


        assertEquals(200, priorityResponse.statusCode());
        assertEquals(expectedTasks, priorityResponse.body());
    }
}