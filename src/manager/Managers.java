package manager;

import manager.tasks.fileManager.FileBackedTasksManager;
import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import manager.tasks.memoryManager.InMemoryTaskManager;
import manager.tasks.TaskManager;

import java.io.File;
import java.nio.file.Path;

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
