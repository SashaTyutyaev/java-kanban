package main.manager;

import main.manager.history.HistoryManager;
import main.manager.history.InMemoryHistoryManager;
import main.manager.tasks.TaskManager;
import main.manager.tasks.file.FileBackedTasksManager;
import main.manager.tasks.memory.InMemoryTaskManager;

import java.io.File;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileManager() {
        return FileBackedTasksManager.loadFromFile(new File("/Users/sashatyutyaev/dev/java-kanban/src/testTasks.csv"));
    }
}
