package Tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subtaskId = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status, int id) {
        super(name, description, status, id);
    }

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
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
