package main.tests;

import main.manager.tasks.memory.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static main.tasks.TaskStatus.DONE;
import static main.tasks.TaskStatus.NEW;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    protected Epic epicWithNewAndDoneSub;
    protected Epic epicWithNewSubs;
    protected Epic epicWithDoneSubs;
    protected Epic testEpic = new Epic("EpicTest1", "EpicTestDescription1", TaskStatus.NEW, 1);
    protected Subtask subWithDoneStatus;
    protected Subtask subWithNewStatus;

    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
        taskManager = new InMemoryTaskManager();

        epicWithNewAndDoneSub = new Epic("EpicName", "EpicDescription", NEW);
        subWithDoneStatus = new Subtask("DoneSubName", "DoneSubDescription", DONE, epicWithNewAndDoneSub.getId());
        subWithNewStatus = new Subtask("NewSubName", "NewSubDescription", NEW, epicWithNewAndDoneSub.getId());
        epicWithDoneSubs = new Epic("Epic2Name", "Epic2Description", NEW);
        epicWithNewSubs = new Epic("Epic3Name", "Epic3Description", NEW);
    }

    @AfterEach
    public void afterEach() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
    }

}