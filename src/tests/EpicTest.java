package tests;

import manager.tasks.memory.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    Epic epic = new Epic("EpicTest1", "EpicTestDescription1", TaskStatus.NEW, 1);

    @BeforeEach
    public void beforeEach() {
        taskManager.addNewEpic(epic);
        taskManager.deleteAllSubtasks();
    }

    @Test
    public void shouldReturnCorrectStartTime() {
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.NEW, 2, 1, 20, LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.NEW, 3, 1, 30, LocalDateTime.of(2024, 1, 1, 1, 1));
        taskManager.addNewSubtask(sub2);
        epic.getEpicTime();
        assertNotNull(epic.getStartTime(), "Время начала не найдено");
        assertEquals(epic.getStartTime(), sub1.getStartTime(), "Время начала не совпадает");
    }

    @Test
    public void shouldReturnEpicWithoutSubs() {
        int subsSize = epic.getSubtaskId().size();
        assertEquals(0, subsSize);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusNewWhenAllSubsNew() {
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.NEW, 2, 1);
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.NEW, 3, 1);
        taskManager.addNewSubtask(sub2);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusDoneWhenAllSubsDone() {
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.DONE, 2, 1);
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.DONE, 3, 1);
        taskManager.addNewSubtask(sub2);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusInProgressWhenSubsNewAndDone() {
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.NEW, 2, 1);
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.DONE, 3, 1);
        taskManager.addNewSubtask(sub2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusInProgressWhenSubsInProgress() {
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.IN_PROGRESS, 2, 1);
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.IN_PROGRESS, 3, 1);
        taskManager.addNewSubtask(sub2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

}