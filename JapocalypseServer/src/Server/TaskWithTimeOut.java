package Server;

import java.util.concurrent.*;

public class TaskWithTimeOut {

    private TaskWithTimeOut() {

    }

    public static <T> T executeTask(Callable<T> task, long timeoutValue, TimeUnit timeUnit) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(task);
        T result;
        try {
            result = future.get(timeoutValue, timeUnit);
        } catch (Exception e) {
            future.cancel(true);
            result = null;
        }
        executor.shutdownNow();
        return result;
    }
}