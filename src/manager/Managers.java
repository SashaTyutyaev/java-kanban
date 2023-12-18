package manager;

import fileManager.FileBackedTasksManager;

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
        return new FileBackedTasksManager(Path.of("/Users/sashatyutyaev/dev/java-kanban/src/tasks.csv"));
    }
}
