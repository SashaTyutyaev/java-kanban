package manager;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import manager.tasks.TaskManager;
import manager.tasks.file.FileBackedTasksManager;
import manager.tasks.memory.InMemoryTaskManager;

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
        return FileBackedTasksManager.loadFromFile(new File("/Users/sashatyutyaev/dev/java-kanban/src/tasks.csv"));
    }
}
