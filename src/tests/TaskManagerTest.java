package tests;

import manager.tasks.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tasks.TaskStatus.NEW;

abstract class TaskManagerTest<T extends TaskManager> {

    public T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtaskWithNewStatus;

    @BeforeEach
    void beforeEach() {
        task = new Task("taskName", "taskDescription", NEW);
        epic = new Epic("epicName", "epicDescription", NEW);
        subtaskWithNewStatus = new Subtask("SubName", "SubDescription", NEW, epic.getId());
    }

    @Test
    void getTaskById() {
        taskManager.addNewTask(task);
        Task savedTask = taskManager.getTask(task.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
    }

    @Test
    void getSubtaskById() {
        final int id = taskManager.addNewEpic(epic);
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id);
        final int subId = taskManager.addNewSubtask(newSub);
        Subtask savedSub = taskManager.getSubtask(subId);
        assertNotNull(savedSub, "Подазадча не найдена");
        assertEquals(newSub, savedSub, "Задачи не совпадают");
    }

    @Test
    void getEpicById() {
        final int id = taskManager.addNewEpic(epic);
        Epic savedEpic = taskManager.getEpic(id);
        assertNotNull(savedEpic, "Задача не найдена");
        assertEquals(epic, savedEpic, "Задачи не совпадают");
    }

    @Test
    void getAllTasks() {
        taskManager.addNewTask(task);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        assertNotNull(taskManager.getTasks(), "Задачи не возвращаются");
        assertEquals(tasks, taskManager.getTasks(), "Возвращается неверный список");
    }

    @Test
    void getAllSubtasks() {
        final int id = taskManager.addNewEpic(epic);
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id);
        taskManager.addNewSubtask(newSub);
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(newSub);
        assertNotNull(taskManager.getSubtasks(), "Задачи не возвращаются");
        assertEquals(subtasks, taskManager.getSubtasks(), "Возвращается неверный список");
    }

    @Test
    void getAllEpics() {
        taskManager.addNewEpic(epic);
        List<Epic> epics = new ArrayList<>();
        epics.add(epic);
        assertNotNull(taskManager.getEpics(), "Задачи не возвращаются");
        assertEquals(epics, taskManager.getEpics(), "Возвращается неверный список");
    }

    @Test
    void getEpicSubtasks() {
        final int id = taskManager.addNewEpic(epic);
        Subtask newSub = new Subtask("SubName", "SubDescription", NEW, id);
        final int subId = taskManager.addNewSubtask(newSub);
        List<Integer> subtasks = new ArrayList<>();
        subtasks.add(subId);
        assertNotNull(epic.getSubtaskId(),"Подазадачи не возвращаются");
        assertEquals(subtasks,epic.getSubtaskId(),"Задачи не возвращаются");
    }

    @Test
    void getEpicSubtasksWithoutSubs() {
        taskManager.addNewEpic(epic);
        assertEquals(0,epic.getSubtaskId().size(),"Список не пустой");
    }


}