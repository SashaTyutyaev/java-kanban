package main.manager.history;

import main.tasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private CustomLinkedList customLinkedList = new CustomLinkedList();


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
