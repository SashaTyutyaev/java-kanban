package fileManager;

import manager.InMemoryTaskManager;
import managersException.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {


    private static Path path;


    public FileBackedTasksManager(Path path) {
        super();
        this.path = path;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(path)))) {
            writer.append("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                writer.write(Converter.taskToString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(Converter.taskToString(epic) + "\n");
            }
            for (Subtask sub : getSubtasks()) {
                writer.write(Converter.taskToString(sub) + "\n");
            }
            writer.append("\n");
            writer.write(Converter.historyToString(historyManager.getHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка с файлом!");
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        boolean isHistory = false;
        FileBackedTasksManager fb = new FileBackedTasksManager(path);
        LinkedList<String> tasksFromFile = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            if (!isHistory) {
                String line = br.readLine();
                if (Converter.taskFromString(line).getType().equals("TASK")) {
                    tasks.put(Converter.taskFromString(line).getId(), Converter.taskFromString(line));
                } else if (Converter.taskFromString(line).getType().equals("EPIC")) {
                    epics.put(Converter.epicFromString(line).getId(), Converter.epicFromString(line));
                } else if (Converter.taskFromString(line).getType().equals("SUBTASK")) {
                    subtasks.put(Converter.subFromString(line).getId(), Converter.subFromString(line));
                }
                if (line == null) {
                    br.readLine();
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
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка с файлом!");
        }
        return fb;
    }


    @Override
    public int addNewTask(Task task) throws ManagerSaveException, IOException {
        super.addNewTask(task);
        save();
        return task.getId();
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) throws ManagerSaveException, IOException {
        super.addNewSubtask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public int addNewEpic(Epic epic) throws ManagerSaveException, IOException {
        super.addNewEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public Task getTask(int id) throws ManagerSaveException, IOException {
        historyManager.add(tasks.get(id));
        save();
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id) throws ManagerSaveException, IOException {
        historyManager.add(subtasks.get(id));
        save();
        return subtasks.get(id);
    }

    @Override
    public Epic getEpic(int id) throws ManagerSaveException, IOException {
        historyManager.add(epics.get(id));
        save();
        return epics.get(id);
    }


    public static void main(String[] args) throws ManagerSaveException, IOException {
        FileBackedTasksManager fb = new FileBackedTasksManager(Path.of("/Users/sashatyutyaev/dev/java-kanban/src/tasks.csv"));
        Task task1 = new Task("Task #1", "Task1 description", TaskStatus.NEW);
        Task task2 = new Task("Task #2", "Task2 description", TaskStatus.IN_PROGRESS);
        fb.addNewTask(task1);
        fb.getTask(task1.getId());
        fb.addNewTask(task2);
        fb.getTask(task2.getId());
        Epic epic1 = new Epic("Epic #1", "Epic1 description", TaskStatus.NEW);
        Epic epic2 = new Epic("Epic #2", "Epic2 description", TaskStatus.NEW);
        fb.addNewEpic(epic1);
        fb.getEpic(epic1.getId());
        fb.addNewEpic(epic2);
        fb.getEpic(epic2.getId());
        Subtask subtask1 = new Subtask("Subtask #1-1", "Subtask1 description", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask #2-1", "Subtask2 description", TaskStatus.NEW, epic2.getId());
        fb.addNewSubtask(subtask1);
        fb.getSubtask(subtask1.getId());
        fb.addNewSubtask(subtask2);
        fb.loadFromFile(new File("/Users/sashatyutyaev/dev/java-kanban/src/tasks.csv"));
        System.out.println(fb.getTasks());
        System.out.println(fb.getSubtasks());
        System.out.println(fb.getEpics());
        System.out.println(fb.getHistory());
    }

}