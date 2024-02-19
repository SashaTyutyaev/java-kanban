package main.manager.tasks.memory;

import main.comparators.TaskComparator;
import main.exceptions.ValidationException;
import main.manager.Managers;
import main.manager.history.HistoryManager;
import main.manager.tasks.TaskManager;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

import static main.tasks.TaskStatus.*;

public class InMemoryTaskManager implements TaskManager {
    protected static final HashMap<Integer, Task> tasks = new HashMap<>();
    protected static final HashMap<Integer, Epic> epics = new HashMap<>();
    protected static final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected static final HistoryManager historyManager = Managers.getDefaultHistory();
    protected static TreeSet<Task> taskTreeSet = new TreeSet<>(new TaskComparator());
    protected int generatorId = 0;

    protected int getNewIdentificator() {
        return ++generatorId;
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
        if (task != null) {
            int id = getNewIdentificator();
            task.setId(id);
            validateTimeInterception(task);
            tasks.put(id, task);
            taskTreeSet.add(task);
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        if (subtask != null) {
            int id = getNewIdentificator();
            Epic epic = epics.get(subtask.getEpicId());
            if (epic == null) {
                System.out.println("Такой важной задачи нет");
                return -1;
            }
            subtask.setId(id);
            validateTimeInterception(subtask);
            epic.addSubtaskId(subtask.getId());
            subtasks.put(id, subtask);
            updateEpicTime(epic);
            updateEpicStatus(subtask.getEpicId());
            taskTreeSet.add(subtask);
            return id;
        } else {
            return -1;
        }
    }


    @Override
    public int addNewEpic(Epic epic) {
        if (epic != null) {
            int id = getNewIdentificator();
            epic.setId(id);
            updateEpicStatus(epic.getId());
            epics.put(id, epic);
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        if (tasks.containsKey(task.getId())) {
            validateTimeInterception(task);
            tasks.put(task.getId(), task);
            taskTreeSet.add(task);
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
                validateTimeInterception(subtask);
                subtasks.put(subtask.getId(), subtask);
                taskTreeSet.add(subtask);
                updateEpicTime(epics.get(subtask.getEpicId()));
                updateEpicStatus(subtask.getEpicId());
            }
        }
    }

    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
        taskTreeSet.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        ArrayList<Integer> sub = epics.get(id).getSubtaskId();
        for (int i : sub) {
            subtasks.remove(i);
            historyManager.remove(i);
            taskTreeSet.remove(subtasks.get(i));
        }
        historyManager.remove(id);
        taskTreeSet.remove(epics.get(id));
        epics.remove(id);
    }


    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            historyManager.remove(id);
            taskTreeSet.remove(subtasks.get(id));
            subtasks.remove(id);
            epics.get(epicId).removeSubtask(id);
            updateEpicStatus(epicId);
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        taskTreeSet.removeIf(task -> task instanceof Task);
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
        taskTreeSet.removeIf(task -> task instanceof Epic);
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
        taskTreeSet.removeIf(task -> task instanceof Subtask);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicId) {
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
                return;
            }
            if (done > 0) {
                epic.setStatus(IN_PROGRESS);
            }
        }
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(taskTreeSet);
    }

    private void updateEpicTime(Epic epic) {
        if (epic.getSubtaskId().size() >= 1) {
            LocalDateTime subEndTime = getEpicSubtasks(epic.getId()).get(0).getEndTime();
            LocalDateTime subStartTime = getEpicSubtasks(epic.getId()).get(0).getStartTime();
            int duration = 0;

            for (Subtask sub : getEpicSubtasks(epic.getId())) {
                if (sub.getEndTime() == null || sub.getStartTime() == null) {
                    continue;
                }
                if (sub.getEndTime().isAfter(subEndTime)) {
                    subEndTime = sub.getEndTime();
                }
                if (sub.getStartTime().isBefore(subStartTime)) {
                    subStartTime = sub.getStartTime();
                }
                duration += sub.getDuration();
            }
            epic.setDuration(duration);
            epic.setStartTime(subStartTime);
            epic.setEndTime(subEndTime);
        }
    }

    private void validateTimeInterception(Task task) {
        for (Task expTask : taskTreeSet) {
            if (Objects.equals(task.getId(), expTask.getId())) {
                continue;
            }
            if (expTask.getStartTime() == null || task.getStartTime() == null) {
                break;
            }
            if (task.getEndTime().isBefore(expTask.getStartTime()) || task.getEndTime().equals(expTask.getStartTime())
                    || task.getStartTime().isAfter(expTask.getEndTime()) || task.getStartTime().equals(expTask.getEndTime())) {
            } else {
                System.out.println("Задача не добавлена");
            }
        }
    }
}