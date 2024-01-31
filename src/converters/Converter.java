package converters;

import tasks.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Converter {

    public static String taskToString(Task task) {
            return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," +
                    task.getDescription() + ",";
    }
    public static String taskToString(Epic epic) {
            return epic.getId() + "," + epic.getType() + "," + epic.getName() + "," + epic.getStatus() + "," +
                    epic.getDescription() + ",";
    }
    public static String taskToString(Subtask subtask) {
            return subtask.getId() + "," + subtask.getType() + "," + subtask.getName() + "," + subtask.getStatus() + "," +
                    subtask.getDescription() + "," + subtask.getEpicId();
    }

    public static TaskType getTaskType(String value){
        return TaskType.valueOf(value.split(",")[1]);
    }

    public static Task taskFromString(String value) {
        if (value.isBlank()) {
            return null;
        }
        String[] elem = value.split(",");
        int id = Integer.parseInt(elem[0]);
        String type = elem[1];
        String name = elem[2];
        TaskStatus status = TaskStatus.valueOf(elem[3]);
        String description = elem[4];
        return new Task(name, description, status, id);
    }

    public static Subtask subFromString(String value) {
        if (value.isBlank()) {
            return null;
        }
        String[] elem = value.split(",");
        int id = Integer.parseInt(elem[0]);
        String type = elem[1];
        String name = elem[2];
        TaskStatus status = TaskStatus.valueOf(elem[3]);
        String description = elem[4];
        int epicId = Integer.parseInt(elem[5]);
        return new Subtask(name, description, status, id, epicId);
    }

    public static Epic epicFromString(String value) {
        if (value.isBlank()) {
            return null;
        }
        String[] elem = value.split(",");
        int id = Integer.parseInt(elem[0]);
        String type = elem[1];
        String name = elem[2];
        TaskStatus status = TaskStatus.valueOf(elem[3]);
        String description = elem[4];
        return new Epic(name, description, status, id);
    }

    public static String historyToString(List<Task> taskList) {
        List<String> historyList = new ArrayList<>();
        for (Task task : taskList) {
            historyList.add(String.valueOf(task.getId()));
        }
        return String.join(",", historyList);
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> lines = new LinkedList<>();
        String[] elem = value.split(",");
        for (String line : elem) {
            lines.add(Integer.parseInt(line));
        }
        return lines;
    }
}
