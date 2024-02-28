package main.manager.tasks.file;

import main.converters.Converter;
import main.exceptions.ManagerSaveException;
import main.manager.tasks.memory.InMemoryTaskManager;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {


    private Path path;


    public FileBackedTasksManager(Path path) {
        this.path = path;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(path)))) {
            writer.append("id,type,name,status,description,epic,duration,startTime\n");
            for (Task task : getTasks()) {
                writer.write(Converter.taskToString(task) + "\n");
            }
            for (Subtask sub : getSubtasks()) {
                writer.write(Converter.taskToString(sub) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(Converter.taskToString(epic) + "\n");
            }
            writer.append("\n");
            writer.write(Converter.historyToString(historyManager.getHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка с файлом!");
        }
    }


    public static FileBackedTasksManager loadFromFile(File file) {
        final FileBackedTasksManager fb = new FileBackedTasksManager(file.toPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            boolean isHistory = false;
            br.readLine();
            int maxId = 0;
            while (br.ready()) {
                if (!isHistory) {
                    String line = br.readLine();
                    if (line.isEmpty()) {
                        isHistory = true;
                    } else {
                        switch (Converter.getTaskType(line)) {
                            case TASK:
                                Task task = Converter.taskFromString(line);
                                if (task.getId() > maxId) {
                                    maxId = task.getId();
                                }
                                tasks.put(task.getId(), task);
                                break;
                            case EPIC:
                                Epic epic = Converter.epicFromString(line);
                                if (epic.getId() > maxId) {
                                    maxId = epic.getId();
                                }
                                epics.put(epic.getId(), epic);
                                break;
                            case SUBTASK:
                                Subtask subtask = Converter.subFromString(line);
                                if (subtask.getId() > maxId) {
                                    maxId = subtask.getId();
                                }
                                subtasks.put(subtask.getId(), subtask);
                                break;
                        }
                    }
                } else {
                    String line = br.readLine();
                    List<Integer> history = Converter.historyFromString(line);
                    for (Integer id : history) {
                        if (tasks.containsKey(id)) {
                            historyManager.add(tasks.get(id));
                        } else if (subtasks.containsKey(id)) {
                            historyManager.add(subtasks.get(id));
                        } else if (epics.containsKey(id)) {
                            historyManager.add(epics.get(id));
                        }
                    }
                }
                fb.generatorId = maxId + 1;
            }
            for (Subtask sub : subtasks.values()) {
                epics.get(sub.getEpicId()).addSubtaskId(sub.getId());
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка с файлом!");
        }
        return fb;
    }


    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public Task getTask(int id) {
        super.getTask(id);
        save();
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        super.getSubtask(id);
        save();
        return subtasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        super.getEpic(id);
        save();
        return epics.get(id);
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
    }

    public static void main(String[] args) {
        final FileBackedTasksManager fb = new FileBackedTasksManager(Path.of("src/resources/tasks.csv"));
        Task task1 = new Task("Task #1", "Task1 description", TaskStatus.NEW, 30, LocalDateTime.of(2024, 2, 11, 10, 1, 1));
        Task task2 = new Task("Task #2", "Task2 description", TaskStatus.IN_PROGRESS, 30, LocalDateTime.of(2024, 2, 11, 11, 1, 1));
        Task task3 = new Task("Task #3", "Task3 description", TaskStatus.IN_PROGRESS, 30, LocalDateTime.of(2024, 2, 11, 12, 1, 1));
        fb.addNewTask(task1);
        fb.getTask(task1.getId());
        fb.addNewTask(task2);
        fb.getTask(task2.getId());
        fb.addNewTask(task3);
        fb.getTask(task3.getId());
        Epic epic1 = new Epic("Epic #1", "Epic1 description", TaskStatus.NEW);
        Epic epic2 = new Epic("Epic #2", "Epic2 description", TaskStatus.NEW);
        fb.addNewEpic(epic1);
        fb.getEpic(epic1.getId());
        fb.addNewEpic(epic2);
        fb.getEpic(epic2.getId());
        Subtask subtask1 = new Subtask("Subtask #1-1", "Subtask1 description", TaskStatus.NEW, epic1.getId(), 30, LocalDateTime.of(2024, 2, 11, 13, 1, 1));
        Subtask subtask2 = new Subtask("Subtask #2-1", "Subtask2 description", TaskStatus.NEW, epic2.getId(), 30, LocalDateTime.of(2024, 2, 11, 14, 1, 1));
        Subtask subtask3 = new Subtask("Subtask #3-1", "Subtask3 description", TaskStatus.NEW, epic1.getId(), 30, LocalDateTime.of(2024, 2, 11, 15, 1, 1));
        Subtask subtask4 = new Subtask("Subtask #4-1", "Subtask3 description", TaskStatus.NEW, epic2.getId(), 90, LocalDateTime.of(2024, 2, 11, 16, 1, 1));
        fb.addNewSubtask(subtask1);
        fb.getSubtask(subtask1.getId());
        fb.addNewSubtask(subtask2);
        fb.getSubtask(subtask2.getId());
        fb.addNewSubtask(subtask4);
        fb.getSubtask(subtask4.getId());
        FileBackedTasksManager fb2 = loadFromFile(new File("src/resources/tasks.csv"));
        Task task4 = new Task("Task #4", "Task4 description", TaskStatus.IN_PROGRESS, 30, LocalDateTime.of(2024, 3, 3, 1, 1, 1));
        Task task5 = new Task("Task #5", "Task5 description", TaskStatus.IN_PROGRESS, 30, LocalDateTime.of(2024, 2, 2, 1, 1, 1));
        fb2.addNewTask(task4);
        fb2.addNewTask(task5);
        fb2.addNewSubtask(subtask3);
        fb2.getTask(task4.getId());
        System.out.println(fb2.getTasks());
        System.out.println(fb2.getEpics());
        System.out.println(fb2.getSubtasks());
        System.out.println(fb2.getHistory());
    }

}