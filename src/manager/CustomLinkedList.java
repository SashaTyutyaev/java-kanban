package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLinkedList {
    private static class Node<T> {
        T task;
        Node<T> prev;
        Node<T> next;

        public Node(T task, Node<T> prev, Node<T> next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

    }


    private Node<Task> tail;
    private Node<Task> head;

    public Map<Integer, Node<Task>> nodeMap = new HashMap<>();


    public void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newTail = new Node<>(task, oldTail, null);
        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }
        nodeMap.put(task.getId(), newTail);
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> node = tail;
        while (node != null) {
            tasks.add(node.task);
            node = node.prev;
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node.prev == null) {
            head = node.next;
            if (node.next == null) {
                tail = null;
                return;
            }
            node.next.prev = null;
            return;
        }
        node.prev.next = node.next;

        if (node == tail) {
            tail = node.prev;
            return;
        }
        node.next.prev = node.prev;
    }

    public void removeTask(Integer key) {
        this.removeNode(nodeMap.get(key));
        this.nodeMap.remove(key);
    }

}

