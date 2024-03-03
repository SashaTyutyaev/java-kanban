package main.tests;

import main.manager.tasks.file.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        super.beforeEach();
        taskManager = new FileBackedTasksManager(new File("src/resources/testTasks.csv").toPath());
    }

    @AfterEach
    void afterEach() {
       super.afterEach();
    }

    @Test
    void save() {
        FileBackedTasksManager fb = new FileBackedTasksManager(new File("src/resources/testTasks2.csv").toPath());
        Task task1 = new Task("Task #1", "Task1 description", TaskStatus.NEW, 30,
                LocalDateTime.of(2024, 2, 11, 15, 0, 0));
        Task task2 = new Task("Task #2", "Task2 description", TaskStatus.IN_PROGRESS, 30,
                LocalDateTime.of(2024, 2, 11, 16, 0, 0));
        Task task3 = new Task("Task #3", "Task3 description", TaskStatus.IN_PROGRESS, 30,
                LocalDateTime.of(2024, 2, 11, 17, 0, 0));
        fb.addNewTask(task1);
        fb.getTask(task1.getId());
        fb.addNewTask(task2);
        fb.getTask(task2.getId());
        fb.addNewTask(task3);
        fb.getTask(task3.getId());
        Epic epic1 = new Epic("Epic #1", "Epic1 description", TaskStatus.NEW);
        Epic epic2 = new Epic("Epic #2", "Epic2 description", TaskStatus.NEW);
        fb.addNewEpic(epic1);
        fb.getEpic(epic1.getId());
        fb.addNewEpic(epic2);
        fb.getEpic(epic2.getId());
        Subtask subtask1 = new Subtask("Subtask #1-1", "Subtask1 description", TaskStatus.NEW, epic1.getId(),
                30, LocalDateTime.of(2024, 1, 1, 1, 1, 1));
        Subtask subtask2 = new Subtask("Subtask #2-1", "Subtask2 description", TaskStatus.NEW, epic2.getId(),
                40, LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        fb.addNewSubtask(subtask1);
        fb.getSubtask(subtask1.getId());
        fb.addNewSubtask(subtask2);
        fb.getSubtask(subtask2.getId());

        compareFiles("src/resources/testTasks.csv", "src/resources/testTasks2.csv");
    }

    private void compareFiles(String expectedFile, String actualFile) {
        FileBackedTasksManager expectedFb = FileBackedTasksManager.loadFromFile(new File(expectedFile));
        FileBackedTasksManager actualFb = FileBackedTasksManager.loadFromFile(new File(actualFile));

        int i = 0;
        Task task;
        Task expectedTask;

        while (i < expectedFb.getTasks().size()) {
            expectedTask = expectedFb.getTasks().get(i);
            task = actualFb.getTasks().get(i);

            assertEquals(expectedTask.getName(), task.getName());
            assertEquals(expectedTask.getDescription(), task.getDescription());
            assertEquals(expectedTask.getStatus(), task.getStatus());
            assertEquals(expectedTask.getId(), task.getId());
            assertEquals(expectedTask.getEpicType(), task.getEpicType());

            expectedFb.deleteTask(expectedTask.getId());


            i++;
        }

        i = 0;

        while (i < expectedFb.getHistory().size()) {
            expectedTask = expectedFb.getHistory().get(i);
            task = actualFb.getHistory().get(i);

            assertEquals(expectedTask.getName(), task.getName());
            assertEquals(expectedTask.getDescription(), task.getDescription());
            assertEquals(expectedTask.getStatus(), task.getStatus());
            assertEquals(expectedTask.getId(), task.getId());
            assertEquals(expectedTask.getEpicType(), task.getEpicType());

            expectedFb.getHistory().remove(i);

            i++;
        }

    }


}