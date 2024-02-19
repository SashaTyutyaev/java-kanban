package main.tests;

import main.manager.history.InMemoryHistoryManager;
import main.manager.tasks.TaskManager;
import main.tasks.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static main.tasks.TaskStatus.DONE;
import static main.tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

abstract class TaskManagerTest<T extends TaskManager> {

    public T taskManager;
    protected Task task;
    protected Epic epic;
    protected Epic epicWithNewAndDoneSub;
    protected Epic epicWithNewSubs;
    protected Epic epicWithDoneSubs;
    private InMemoryHistoryManager historyManager;
    protected Epic testEpic = new Epic("EpicTest1", "EpicTestDescription1", TaskStatus.NEW, 1);
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void beforeEach() {
        task = new Task("taskName", "taskDescription", NEW, 30, LocalDateTime.of(2024, 1, 1, 1, 1));
        epic = new Epic("epicName", "epicDescription", NEW);
        epicWithNewAndDoneSub = new Epic("EpicName", "EpicDescription", NEW);
        epicWithDoneSubs = new Epic("Epic2Name", "Epic2Description", NEW);
        epicWithNewSubs = new Epic("Epic3Name", "Epic3Description", NEW);
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("task1Name","task1Description", TaskStatus.NEW,1);
        task2 = new Task("task2Name","task2Description", TaskStatus.NEW,2);
        task3 = new Task("task3Name","task3Description", TaskStatus.NEW,3);
    }

    @AfterEach
    void afterEach() {
        taskManager.deleteAllEpics();
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
    }

    @Test
    void getTaskById_ReturnCorrectTask_TaskInStorage() {
        taskManager.addNewTask(task);
        Task savedTask = taskManager.getTask(task.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
    }

    @Test
    void getSubtaskById_ReturnCorrectSubtask_SubtaskInStorage() {
        final int id = taskManager.addNewEpic(epic);
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id, 30, LocalDateTime.of(2024, 1, 6, 1, 1));
        final int subId = taskManager.addNewSubtask(newSub);
        Subtask savedSub = taskManager.getSubtask(subId);
        assertNotNull(savedSub, "Подазадча не найдена");
        assertEquals(newSub, savedSub, "Задачи не совпадают");
    }

    @Test
    void getEpicById_ReturnCorrectEpic_EpicInStorage() {
        final int id = taskManager.addNewEpic(epic);
        Epic savedEpic = taskManager.getEpic(id);
        assertNotNull(savedEpic, "Задача не найдена");
        assertEquals(epic, savedEpic, "Задачи не совпадают");
    }

    @Test
    void getAllTasks_ReturnCorrectAllTasks_TasksInStorage() {
        taskManager.addNewTask(task);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        assertNotNull(taskManager.getTasks(), "Задачи не возвращаются");
        assertEquals(tasks, taskManager.getTasks(), "Возвращается неверный список");
    }

