package manager.tasks.fileManager;

import elseFunctions.Converter;
import exceptions.ManagerSaveException;
import manager.Managers;
import manager.tasks.memoryManager.InMemoryTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {


    private Path path;


    private FileBackedTasksManager(Path path) {
        super();
        this.path = path;
    }

    private void save() {
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
        final FileBackedTasksManager fb = new FileBackedTasksManager(Path.of("/Users/sashatyutyaev/dev/java-kanban/src/tasks.csv"));
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String fileLine = Files.readString(Path.of("/Users/sashatyutyaev/dev/java-kanban/src/tasks.csv"));
            String fileLines[] = fileLine.split("\n");
            int taskId = Converter.taskFromString(fileLines[fileLines.length - 3]).getId();
            boolean isHistory = false;
            int initialID = taskId + 1;
            br.readLine();
            if (!isHistory) {
                String line = br.readLine();
                if (Converter.taskFromString(line).getId() > initialID) {
                    initialID = Converter.taskFromString(line).getId();
                    fb.updateIdentificator(initialID);
                }
                switch (Converter.getTaskType(Converter.taskFromString(line))) {
                    case "TASK":
                        tasks.put(Converter.taskFromString(line).getId(), Converter.taskFromString(line));
                        break;
                    case "EPIC":
                        epics.put(Converter.epicFromString(line).getId(), Converter.epicFromString(line));
                        break;
                    case "SUBTASK":
                        subtasks.put(Converter.subFromString(line).getId(), Converter.subFromString(line));
                }
                if (line.isEmpty()) {
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
    public int addNewTask(Task task) {
        super.addNewTask(task);
        save();
        return task.getId();
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
        return epic.getId();
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
        FileBackedTasksManager fb = loadFromFile(new File("/Users/sashatyutyaev/dev/java-kanban/src/tasks.csv"));
        Task task1 = new Task("Task #1", "Task1 description", TaskStatus.NEW);
        Task task2 = new Task("Task #2", "Task2 description", TaskStatus.IN_PROGRESS);
        Task task3 = new Task("Task #3", "Task3 description", TaskStatus.IN_PROGRESS);
        Task task4 = new Task("Task #4", "Task4 description", TaskStatus.IN_PROGRESS);
        Task task5 = new Task("Task #5", "Task5 description", TaskStatus.IN_PROGRESS);
        fb.addNewTask(task1);
        fb.getTask(task1.getId());
        fb.addNewTask(task2);
        fb.getTask(task2.getId());
        fb.addNewTask(task3);
        fb.getTask(task3.getId());
        Epic epic1 = new Epic("Epic #1", "Epic1 description", TaskStatus.NEW);
        Epic epic2 = new Epic("Epic #2", "Epic2 description", TaskStatus.NEW);
        Epic epic3 = new Epic("Epic #3", "Epic3 description", TaskStatus.NEW);
        fb.addNewEpic(epic1);
        fb.getEpic(epic1.getId());
        fb.addNewEpic(epic2);
        fb.getEpic(epic2.getId());
        Subtask subtask1 = new Subtask("Subtask #1-1", "Subtask1 description", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask #2-1", "Subtask2 description", TaskStatus.NEW, epic2.getId());
        Subtask subtask3 = new Subtask("Subtask #3-1", "Subtask3 description", TaskStatus.NEW, epic2.getId());
        fb.addNewSubtask(subtask1);
        fb.getSubtask(subtask1.getId());
        fb.addNewSubtask(subtask2);
        fb.getSubtask(subtask2.getId());
        FileBackedTasksManager fb2 = loadFromFile(new File("/Users/sashatyutyaev/dev/java-kanban/src/tasks.csv"));
        fb2.addNewTask(task4);
        fb2.addNewTask(task5);
        fb2.addNewEpic(epic3);
        fb2.addNewSubtask(subtask3);
        System.out.println(fb2.getTasks());
        System.out.println(fb2.getEpics());
        System.out.println(fb2.getSubtasks());
        System.out.println(fb2.getHistory());
    }

}