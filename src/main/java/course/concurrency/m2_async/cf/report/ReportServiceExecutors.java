package course.concurrency.m2_async.cf.report;

import course.concurrency.m2_async.cf.LoadGenerator;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class ReportServiceExecutors {

//    private ExecutorService executor = ForkJoinPool.commonPool();         // compute:  44770 ЦП 100%; sleep: 31780 ЦП 1-15%
    private ExecutorService executor = Executors.newSingleThreadExecutor(); // compute:  146829 ЦП 25%;
//    private ExecutorService executor = Executors.newFixedThreadPool(2); // compute:  82046 ЦП 36%;
//    private ExecutorService executor = Executors.newFixedThreadPool(4);       // compute:  44942 ЦП 75%;
//    private ExecutorService executor = Executors.newFixedThreadPool(8); // compute:  41646 ЦП 100%;
//    private ExecutorService executor = Executors.newFixedThreadPool(16);      // compute:  43220 ЦП 100%;
//    private ExecutorService executor = Executors.newFixedThreadPool(24);            // compute:  43919 ЦП 100%;
//    private ExecutorService executor = Executors.newFixedThreadPool(32);    // compute:  43954 ЦП 100%;
//    private ExecutorService executor = Executors.newFixedThreadPool(64); // compute:  44941 ЦП 100%;
//private ExecutorService executor = Executors.newFixedThreadPool(128); // compute:  44689 ЦП 100%;
//    private ExecutorService executor = Executors.newCachedThreadPool(); // compute:  43302 ЦП 100%;
//private ExecutorService executor = Executors.newWorkStealingPool();   // compute:  41850 ЦП 100%;
    private LoadGenerator loadGenerator = new LoadGenerator();
    SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();

    public Others.Report getReport() {
        Future<Collection<Others.Item>> iFuture =
                executor.submit(() -> getItems());
        Future<Collection<Others.Customer>> customersFuture =
                executor.submit(() -> getActiveCustomers());

        try {
            Collection<Others.Customer> customers = customersFuture.get();
            Collection<Others.Item> items = iFuture.get();
            return combineResults(items, customers);
        } catch (ExecutionException | InterruptedException ex) {}

        return new Others.Report();
    }

    private Others.Report combineResults(Collection<Others.Item> items, Collection<Others.Customer> customers) {
        return new Others.Report();
    }

    private Collection<Others.Customer> getActiveCustomers() {
        loadGenerator.work();
        loadGenerator.work();
        return List.of(new Others.Customer(), new Others.Customer());
    }

    private Collection<Others.Item> getItems() {
        loadGenerator.work();
        return List.of(new Others.Item(), new Others.Item());
    }

    public void shutdown() {
        executor.shutdown();
    }
}