    @Test
    void getAllSubtasks_ReturnCorrectAllSubtasks_SubtasksInStorage() {
        final int id = taskManager.addNewEpic(epic);
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id, 30, LocalDateTime.of(2024, 1, 7, 1, 1));
        taskManager.addNewSubtask(newSub);
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(newSub);
        assertNotNull(taskManager.getSubtasks(), "Задачи не возвращаются");
        assertEquals(subtasks, taskManager.getSubtasks(), "Возвращается неверный список");
    }

    @Test
    void getAllEpics_ReturnCorrectAllEpics_EpicsInStorage() {
        taskManager.addNewEpic(epic);
        List<Epic> epics = new ArrayList<>();
        epics.add(epic);
        assertNotNull(taskManager.getEpics(), "Задачи не возвращаются");
        assertEquals(epics, taskManager.getEpics(), "Возвращается неверный список");
    }

    @Test
    void getEpicSubtasks_ReturnCorrectEpicSubtasks_EpicSubtasksInStorage() {
        final int id = taskManager.addNewEpic(epic);
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id, 30, LocalDateTime.of(2024, 1, 8, 1, 1));
        final int subId = taskManager.addNewSubtask(newSub);
        List<Integer> subtasks = new ArrayList<>();
        subtasks.add(subId);
        assertNotNull(epic.getSubtaskId(), "Подазадачи не возвращаются");
        assertEquals(subtasks, epic.getSubtaskId(), "Задачи не возвращаются");
    }

    @Test
    void getEpicSubtasksWithoutSubs_ReturnCorrectEpic_EpicInStorage() {
        taskManager.addNewEpic(epic);
        assertEquals(0, epic.getSubtaskId().size(), "Список не пустой");
    }

    @Test
    void getPrioritizedTasks_ReturnCorrectPrioritizedTasks() {
        Task task1 = new Task("Name", "Description", NEW, 20, LocalDateTime.of(2024, 1, 9, 1, 1, 1));
        taskManager.addNewTask(task1);
        final int id = taskManager.addNewEpic(epic);
        Subtask sub1 = new Subtask("SubName", "SubDescription", NEW, id, 20, LocalDateTime.of(2024, 1, 10, 1, 1, 1));
        Subtask sub2 = new Subtask("Sub2Name", "Sub2Description", NEW, id, 30, LocalDateTime.of(2024, 1, 11, 1, 1, 1));
        taskManager.addNewSubtask(sub1);
        taskManager.addNewSubtask(sub2);

        ArrayList<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(task1);
        expectedTasks.add(sub1);
        expectedTasks.add(sub2);

        assertEquals(expectedTasks, taskManager.getPrioritizedTasks());
    }

    @Test
    void addNewTask_ReturnCorrectTask_TaskInStorage() {
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
    void addNewTaskIfNull_ReturnNull_TaskIsNotInStorage() {
        taskManager.addNewTask(null);
        assertEquals(0, taskManager.getTasks().size(), "Неверное количество задач");
    }

    @Test
    void addNewSubtask_ReturnCorrectSubtask_SubtaskInStorage() {
        taskManager.addNewEpic(epic);
        assertEquals(1, taskManager.getEpics().size(), "Важная задача отсутсвует");
        Subtask subtask = new Subtask("SubName", "SubDescription", NEW, epic.getId(), 30, LocalDateTime.of(2024, 1, 12, 1, 1));
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
    void addNewSubtaskIfNull_ReturnNull_SubtaskIsNotInStorage() {
        taskManager.addNewSubtask(null);
        assertEquals(0, taskManager.getSubtasks().size(), "Неверное количество подазадач");
    }

    @Test
    void addNewEpic_ReturnCorrectEpic_EpicInStorage() {
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
    void addNewEpicIfNull_ReturnNull_EpicIsNotInStorage() {
        taskManager.addNewEpic(null);
        assertEquals(0, taskManager.getEpics().size(), "Неверное количество задач");
    }

    @Test
    void updateTask_ReturnCorrectTask_TaskInStorage() {
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
    void updateTaskIfNull_ReturnNull_TaskIsNotInStorage() {
        taskManager.updateTask(null);
        assertEquals(0, taskManager.getTasks().size(), "Неверное количество задач");
    }

    @Test
    void updateSubtask_ReturnCorrectSubtask_SubtaskInStorage() {
        taskManager.addNewEpic(epic);
        assertEquals(1, taskManager.getEpics().size(), "Важная задача отсутсвует");
        Subtask subtask = new Subtask("SubName", "SubDescription", NEW, epic.getId(), 30, LocalDateTime.of(2024, 1, 13, 1, 1));
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
    void updateSubtaskIfNull_ReturnNull_SubtaskIsNotInStorage() {
        taskManager.updateSubtask(null);
        assertEquals(0, taskManager.getSubtasks().size(), "Неверное количество задач");
    }

    @Test
    void updateEpicWithNewAndDoneSubs() {
        final int id = taskManager.addNewEpic(epicWithNewAndDoneSub);
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id, 30, LocalDateTime.of(2024, 1, 14, 1, 1));
        Subtask doneSub = new Subtask("SubName", "SubDescription", DONE, id, 30, LocalDateTime.of(2024, 1, 15, 1, 1));
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
        Subtask doneSub = new Subtask("SubName", "SubDescription", DONE, id, 30, LocalDateTime.of(2024, 1, 16, 1, 1));
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
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id, 30, LocalDateTime.of(2024, 1, 17, 1, 1));
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
    void updateEpicIfNull_ReturnNull_EpicIsNotInStorage() {
        taskManager.updateEpic(null);
        assertEquals(0, taskManager.getEpics().size(), "Неверное количество задач");
    }

    @Test
    void deleteAllTasks_ReturnEmptyTaskList() {
        taskManager.addNewTask(task);
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTasks().size(), "Список задач не очистился");
    }

    @Test
    void deleteAllSubtasks_ReturnEmptySubtaskList() {
        final int id = taskManager.addNewEpic(epic);
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id, 30, LocalDateTime.of(2024, 1, 18, 1, 1));
        Subtask newSub2 = new Subtask("SubName", "SubDescription", NEW, id, 30, LocalDateTime.of(2024, 1, 19, 1, 1));
        taskManager.addNewSubtask(newSub);
        taskManager.addNewSubtask(newSub2);
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getSubtasks().size(), "Список подзадач не очистился");
    }

    @Test
    void deleteAllEpics_ReturnEmptyEpicList() {
        taskManager.addNewEpic(epic);
        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getEpics().size(), "Список задач не очистился");
    }

    @Test
    void deleteTaskById_DeleteOneTaskFromTaskList() {
        Task task2 = new Task("TaskName", "TaskDesceription", NEW, 30, LocalDateTime.of(2024, 1, 20, 1, 1));
        final int id = taskManager.addNewTask(task);
        taskManager.addNewTask(task2);
        Task removedTask = taskManager.getTask(id);
        assertNotNull(removedTask, "Задача не найдена");
        assertEquals(task, removedTask, "Задачи не совпадают");
        taskManager.deleteTask(id);
        assertEquals(1, taskManager.getTasks().size(), "Задача не удалилась из списка");
    }

    @Test
    void deleteSubtaskById_DeleteOneSubtaskFromSubtaskList() {
        final int id = taskManager.addNewEpic(epic);
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id, 30, LocalDateTime.of(2024, 1, 21, 1, 1));
        Subtask newSub2 = new Subtask("SubName", "SubDescription", NEW, id, 30, LocalDateTime.of(2024, 1, 22, 1, 1));
        taskManager.addNewSubtask(newSub);
        taskManager.addNewSubtask(newSub2);
        Subtask removedSub = taskManager.getSubtask(newSub2.getId());
        assertNotNull(removedSub, "Подзадача не найдена");
        assertEquals(newSub2, removedSub, "Задачи не совпадают");
        taskManager.deleteSubtask(newSub2.getId());
        assertEquals(1, taskManager.getSubtasks().size(), "Подзадача не удалилась из списка");
    }

    @Test
    void deleteEpicById_DeleteOneEpicFromEpicList() {
        final int id = taskManager.addNewEpic(epic);
        final int id2 = taskManager.addNewEpic(epicWithDoneSubs);
        Epic removedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(removedEpic, "Задача не найдена");
        assertEquals(epic, removedEpic, "Задачи не совпадают");
        taskManager.deleteEpic(id);
        assertEquals(1, taskManager.getEpics().size(), "Задача не удалилась из списка");
    }

    @Test
    public void shouldReturnCorrectStartTime() {
        taskManager.addNewEpic(testEpic);
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.NEW, 2, 1, 20, LocalDateTime.of(2024, 1, 23, 1, 1));
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.NEW, 3, 1, 30, LocalDateTime.of(2024, 1, 24, 1, 1));
        taskManager.addNewSubtask(sub2);
        assertNotNull(testEpic.getStartTime(), "Время начала не найдено");
        assertEquals(testEpic.getStartTime(), sub1.getStartTime(), "Время начала не совпадает");
    }

    @Test
    public void shouldReturnCorrectEndTime() {
        taskManager.addNewEpic(testEpic);
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.NEW, 2, 1, 20, LocalDateTime.of(2024, 1, 25, 1, 1));
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.NEW, 3, 1, 30, LocalDateTime.of(2024, 1, 26, 1, 1));
        taskManager.addNewSubtask(sub2);
        assertNotNull(testEpic.getEndTime(), "Время конца не найдено");
        assertEquals(testEpic.getEndTime(), sub2.getEndTime(), "Время конца не совпадает");
    }

    @Test
    public void shouldReturnNullStartTime() {
        taskManager.addNewEpic(testEpic);
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.NEW, 2, 1);
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.NEW, 3, 1);
        taskManager.addNewSubtask(sub2);
        assertNull(testEpic.getStartTime(), "Время должно быть null");
        assertEquals(testEpic.getStartTime(), sub1.getStartTime(), "Время не совпадает");
    }

    @Test
    public void shouldReturnNullEndTime() {
        taskManager.addNewEpic(testEpic);
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.NEW, 2, 1);
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.NEW, 3, 1);
        taskManager.addNewSubtask(sub2);
        assertNull(testEpic.getEndTime(), "Время должно быть null");
        assertEquals(testEpic.getEndTime(), sub2.getEndTime(), "Время не совпадает");
    }

    @Test
    public void shouldReturnCorrectDuration() {
        taskManager.addNewEpic(testEpic);
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.NEW, 2, 1, 20, LocalDateTime.of(2024, 2, 1, 1, 1));
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.NEW, 3, 1, 30, LocalDateTime.of(2024, 2, 2, 1, 1));
        taskManager.addNewSubtask(sub2);
        int expectedDuration = sub1.getDuration() + sub2.getDuration();
        int actualDuration = testEpic.getDuration();
        assertNotNull(actualDuration, "Длительность задачи не найдена");
        assertEquals(actualDuration, expectedDuration, "Длительности не совпадают");
    }

    @Test
    public void shouldReturnZeroDuration() {
        taskManager.addNewEpic(testEpic);
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.NEW, 2, 1);
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.NEW, 3, 1);
        taskManager.addNewSubtask(sub2);
        int actualDuration = testEpic.getDuration();
        assertEquals(actualDuration, 0, "Время должно быть null");
        assertEquals(actualDuration, sub1.getDuration(), "Время не совпадает");
    }

    @Test
    public void shouldReturnEpicWithoutSubs() {
        taskManager.addNewEpic(testEpic);
        int subsSize = testEpic.getSubtaskId().size();
        assertEquals(0, subsSize);
        assertEquals(TaskStatus.NEW, testEpic.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusNewWhenAllSubsNew() {
        taskManager.addNewEpic(testEpic);
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.NEW, 2, 1, 20, LocalDateTime.of(2024, 2, 5, 1, 1));
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.NEW, 3, 1, 20, LocalDateTime.of(2024, 2, 6, 1, 1));
        taskManager.addNewSubtask(sub2);
        assertEquals(TaskStatus.NEW, testEpic.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusDoneWhenAllSubsDone() {
        taskManager.addNewEpic(testEpic);
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.DONE, 2, 1, 20, LocalDateTime.of(2024, 2, 7, 1, 1));
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.DONE, 3, 1, 20, LocalDateTime.of(2024, 2, 8, 1, 1));
        taskManager.addNewSubtask(sub2);
        assertEquals(TaskStatus.DONE, testEpic.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusInProgressWhenSubsNewAndDone() {
        taskManager.addNewEpic(testEpic);
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.NEW, 2, 1, 20, LocalDateTime.of(2024, 2, 9, 1, 1));
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.DONE, 3, 1, 20, LocalDateTime.of(2024, 2, 10, 1, 1));
        taskManager.addNewSubtask(sub2);
        assertEquals(TaskStatus.IN_PROGRESS, testEpic.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusInProgressWhenSubsInProgress() {
        taskManager.addNewEpic(testEpic);
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.IN_PROGRESS, 2, 1, 20, LocalDateTime.of(2024, 1, 11, 1, 1));
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.IN_PROGRESS, 3, 1, 20, LocalDateTime.of(2024, 1, 12, 1, 1));
        taskManager.addNewSubtask(sub2);
        assertEquals(TaskStatus.IN_PROGRESS, testEpic.getStatus());
    }

    @Test
    public void shouldReturnPrioritizedTasksWithTwoTasks() {
        taskManager.addNewEpic(testEpic);
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.DONE, 2, 1, 20, LocalDateTime.of(2024, 2, 7, 1, 1));
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.DONE, 3, 1, 20, LocalDateTime.of(2024, 2, 7, 1, 1));
        taskManager.addNewSubtask(sub2);
        taskManager.addNewTask(task);

        assertEquals(taskManager.getPrioritizedTasks().size(), 2);
        assertEquals(taskManager.getSubtasks().size(), 1);
        assertEquals(taskManager.getTasks().size(), 1);
    }

    @Test
    public void shouldReturnPrioritizedTasksWithOnlyOneTask() {
        taskManager.addNewEpic(testEpic);
        Subtask sub1 = new Subtask("SubTest1", "SubTest1Description", TaskStatus.DONE, 2, 1, 20, LocalDateTime.of(2024, 2, 7, 1, 1));
        taskManager.addNewSubtask(sub1);
        Subtask sub2 = new Subtask("SubTest2", "SubTest2Description", TaskStatus.DONE, 3, 1, 20, LocalDateTime.of(2024, 2, 7, 1, 1));
        taskManager.addNewSubtask(sub2);
        Task task1 = new Task("taskName", "taskDescription", NEW, 30, LocalDateTime.of(2024, 2, 7, 1, 1));
        taskManager.addNewTask(task1);

        assertEquals(taskManager.getPrioritizedTasks().size(), 1);
        assertEquals(taskManager.getSubtasks().size(), 1);
        assertEquals(taskManager.getTasks().size(), 0);
    }

    @Test
    void shouldReturnHistoryWithOneTask() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    void shouldReturnHistoryWithoutTasks() {
        assertNotNull(historyManager.getHistory(),"История не пустая");
        assertEquals(0,historyManager.getHistory().size(),"История пустая");
    }

    @Test
    void shouldCorrectlyAddTaskIfOneDuplicatedTask() {
        historyManager.add(task1);
        historyManager.add(task1);
        assertNotNull(historyManager.getHistory(),"История пустая");
        assertEquals(1,historyManager.getHistory().size(),"Неверная история просмотра");
    }

    @Test
    void shouldCorrectlyAddTasksIfManyDuplicatedTasks() {
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task2);
        assertNotNull(historyManager.getHistory(),"История пустая");
        assertEquals(2,historyManager.getHistory().size(),"Неверная история просмотра");
    }

    @Test
    void shouldRemoveFirstTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());
        assertNotNull(historyManager.getHistory(),"История пустая");
        assertEquals(2,historyManager.getHistory().size(),"Неверная история просмотра");
        assertEquals(task2,historyManager.getHistory().get(1));
        assertEquals(task3,historyManager.getHistory().get(0));
    }

    @Test
    void shouldRemoveMidTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        assertNotNull(historyManager.getHistory(),"История пустая");
        assertEquals(2,historyManager.getHistory().size(),"Неверная история просмотра");
        assertEquals(task1,historyManager.getHistory().get(1));
        assertEquals(task3,historyManager.getHistory().get(0));
    }

    @Test
    void shouldRemoveLastTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());
        assertNotNull(historyManager.getHistory(),"История пустая");
        assertEquals(2,historyManager.getHistory().size(),"Неверная история просмотра");
        assertEquals(task1,historyManager.getHistory().get(1));
        assertEquals(task2,historyManager.getHistory().get(0));
    }

    @Test
    void shouldReturnCorrectHistoryWithThreeTasks() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = new ArrayList<>();
        history.add(task3);
        history.add(task2);
        history.add(task1);

        assertNotNull(historyManager.getHistory(),"История пустая");
        assertEquals(history,historyManager.getHistory(),"История не совпадает");
    }


}