package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    public CustomLinkedList customLinkedList = new CustomLinkedList();


    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTasks();
    }

    @Override
    public void add(Task task) {
        if (customLinkedList.nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        customLinkedList.linkLast(task);
    }

    @Override
    public void remove(int id) {
        customLinkedList.removeTask(id);

    }

}
