package main.tests;

import main.manager.history.InMemoryHistoryManager;
import main.tasks.Task;
import main.tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void add() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    void addEmptyHistory() {
        assertNotNull(historyManager.getHistory(),"История пустая");
        assertEquals(0,historyManager.getHistory().size(),"История пустая");
    }

    @Test
    void addDuplicatedTasks() {
        historyManager.add(task1);
        historyManager.add(task1);
        assertNotNull(historyManager.getHistory(),"История пустая");
        assertEquals(1,historyManager.getHistory().size(),"Неверная история просмотра");
    }

    @Test
    void addManyDuplicatedTasks() {
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task2);
        assertNotNull(historyManager.getHistory(),"История пустая");
        assertEquals(2,historyManager.getHistory().size(),"Неверная история просмотра");
    }

    @Test
    void removeFirstTask() {
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
    void removeMidTask() {
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
    void removeLastTask() {
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
    void getHistory() {
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

    @Test
    void getEmptyHistory() {
        assertNotNull(historyManager.getHistory());
        assertEquals(0,historyManager.getHistory().size(),"Неверная история просмотра");
    }


}
