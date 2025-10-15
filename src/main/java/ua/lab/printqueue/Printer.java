package ua.lab.printqueue;

import java.util.concurrent.atomic.AtomicInteger;

public class Printer implements Runnable {
    private final String name;
    private final SynchronizedPrintQueue syncQueue;
    private final UnsynchronizedPrintQueue unsyncQueue;
    private final boolean useSync;
    private final AtomicInteger printedCounter;
    private final int totalToPrint;

    public Printer(String name, SynchronizedPrintQueue queue, AtomicInteger printedCounter, int totalToPrint) {
        this.name = name;
        this.syncQueue = queue;
        this.unsyncQueue = null;
        this.useSync = true;
        this.printedCounter = printedCounter;
        this.totalToPrint = totalToPrint;
    }

    public Printer(String name, UnsynchronizedPrintQueue queue, AtomicInteger printedCounter, int totalToPrint) {
        this.name = name;
        this.unsyncQueue = queue;
        this.syncQueue = null;
        this.useSync = false;
        this.printedCounter = printedCounter;
        this.totalToPrint = totalToPrint;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Document doc;
                if (useSync) {
                    doc = syncQueue.take();
                } else {
                    doc = unsyncQueue.take();
                }
                if (doc == null) {
                    Thread.sleep(10);
                    continue;
                }
                if (doc.getId() == -1) {
                    break;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                int printed = printedCounter.incrementAndGet();
                System.out.printf("[%s] Printed %s (total printed: %d)%n", name, doc, printed);
                if (totalToPrint > 0 && printed >= totalToPrint) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("%s interrupted and stopping.%n", name);
        } catch (Exception e) {
            System.out.printf("%s encountered exception: %s%n", name, e);
        }
    }
}
