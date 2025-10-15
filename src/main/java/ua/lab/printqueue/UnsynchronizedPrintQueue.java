package ua.lab.printqueue;

import java.util.LinkedList;
import java.util.Queue;

public class UnsynchronizedPrintQueue {
    private final Queue<Document> queue = new LinkedList<>();

    public void submit(Document doc) {
        queue.add(doc);
    }

    public Document take() {
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }
}
