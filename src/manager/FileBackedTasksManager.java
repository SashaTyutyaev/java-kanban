package manager;

import tasks.*;

import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {


    public File file;


    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public String toString(Task task) {
        if (task instanceof Subtask) {
            return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," +
                    task.getDescription() + "," + ((Subtask) task).getEpicId();
        } else {
            return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," +
                    task.getDescription() + ",";
        }
    }

    public Task fromString(String value) {
        if (value.isBlank()) {
            return null;
        }
        String[] elem = value.split(",");
        int id = Integer.parseInt(elem[0]);
        String type = elem[1];
        String name = elem[2];
        TaskStatus status = TaskStatus.valueOf(elem[3]);
        String description = elem[4];
        if (type.equals("SUBTASK")) {
            int epicId = Integer.parseInt(elem[5]);
            return new Subtask(name, description, status, id, epicId);
        } else {
            return new Task(name, description, status, id);
        }
    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask sub : getSubtasks()) {
                writer.write(toString(sub) + "\n");
            }
            writer.append("\n");
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка с файлом!");
        }
    }

    static String historyToString(HistoryManager manager) {
        List<String> historyList = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            historyList.add(String.valueOf(task.getId()));
        }
        return String.join(",", historyList);
    }

    public List<Integer> historyFromString(String value) {
        List<Integer> lines = new LinkedList<>();
        String[] elem = value.split(",");
        for (String line : elem) {
            lines.add(Integer.parseInt(line));
        }
        return lines;
    }

    public FileBackedTasksManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTasksManager fb = new FileBackedTasksManager(file);
        LinkedList<String> tasksFromFile = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                String line = br.readLine();
                tasksFromFile.add(line);
            }
            if (tasksFromFile.size() != 2) {
                tasksFromFile.removeFirst();
                if (this.tasks.size() > 2) {
                    for (int i = 0; i < this.tasks.size() - 2; i++) {
                        fromString(tasksFromFile.get(i));
                    }
                }
                if (!tasksFromFile.getLast().equals("")) {
                    List<Integer> history = historyFromString(tasksFromFile.getLast());
                    for (Integer id : history) {
                        if (tasks.containsKey(id)) {
                            historyManager.add(tasks.get(id));
                        } else if (subtasks.containsKey(id)){
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
        FileBackedTasksManager fb = new FileBackedTasksManager(new File("/Users/sashatyutyaev/dev/java-kanban/src/tasks.csv"));
        Task task1 = new Task("Task #1", "Task1 description", TaskStatus.NEW);
        Task task2 = new Task("Task #2", "Task2 description", TaskStatus.IN_PROGRESS);
        fb.addNewTask(task1);
        fb.getTask(task1.getId());
        fb.addNewTask(task2);
        fb.getTask(task2.getId());
        Epic epic1 = new Epic("Epic #1", "Epic1 description", TaskStatus.NEW);
        Epic epic2 = new Epic("Epic #1", "Epic1 description", TaskStatus.NEW);
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