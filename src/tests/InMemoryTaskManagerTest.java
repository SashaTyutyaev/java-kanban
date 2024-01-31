package tests;

import manager.tasks.memory.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tasks.TaskStatus.DONE;
import static tasks.TaskStatus.NEW;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private Epic epicWithNewAndDoneSub;
    private Epic epicWithNewSubs;
    private Epic epicWithDoneSubs;
    private Subtask subWithDoneStatus;
    private Subtask subWithNewStatus;

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

    @Test
    void addNewTask() {
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewTaskIfNull() {
        taskManager.addNewTask(null);
        assertEquals(0, taskManager.getTasks().size(), "Неверное количество задач");
    }

    @Test
    void addNewSubtask() {
        taskManager.addNewEpic(epic);
        assertEquals(1, taskManager.getEpics().size(), "Важная задача отсутсвует");
        Subtask subtask = new Subtask("SubName", "SubDescription", NEW, epic.getId());
        final int subId = taskManager.addNewSubtask(subtask);

        final Subtask savedSub = taskManager.getSubtask(subId);

        assertNotNull(savedSub, "Подзадача не найдена");
        assertEquals(subtask, savedSub, "Подзадачи не совпадают");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают");
    }

    @Test
    void addNewSubtaskIfNull() {
        taskManager.addNewSubtask(null);
        assertEquals(0, taskManager.getSubtasks().size(), "Неверное количество подазадач");
    }

    @Test
    void addNewEpic() {
        final int epicId = taskManager.addNewEpic(epic);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Важная задача не найдена");
        assertEquals(epic, savedEpic, "Задачи не совпдадают");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество задач");
        assertEquals(epic, epics.get(0), "Задачи не совпадают");
    }

    @Test
    void addNewEpicIfNull() {
        taskManager.addNewEpic(null);
        assertEquals(0, taskManager.getEpics().size(), "Неверное количество задач");
    }

    @Test
    void updateTask() {
        final int id = taskManager.addNewTask(task);
        taskManager.updateTask(task);

        final Task updatedTask = taskManager.getTask(id);

        assertNotNull(updatedTask, "Задача не найдена");
        assertEquals(task, updatedTask, "Задачи не совпадают");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не вощвращаются");
        assertEquals(1, tasks.size(), "Неверное количесвто задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void updateTaskIfNull() {
        taskManager.updateTask(null);
        assertEquals(0, taskManager.getTasks().size(), "Неверное количество задач");
    }

    @Test
    void updateSubtask() {
        taskManager.addNewEpic(epic);
        assertEquals(1, taskManager.getEpics().size(), "Важная задача отсутсвует");
        Subtask subtask = new Subtask("SubName", "SubDescription", NEW, epic.getId());
        final int subId = taskManager.addNewSubtask(subtask);

        final Subtask updatedSub = taskManager.getSubtask(subId);

        assertNotNull(updatedSub, "Подзадача не найдена");
        assertEquals(subtask, updatedSub, "Подзадачи не совпадают");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Неверное количество задач");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают");

    }

    @Test
    void updateSubtaskIfNull() {
        taskManager.updateSubtask(null);
        assertEquals(0, taskManager.getSubtasks().size(), "Неверное количество задач");
    }

    @Test
    void updateEpicWithNewAndDoneSubs() {
        final int id = taskManager.addNewEpic(epicWithNewAndDoneSub);
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id);
        Subtask doneSub = new Subtask("SubName", "SubDescription", DONE, id);
        taskManager.addNewSubtask(newSub);
        taskManager.addNewSubtask(doneSub);
        taskManager.updateEpic(epicWithNewAndDoneSub);
        assertEquals(TaskStatus.IN_PROGRESS, epicWithNewAndDoneSub.getStatus());

        final Epic updatedEpic = taskManager.getEpic(epicWithNewAndDoneSub.getId());

        assertNotNull(updatedEpic, "Задача не найдена");
        assertEquals(epicWithNewAndDoneSub, updatedEpic, "Задачи не совпадают");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Неверное колиество задач");
        assertEquals(epicWithNewAndDoneSub, epics.get(0), "Задачи не совпадают");
    }

    @Test
    void updateEpicWithDoneSubs() {
        final int id = taskManager.addNewEpic(epicWithDoneSubs);
        Subtask doneSub = new Subtask("SubName", "SubDescription", DONE, id);
        taskManager.addNewSubtask(doneSub);
        taskManager.updateEpic(epicWithDoneSubs);
        assertEquals(DONE, epicWithDoneSubs.getStatus());

        final Epic updatedEpic = taskManager.getEpic(epicWithDoneSubs.getId());

        assertNotNull(updatedEpic, "Задача не найдена");
        assertEquals(epicWithDoneSubs, updatedEpic, "Задачи не совпадают");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Неверное количесво задач");
        assertEquals(epicWithDoneSubs, epics.get(0), "Задачи не совпадают");
    }

    @Test
    void updateEpicWithNewSubs() {
        final int id = taskManager.addNewEpic(epicWithNewSubs);
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id);
        taskManager.addNewSubtask(newSub);
        taskManager.updateEpic(epicWithNewSubs);
        assertEquals(NEW, epicWithNewSubs.getStatus());

        final Epic updatedEpic = taskManager.getEpic(epicWithNewSubs.getId());

        assertNotNull(updatedEpic, "Задача не найдена");
        assertEquals(epicWithNewSubs, updatedEpic, "Задачи не совпадают");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество задач");
        assertEquals(epicWithNewSubs, epics.get(0), "Задачи не совпадают");
    }

    @Test
    void updateEpicWithoutSubs() {
        taskManager.addNewEpic(epic);
        taskManager.updateEpic(epic);
        assertEquals(NEW, epic.getStatus());

        final Epic updatedEpic = taskManager.getEpic(epic.getId());

        assertNotNull(updatedEpic, "Задача не найдена");
        assertEquals(epic, updatedEpic, "Задачи не совпадают");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество задач");
        assertEquals(0, epic.getSubtaskId().size(), "Неверное количество подазадач");
        assertEquals(epic, epics.get(0), "Задачи не совпадают");
    }

    @Test
    void updateEpicIfNull() {
        taskManager.updateEpic(null);
        assertEquals(0, taskManager.getEpics().size(), "Неверное количество задач");
    }

    @Test
    void deleteAllTasks() {
        taskManager.addNewTask(task);
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTasks().size(), "Список задач не очистился");
    }

    @Test
    void deleteAllSubtasks() {
        final int id = taskManager.addNewEpic(epic);
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id);
        Subtask newSub2 = new Subtask("SubName", "SubDescription", NEW, id);
        taskManager.addNewSubtask(newSub);
        taskManager.addNewSubtask(newSub2);
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getSubtasks().size(), "Список подзадач не очистился");
    }

    @Test
    void deleteAllEpics() {
        taskManager.addNewEpic(epic);
        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getEpics().size(), "Список задач не очистился");
    }

    @Test
    void deleteTaskById() {
        Task task2 = new Task("TaskName", "TaskDesceription", NEW);
        final int id = taskManager.addNewTask(task);
        taskManager.addNewTask(task2);
        Task removedTask = taskManager.getTask(id);
        assertNotNull(removedTask, "Задача не найдена");
        assertEquals(task, removedTask, "Задачи не совпадают");
        taskManager.deleteTask(id);
        assertEquals(1, taskManager.getTasks().size(), "Задача не удалилась из списка");
    }

    @Test
    void deleteSubtaskById() {
        final int id = taskManager.addNewEpic(epic);
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id);
        Subtask newSub2 = new Subtask("SubName", "SubDescription", NEW, id);
        taskManager.addNewSubtask(newSub);
        taskManager.addNewSubtask(newSub2);
        Subtask removedSub = taskManager.getSubtask(newSub2.getId());
        assertNotNull(removedSub, "Подзадача не найдена");
        assertEquals(newSub2, removedSub, "Задачи не совпадают");
        taskManager.deleteSubtask(newSub2.getId());
        assertEquals(1, taskManager.getSubtasks().size(), "Подзадача не удалилась из списка");
    }

    @Test
    void deleteEpicById() {
        final int id = taskManager.addNewEpic(epic);
        final int id2 = taskManager.addNewEpic(epicWithDoneSubs);
        Epic removedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(removedEpic, "Задача не найдена");
        assertEquals(epic, removedEpic, "Задачи не совпадают");
        taskManager.deleteEpic(id);
        assertEquals(1, taskManager.getEpics().size(), "Задача не удалилась из списка");
    }
}