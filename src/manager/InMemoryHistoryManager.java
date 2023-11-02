package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> tasksHistory = new ArrayList<>();
    final int HISTORY_SIZE = 10;


    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }

    @Override
    public void addTask(Task task) {
        if (tasksHistory.size() >= HISTORY_SIZE){
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }
}
