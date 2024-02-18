package main.comparators;

import main.tasks.Task;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {
    @Override
    public int compare(Task task1, Task task2) {
        if (task1.getStartTime() == null) {
            return 1;
        }

        if (task2.getStartTime() == null) {
            return -1;
        }

        if (task2.getStartTime().equals(task1.getStartTime())) {
            return 0;
        }

        if (task2.getStartTime().isBefore(task1.getStartTime())) {
            return 1;
        } else {
            return -1;
        }
    }
}
