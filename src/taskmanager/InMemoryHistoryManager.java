package taskmanager;

import tasks.Task;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    Node<Task> head;
    Node<Task> tail;
    private int size = 0;
    private final HashMap<Integer, Node<Task>> tasksHashMap = new HashMap<>();

    private void linkLast(Task task) {
        if (size == 0) {
            Node<Task> node = new Node<>(task, null, null);
            head = node;
            tail = node;
            tasksHashMap.put(task.getId(), node);
            size++;
        } else if (size > 0) {
            Node<Task> node = new Node<>(task, tail, null);
            tail.next = node;
            tail = node;
            tasksHashMap.put(task.getId(), node);
            size++;
        }
    }

    @Override
    public void add(Task task) {
        tasksHashMap.remove(task.getId());
        linkLast(task);
    }

    private void removeNode(Node<Task> node) {
        if (node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = node.next;
            head.prev = null;
        } else if (node == tail) {
            tail = node.prev;
            tail.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    @Override
    public void remove(int id) {
        Node<Task> node = tasksHashMap.get(id);
        if (node != null) {
            removeNode(node);
            tasksHashMap.remove(id);
            size--;
        }
    }

    private List<Task> getTasks() {
        ArrayList<Task> listForReturn = new ArrayList<>();
        Node<Task> currentNode = head;
        for (int i = 0; i < size; i++) {
            listForReturn.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return listForReturn;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

}

class Node<T> {
    T task;
    Node<T> prev;
    Node<T> next;

    Node(T task, Node<T> prev, Node<T> next) {
        this.task = task;
        this.prev = prev;
        this.next = next;
    }
}