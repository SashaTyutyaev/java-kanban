package manager.tasks.memoryManager;

import exceptions.ManagerSaveException;
import manager.Managers;
import manager.history.HistoryManager;
import manager.tasks.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static tasks.TaskStatus.*;

public class InMemoryTaskManager implements TaskManager {
    protected static final HashMap<Integer, Task> tasks = new HashMap<>();
    protected static final HashMap<Integer, Epic> epics = new HashMap<>();
    protected static final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected static final HistoryManager historyManager = Managers.getDefaultHistory();
    private int generatorId = 0;

    public int getNewIdentificator() {
        return ++generatorId;
    }

    public void updateIdentificator(int ID){
        generatorId = ID;
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }


    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        if (!epics.containsKey(epicId)) {
            return new ArrayList<>();
        }
        Epic epic = epics.get(epicId);
        ArrayList<Integer> sub = epic.getSubtaskId();
        for (int id : sub) {
            if (subtasks.get(id).getEpicId() == epicId) {
                epicSubtasks.add(subtasks.get(id));
            }
        }
        return epicSubtasks;
    }


    @Override
    public int addNewTask(Task task) {
        final int id = getNewIdentificator();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        final int id = getNewIdentificator();
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Такой важной задачи нет");
            return -1;
        }
        subtask.setId(id);
        epic.addSubtaskId(subtask.getId());
        subtasks.put(id, subtask);
        updateEpicStatus(subtask.getEpicId());
        return id;

    }


    @Override
    public int addNewEpic(Epic epic) {
        final int id = getNewIdentificator();
        epic.setId(id);
        updateEpicStatus(epic.getId());
        epics.put(id, epic);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic.getId());
        }

    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        if (subtasks.containsKey(subtask.getId())) {
            int epicId = subtasks.get(subtask.getId()).getEpicId();
            if (((Integer) subtask.getEpicId()).equals(epicId)) {
                subtasks.put(subtask.getId(), subtask);
                updateEpicStatus(subtask.getEpicId());
            }
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        ArrayList<Integer> sub = epics.get(id).getSubtaskId();
        for (int i : sub) {
            subtasks.remove(i);
            historyManager.remove(i);
        }
        epics.remove(id);
        historyManager.remove(id);
    }


    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            subtasks.remove(id);
            epics.get(epicId).removeSubtask(id);
            historyManager.remove(id);
            updateEpicStatus(epicId);
        }


    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                epic.cleanSubtaskId();
                updateEpicStatus(epic.getId());
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void updateEpicStatus(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            ArrayList<Integer> sub = epic.getSubtaskId();
            if (sub.isEmpty()) {
                epic.setStatus(NEW);
                return;
            }
            int newTask = 0;
            int inProgress = 0;
            int done = 0;
            for (int id : sub) {
                if (subtasks.get(id).getStatus().equals(DONE)) {
                    done++;
                } else if (subtasks.get(id).getStatus().equals(IN_PROGRESS)) {
                    inProgress++;
                } else if (subtasks.get(id).getStatus().equals(NEW)) {
                    newTask++;
                }
            }
            if (newTask > 0 && inProgress == 0 && done == 0) {
                epic.setStatus(NEW);
                return;
            }
            if (inProgress > 0) {
                epic.setStatus(IN_PROGRESS);
                return;
            }
            if (done > 0 && inProgress == 0 && newTask == 0) {
                epic.setStatus(DONE);
            }
        }
    }
}




