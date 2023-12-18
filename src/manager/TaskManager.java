package manager;

import managersException.ManagerSaveException;
import tasks.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    Task getTask(int id) throws ManagerSaveException, IOException;

    Subtask getSubtask(int id) throws ManagerSaveException, IOException;

    Epic getEpic(int id) throws ManagerSaveException, IOException;

    int addNewTask(Task task) throws ManagerSaveException, IOException;

    int addNewEpic(Epic epic) throws ManagerSaveException, IOException;

    Integer addNewSubtask(Subtask subtask) throws ManagerSaveException, IOException;

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    List<Task> getHistory();

}
