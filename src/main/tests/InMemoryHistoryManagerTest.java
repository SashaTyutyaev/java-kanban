package main.tests;

import main.manager.history.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.tasks.Task;
import main.tasks.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("task1Name","task1Description", TaskStatus.NEW,1);
        task2 = new Task("task2Name","task2Description", TaskStatus.NEW,2);
        task3 = new Task("task3Name","task3Description", TaskStatus.NEW,3);
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