package main.converters;

import main.tasks.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Converter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String taskToString(Task task) {
        if (task.getStartTime() != null) {
            return task.getId() + "," + task.getEpicType() + "," + task.getName() + "," + task.getStatus() + "," +
                    task.getDescription() + "," + task.getDuration() + "," + task.getStartTime().format(formatter) + ",";
        } else {
            return task.getId() + "," + task.getEpicType() + "," + task.getName() + "," + task.getStatus() + "," +
                    task.getDescription() + ",";
        }
    }

    public static String taskToString(Epic epic) {
        if (epic.getStartTime() != null ) {
            return epic.getId() + "," + epic.getEpicType() + "," + epic.getName() + "," + epic.getStatus() + "," +
                    epic.getDescription() + "," + epic.getDuration() + "," + epic.getStartTime().format(formatter) + ",";
        }
        else {
            return epic.getId() + "," + epic.getEpicType() + "," + epic.getName() + "," + epic.getStatus() + "," +
                    epic.getDescription() + ",";
        }
    }

    public static String taskToString(Subtask subtask) {
        if (subtask.getStartTime() != null) {
            return subtask.getId() + "," + subtask.getEpicType() + "," + subtask.getName() + "," + subtask.getStatus() + "," +
                    subtask.getDescription() + "," + subtask.getEpicId() + "," + subtask.getDuration() + "," + subtask.getStartTime().format(formatter) + ",";
        } else {
            return subtask.getId() + "," + subtask.getEpicType() + "," + subtask.getName() + "," + subtask.getStatus() + "," +
                    subtask.getDescription() + "," + subtask.getEpicId() + ",";
        }
    }

    public static TaskType getTaskType(String value) {
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
        int duration = Integer.parseInt(elem[5]);
        LocalDateTime startTime = LocalDateTime.parse(elem[6],formatter);
        return new Task(name, description, status, id, duration, startTime);
    }

    public static Subtask subFromString(String value) {
        if (value.isBlank()) {
            return null;
        }
        String[] elem = value.split(",");
        if (elem.length >= 7) {
            int id = Integer.parseInt(elem[0]);
            String type = elem[1];
            String name = elem[2];
            TaskStatus status = TaskStatus.valueOf(elem[3]);
            String description = elem[4];
            int epicId = Integer.parseInt(elem[5]);
            int duration = Integer.parseInt(elem[6]);
            LocalDateTime startTime = LocalDateTime.parse(elem[7], formatter);
            return new Subtask(name, description, status, id, epicId, duration, startTime);
        } else {
            int id = Integer.parseInt(elem[0]);
            String type = elem[1];
            String name = elem[2];
            TaskStatus status = TaskStatus.valueOf(elem[3]);
            String description = elem[4];
            int epicId = Integer.parseInt(elem[5]);
            return new Subtask(name, description, status, id, epicId);
        }
    }

    public static Epic epicFromString(String value) {
        if (value.isBlank()) {
            return null;
        }
        String[] elem = value.split(",");
        if (elem.length >= 6) {
            int id = Integer.parseInt(elem[0]);
            String type = elem[1];
            String name = elem[2];
            TaskStatus status = TaskStatus.valueOf(elem[3]);
            String description = elem[4];
            int duration = Integer.parseInt(elem[5]);
            LocalDateTime startTime = LocalDateTime.parse(elem[6], formatter);
            return new Epic(name, description, status, id, duration, startTime);
        }
        else {
            int id = Integer.parseInt(elem[0]);
            String type = elem[1];
            String name = elem[2];
            TaskStatus status = TaskStatus.valueOf(elem[3]);
            String description = elem[4];
            return new Epic(name, description, status, id);
        }
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
