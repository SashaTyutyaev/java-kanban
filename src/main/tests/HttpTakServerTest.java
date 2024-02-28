package main.tests;

import com.google.gson.Gson;
import main.manager.Managers;
import main.server.HttpTaskServer;
import main.server.KVServer;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTakServerTest {

    private HttpTaskServer taskServer;
    private KVServer kvServer;
    private HttpClient client;
    private Gson gson;


    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        taskServer = new HttpTaskServer();
        kvServer = new KVServer();
        taskServer.start();
        kvServer.start();
        client = HttpClient.newHttpClient();
        gson = Managers.getGson();

    }

    @AfterEach
    void afterEach() {
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    void addNewTask_Return201StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/task");
        Task task = new Task("TaskName", "TaskDescription", TaskStatus.NEW);
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
        assertTrue(response.body().matches("\\d+"));
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks/task");
        Task task = new Task("TaskName",
                "TaskDescription",
                TaskStatus.NEW,
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
                30,
                LocalDateTime.of(2022, 2, 1, 1, 1, 1));
        String jsonTask2 = gson.toJson(task2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        URI priorityUrl = URI.create("http://localhost:5000/tasks");
        HttpRequest priorityRequest = HttpRequest.newBuilder()
                .uri(priorityUrl)
                .GET()
                .build();
        HttpResponse<String> priorityResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> actual = new ArrayList<>();
        actual.add(task);
        actual.add(task2);

        String expectedTasks = gson.toJson(actual);


        assertEquals(200, response.statusCode());
        assertEquals(expectedTasks,priorityResponse.body());
    }
}