import Tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager implements ITaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId = 0;

    @Override
    public Task getTask(int id) {
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        return epics.get(id);
    }


    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskArrayList = new ArrayList<>(tasks.values());
        return taskArrayList;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicArrayList = new ArrayList<>(epics.values());
        return epicArrayList;
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>(subtasks.values());
        return subtaskArrayList;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        if (epicId > 0) {
            if (!subtasks.isEmpty()) {
                for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                    if (entry.getValue().getEpicId() == epicId) {
                        epicSubtasks.add(entry.getValue());
                    }
                }
            }
        }
        return epicSubtasks;
    }


    @Override
    public int addNewTask(Task task) {
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        final int id = ++generatorId;
        Epic epic = getEpic(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Такой важной задачи нет");
            return -1;
        }
        subtask.setId(id);
        epic.addSubtaskId(subtask.getId());

        subtasks.put(id, subtask);
        return id;
    }


    @Override
    public int addNewEpic(Epic epic) {
        final int id = ++generatorId;
        epic.setId(id);
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
            subtasks.put(subtask.getId(), subtask);
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.clear();
    }

    @Override
    public void deleteEpic(int id) {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            subtasks.remove(id);
            epics.get(epicId).removeSubtask(id);
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
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                entry.getValue().cleanSubtaskId();
                updateEpicStatus(entry.getValue().getId());
            }
        }
    }

    @Override
    public void updateEpicStatus(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            ArrayList<Integer> sub = epic.getSubtaskId();
            int newTask = 0;
            int inProgress = 0;
            int done = 0;
            if (!epics.isEmpty()) {
                for (int id : sub) {
                    if (subtasks.get(id).getStatus().equals("DONE")) {
                        done++;
                    }
                    if (subtasks.get(id).getStatus().equals("IN_PROGRESS")) {
                        inProgress++;
                    }
                    if (subtasks.get(id).getStatus().equals("NEW")) {
                        newTask++;
                    }
                }
                if (sub.isEmpty()) {
                    epic.setStatus("NEW");
                }
                if (newTask > 0 && inProgress == 0 && done == 0) {
                    epic.setStatus("NEW");
                    return;
                }
                if (inProgress > 0) {
                    epic.setStatus("IN_PROGRESS");
                    return;
                }
                if (done > 0 && inProgress == 0 && newTask == 0) {
                    epic.setStatus("DONE");
                }
            }
        }
    }
}



