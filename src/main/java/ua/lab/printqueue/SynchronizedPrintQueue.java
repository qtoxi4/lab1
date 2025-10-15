package ua.lab.printqueue;

import java.util.LinkedList;
import java.util.Queue;

public class SynchronizedPrintQueue {
    private final Queue<Document> queue = new LinkedList<>();
    private final int capacity;

    public SynchronizedPrintQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        this.capacity = capacity;
    }

    public SynchronizedPrintQueue() {
        this(Integer.MAX_VALUE);
    }

    public synchronized void submit(Document doc) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait();
        }
        queue.add(doc);
        notifyAll();
    }

    public synchronized Document take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        Document d = queue.poll();
        notifyAll();
        return d;
    }

    public synchronized int size() {
        return queue.size();
    }
}
