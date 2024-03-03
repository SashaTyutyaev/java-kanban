package main.tests;

import main.manager.Managers;
import main.manager.tasks.http.HttpTaskManager;
import main.server.KVServer;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private HttpTaskManager httpTaskManager;
    private KVServer kVServer;


    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        super.beforeEach();
        kVServer = new KVServer();
        kVServer.start();
        httpTaskManager = (HttpTaskManager) Managers.getDefault();
        taskManager = httpTaskManager;
    }

    @AfterEach
    void afterEach() {
        super.afterEach();
        kVServer.stop();
    }

    @Test
    void save() throws IOException, InterruptedException {
        Task task1 = new Task("Task #1", "Task1 description", TaskStatus.NEW, 30,
                LocalDateTime.of(2024, 2, 11, 15, 0, 0));
        Task task2 = new Task("Task #2", "Task2 description", TaskStatus.IN_PROGRESS, 30,
                LocalDateTime.of(2024, 2, 11, 16, 0, 0));
        Task task3 = new Task("Task #3", "Task3 description", TaskStatus.IN_PROGRESS, 30,
                LocalDateTime.of(2024, 2, 11, 17, 0, 0));
        httpTaskManager.addNewTask(task1);
        httpTaskManager.getTask(task1.getId());
        httpTaskManager.addNewTask(task2);
        httpTaskManager.getTask(task2.getId());
        httpTaskManager.addNewTask(task3);
        httpTaskManager.getTask(task3.getId());
        Epic epic1 = new Epic("Epic #1", "Epic1 description", TaskStatus.NEW);
        Epic epic2 = new Epic("Epic #2", "Epic2 description", TaskStatus.NEW);
        httpTaskManager.addNewEpic(epic1);
        httpTaskManager.getEpic(epic1.getId());
        httpTaskManager.addNewEpic(epic2);
        httpTaskManager.getEpic(epic2.getId());
        Subtask subtask1 = new Subtask("Subtask #1-1", "Subtask1 description", TaskStatus.NEW, epic1.getId(),
                30, LocalDateTime.of(2024, 1, 1, 1, 1, 1));
        Subtask subtask2 = new Subtask("Subtask #2-1", "Subtask2 description", TaskStatus.NEW, epic2.getId(),
                40, LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        httpTaskManager.addNewSubtask(subtask1);
        httpTaskManager.getSubtask(subtask1.getId());
        httpTaskManager.addNewSubtask(subtask2);
        httpTaskManager.getSubtask(subtask2.getId());

        HttpTaskManager newHttpTaskManager =(HttpTaskManager) Managers.getDefault();

        compareFiles(httpTaskManager, newHttpTaskManager);
    }

    private void compareFiles(HttpTaskManager actualManager, HttpTaskManager expectedManager) {

        int i = 0;
        Task task;
        Task expectedTask;

        while (i < expectedManager.getTasks().size()) {
            expectedTask = expectedManager.getTasks().get(i);
            task = actualManager.getTasks().get(i);

            assertEquals(expectedTask.getName(), task.getName());
            assertEquals(expectedTask.getDescription(), task.getDescription());
            assertEquals(expectedTask.getStatus(), task.getStatus());
            assertEquals(expectedTask.getId(), task.getId());
            assertEquals(expectedTask.getEpicType(), task.getEpicType());

            expectedManager.deleteTask(expectedTask.getId());


            i++;
        }

        i = 0;

        while (i < expectedManager.getHistory().size()) {
            expectedTask = expectedManager.getHistory().get(i);
            task = actualManager.getHistory().get(i);

            assertEquals(expectedTask.getName(), task.getName());
            assertEquals(expectedTask.getDescription(), task.getDescription());
            assertEquals(expectedTask.getStatus(), task.getStatus());
            assertEquals(expectedTask.getId(), task.getId());
            assertEquals(expectedTask.getEpicType(), task.getEpicType());

            expectedManager.getHistory().remove(i);

            i++;
        }

    }
}
