package ua.lab.printqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class Main {
    public static void main(String[] args) throws Exception {
        final int users = 3;
        final int printers = 2;
        final int docsPerUser = 20;
        final int totalDocs = users * docsPerUser;

        System.out.println("--- DEMO 1: Unsynchronized (incorrect / may exhibit race conditions) ---");
        UnsynchronizedPrintQueue unsyncQueue = new UnsynchronizedPrintQueue();
        AtomicInteger unsyncPrintedCounter = new AtomicInteger(0);

        List<Thread> userThreads = new ArrayList<>();
        for (int i = 0; i < users; i++) {
            UserTask ut = new UserTask("User" + i, unsyncQueue, i * docsPerUser, docsPerUser);
            Thread t = new Thread(ut, "UserThread-" + i);
            userThreads.add(t);
            t.start();
        }

        List<Thread> printerThreads = new ArrayList<>();
        for (int i = 0; i < printers; i++) {
            Printer p = new Printer("Printer-U" + i, unsyncQueue, unsyncPrintedCounter, totalDocs);
            Thread t = new Thread(p, "Printer-U-" + i);
            printerThreads.add(t);
            t.start();
        }

        for (Thread t : userThreads) {
            t.join();
        }

        for (int i = 0; i < printers; i++) {
            unsyncQueue.submit(new Document(-1, "POISON" + i));
        }

        for (Thread t : printerThreads) {
            t.join(2000);
        }

        System.out.printf("Unsynchronized: submitted=%d, printed(approx)=%d\n", totalDocs, unsyncPrintedCounter.get());

        System.out.println("\n--- DEMO 2: Synchronized (correct; using wait/notify) ---");
        SynchronizedPrintQueue syncQueue = new SynchronizedPrintQueue(100);
        AtomicInteger syncPrintedCounter = new AtomicInteger(0);

        printerThreads.clear();
        for (int i = 0; i < printers; i++) {
            Printer p = new Printer("Printer-S" + i, syncQueue, syncPrintedCounter, totalDocs);
            Thread t = new Thread(p, "Printer-S-" + i);
            printerThreads.add(t);
            t.start();
        }

        userThreads.clear();
        for (int i = 0; i < users; i++) {
            UserTask ut = new UserTask("User" + i, syncQueue, i * docsPerUser, docsPerUser);
            Thread t = new Thread(ut, "UserThread-S-" + i);
            userThreads.add(t);
            t.start();
        }

        for (Thread t : userThreads) {
            t.join();
        }

        for (int i = 0; i < printers; i++) {
            syncQueue.submit(new Document(-1, "POISON" + i));
        }

        for (Thread t : printerThreads) {
            t.join();
        }

        System.out.printf("Synchronized: submitted=%d, printed=%d\n", totalDocs, syncPrintedCounter.get());
        System.out.println("Demo complete.");
    }
}
