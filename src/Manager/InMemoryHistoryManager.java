package Manager;

import Tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> tasksHistory = new ArrayList<>();


    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }

    @Override
    public void addTask(Task task) {
        if (tasksHistory.size() >= 10){
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }
}
