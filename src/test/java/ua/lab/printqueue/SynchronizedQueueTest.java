package ua.lab.printqueue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SynchronizedQueueTest {
    @Test
    public void testAllDocumentsPrintedWithSynchronizedQueue() throws Exception {
        final int users = 4;
        final int printers = 3;
        final int docsPerUser = 25;
        final int totalDocs = users * docsPerUser;

        SynchronizedPrintQueue queue = new SynchronizedPrintQueue(100);
        AtomicInteger printedCounter = new AtomicInteger(0);

        List<Thread> printersList = new ArrayList<>();
        for (int i = 0; i < printers; i++) {
            Printer p = new Printer("PrinterTest-" + i, queue, printedCounter, totalDocs);
            Thread t = new Thread(p);
            printersList.add(t);
            t.start();
        }

        List<Thread> userThreads = new ArrayList<>();
        for (int i = 0; i < users; i++) {
            UserTask ut = new UserTask("UserTest" + i, queue, i * docsPerUser, docsPerUser);
            Thread t = new Thread(ut);
            userThreads.add(t);
            t.start();
        }

        for (Thread t : userThreads) {
            t.join();
        }

        for (int i = 0; i < printers; i++) {
            queue.submit(new Document(-1, "POISON" + i));
        }

        for (Thread t : printersList) {
            t.join();
        }

        assertEquals(totalDocs, printedCounter.get(), "All documents must be printed in synchronized mode");
    }
}
