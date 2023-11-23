package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    public final CustomLinkedList customLinkedList = new CustomLinkedList();

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


    private static class CustomLinkedList {
        private Node<Task> tail;
        private Node<Task> head;

        private Map<Integer, Node<Task>> nodeMap = new HashMap<>();


        private void linkLast(Task task) {
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

        private List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            Node<Task> node = head;
            while (node.next != null) {
                tasks.add(node.task);
                node = node.next;
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

        private void removeTask(Integer key) {
            this.removeNode(nodeMap.get(key));
            this.nodeMap.remove(key);
        }

        public Map<Integer, Node<Task>> getNodes() {
            return nodeMap;
        }

    }


    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTasks();
    }

    @Override
    public void add(Task task) {
        if (customLinkedList.getNodes().containsKey(task.getId())) {
            remove(task.getId());
        }
        customLinkedList.linkLast(task);
    }

    @Override
    public void remove(int id) {
        customLinkedList.removeTask(id);

    }
}
