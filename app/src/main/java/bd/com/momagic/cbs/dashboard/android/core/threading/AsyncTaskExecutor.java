package bd.com.momagic.cbs.dashboard.android.core.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bd.com.momagic.cbs.dashboard.android.core.utilities.MiscellaneousUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.ObjectUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.ThreadUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

final class AsyncTaskExecutor {

    private static final int EXECUTOR_SERVICE_TERMINATION_WAIT_TIMEOUT_IN_MILLISECONDS = 20;
    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskExecutor.class);
    private static final ExecutorService executorService
            = new ForkJoinPool(MiscellaneousUtilities.getAvailableProcessors());

    /**
     * This method submits a task to the executor service
     * in a thread-safe manner.
     * @param task Task to execute.
     * @return An AsyncTask object.
     * @param <Type> Asynchronous task result type.
     */
    @SuppressWarnings(value = "unchecked")
    private static <Type> AsyncTask<Type> submitTaskToExecutorService(Object task) {
        Future<?> future = null;
        Exception exception = null;

        try {
            // checks the instance type of the task...
            if (task instanceof Runnable) {
                final Runnable runnable = (Runnable) task;

                future = executorService.submit(runnable);
            } else if (task instanceof Callable<?>) {
                final Callable<?> callable = (Callable<?>) task;

                future = executorService.submit(callable);
            } else {
                // if the task doesn't match any of the types,
                // we'll set an exception...
                exception = new Exception("Invalid task provided.");
            }
        } catch (Exception _exception) {
            // assigns the exception to the outer scope variable...
            exception = _exception;
        }

        // if future is not null, returns an async task derived from the future...
        if (future != null) { return AsyncTask.from((Future<Type>) future); }

        logger.error("An exception occurred while running the async task.", exception);

        // otherwise, we shall create an async task from the exception...
        return AsyncTask.from(exception);
    }

    /**
     * Asynchronously executes a task.
     * @implNote This method is thread-safe.
     * @param task Task to execute.
     * @return An AsyncTask object.
     */
    static AsyncTask<?> run(Runnable task) {
        return submitTaskToExecutorService(task);
    }

    /**
     * Asynchronously executes a task.
     * @implNote This method is thread-safe.
     * @param task Task to execute.
     * @return An AsyncTask object.
     * @param <Type> Asynchronous task result type.
     */
    static <Type> AsyncTask<Type> run(Callable<Type> task) {
        return submitTaskToExecutorService(task);
    }

    /**
     * Throws exception if the provided array of objects
     * contains exception.
     * @param objects An array of objects that may or may not
     *                contain exception.
     * @return The provided array without any modification.
     * @throws Exception If any of the objects is an exception.
     */
    private static Object[] throwExceptionIfExists(Object[] objects) throws Exception {
        for (int i = 0; i < objects.length; ++i) {
            final Object object = objects[i];

            // if object is not an instance of Exception class,
            // we shall skip this iteration...
            if (!(object instanceof Exception)) { continue; }

            // if exception is found, we'll throw the exception...
            throw (Exception) object;
        }

        // if no exception is found after iteration,
        // we shall return the objects as-is...
        return objects;
    }

    /**
     * Awaits all the async tasks. This method throws
     * the first available exception (if found).
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * @throws Exception If any of the async tasks threw exception.
     */
    static Object[] await(AsyncTask<?>[] asyncTasks) throws Exception {
        // awaits all the async tasks...
        final Object[] results = awaitAll(asyncTasks);

        // otherwise, looks for exception object within the results list...
        return throwExceptionIfExists(results);
    }

    /**
     * Awaits all the async tasks. This method throws
     * the first available exception (if found).
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * @throws Exception If any of the async tasks threw exception.
     */
    static Object[] await(Iterable<AsyncTask<?>> asyncTasks) throws Exception {
        // awaits all the async tasks...
        final Object[] results = awaitAll(asyncTasks);

        // otherwise, looks for exception object within the results list...
        return throwExceptionIfExists(results);
    }

    /**
     * Awaits all the async tasks. This method throws
     * the first available exception (if found).
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * @throws Exception If any of the async tasks threw exception.
     */
    static Object[] await(List<AsyncTask<?>> asyncTasks) throws Exception {
        // awaits all the async tasks...
        final Object[] results = awaitAll(asyncTasks);

        // otherwise, looks for exception object within the results list...
        return throwExceptionIfExists(results);
    }

    /**
     * Awaits all the async tasks.
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * The list may contain actual results or exceptions.
     */
    static Object[] awaitAll(AsyncTask<?>[] asyncTasks) {
        return awaitAll(asyncTasks, asyncTasks.length);
    }

    /**
     * Awaits all the async tasks.
     * @param asyncTasks Async tasks to be awaited.
     * @param length Length of the array till which
     * @return An array containing the results of all the tasks.
     * The list may contain actual results or exceptions.
     */
    static Object[] awaitAll(AsyncTask<?>[] asyncTasks, int length) {
        // instantiates an array to hold all the async task results...
        final Object[] results = new Object[length];

        for (int i = 0; i < length; ++i) {
            final AsyncTask<?> asyncTask = asyncTasks[i];
            Object result;

            try {
                // awaiting async task may throw exception...
                result = asyncTask.await();
            } catch (Exception exception) {
                // if exception is thrown, we shall set the
                // exception as the result...
                result = exception;
            }

            // adds the result to the array...
            results[i] = result;
        }

        // finally, we shall return the results...
        return results;
    }

    /**
     * Awaits all the async tasks.
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * The list may contain actual results or exceptions.
     */
    static Object[] awaitAll(Iterable<AsyncTask<?>> asyncTasks) {
        // instantiates a list to hold all the async tasks...
        final List<AsyncTask<?>> _asyncTasks = new ArrayList<>();

        // adds all the async tasks to our newly created list...
        for (final AsyncTask<?> asyncTask : asyncTasks) {
            _asyncTasks.add(asyncTask);
        }

        // awaits all the tasks...
        return awaitAll(_asyncTasks);
    }

    /**
     * Awaits all the async tasks.
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * The list may contain actual results or exceptions.
     */
    static Object[] awaitAll(List<AsyncTask<?>> asyncTasks) {
        // checks if null or empty list is provided...
        if (asyncTasks == null || asyncTasks.isEmpty()) { return ObjectUtilities.getEmptyObjectArray(); }

        return awaitAll(asyncTasks.toArray(new AsyncTask[0]));
    }

    static ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Releases all the resources associated with the
     * asynchronous task execution runtime.
     */
    static void dispose() {
        logger.info("Releasing all the resources associated with the asynchronous task executor.");

        try {
            executorService.shutdownNow();

            logger.info("Executor service shutdown successful.");
        } catch (Exception exception) {
            logger.error("An exception occurred while shutting down the underlying executor service.", exception);
        }

        // waits for the executor service termination...
        while (!ThreadUtilities.awaitExecutorServiceTermination(
                EXECUTOR_SERVICE_TERMINATION_WAIT_TIMEOUT_IN_MILLISECONDS, executorService)) {
            logger.info("Waiting for the executor service termination.");
        }

        logger.info("Successfully terminated the executor service.");

        try {
            executorService.shutdown();

            logger.info("Successfully closed the executor service.");
        } catch (Exception exception) {
            logger.warn("An exception occurred while closing the underlying executor service.", exception);
        }
    }
}
