package tasks;

import manager.tasks.memory.InMemoryTaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subtaskId = new ArrayList<>();
    private TaskType type = TaskType.EPIC;
    private LocalDateTime endTime;
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Override
    public String getType() {
        return type.toString();
    }
    

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Epic(String name, String description, TaskStatus status, int id) {
        super(name, description, status, id);
    }

    public void getEpicTime() {
        endTime = taskManager.getSubtasks().get(subtaskId.get(0)).getEndTime();
        startTime = taskManager.getSubtasks().get(subtaskId.get(0)).getStartTime();

        for (Subtask sub : taskManager.getSubtasks()) {
            if (subtaskId.contains(sub.getId())) {
                if (sub.getEndTime() == null || sub.getStartTime() == null) {
                    continue;
                }
                if (sub.getEndTime().isAfter(endTime)) {
                    endTime = sub.getEndTime();
                }
                if (sub.getStartTime().isBefore(startTime)) {
                    startTime = sub.getStartTime();
                }
                duration += sub.getDuration();
            }
        }
    }


    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }


    public void addSubtaskId(int id) {
        subtaskId.add(id);
    }


    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }


    public void cleanSubtaskId() {
        subtaskId.clear();
    }

    public void removeSubtask(int id) {
        subtaskId.remove(Integer.valueOf(id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskId, epic.subtaskId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskId=" + subtaskId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
