package main.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.adapters.LocalDateTimeAdapter;
import main.manager.history.HistoryManager;
import main.manager.history.InMemoryHistoryManager;
import main.manager.tasks.TaskManager;
import main.manager.tasks.file.FileBackedTasksManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager(URI.create("http://localhost:5000"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileManager() {
        return FileBackedTasksManager.loadFromFile(new File("src/resources/testTasks.csv"));
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}
