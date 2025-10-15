package ua.lab.printqueue;

public class UserTask implements Runnable {
    private final String userName;
    private final SynchronizedPrintQueue syncQueue;
    private final UnsynchronizedPrintQueue unsyncQueue;
    private final boolean useSync;
    private final int fromId;
    private final int count;

    public UserTask(String userName, SynchronizedPrintQueue queue, int fromId, int count) {
        this.userName = userName;
        this.syncQueue = queue;
        this.unsyncQueue = null;
        this.useSync = true;
        this.fromId = fromId;
        this.count = count;
    }

    public UserTask(String userName, UnsynchronizedPrintQueue queue, int fromId, int count) {
        this.userName = userName;
        this.unsyncQueue = queue;
        this.syncQueue = null;
        this.useSync = false;
        this.fromId = fromId;
        this.count = count;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < count; i++) {
                Document d = new Document(fromId + i, userName + "-doc-" + (fromId + i));
                if (useSync) {
                    syncQueue.submit(d);
                } else {
                    unsyncQueue.submit(d);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.printf("%s encountered exception while submitting: %s%n", userName, e);
        }
    }
}
