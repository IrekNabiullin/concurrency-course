package course.concurrency.m2_async.cf.report;

import course.concurrency.m2_async.cf.LoadGenerator;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class ReportServiceCF {

//    private ExecutorService executor = ForkJoinPool.commonPool();         // compute:  41131 ЦП 100%; sleep: 81848 ЦП 1-15%
//    private ExecutorService executor = Executors.newSingleThreadExecutor(); // compute: 149377 ЦП  20%; sleep: 300017 ЦП 1-15%
//    private ExecutorService executor = Executors.newFixedThreadPool(2);   // compute:  79357 ЦП  35%; sleep: 274171 ЦП 1-15%
//    private ExecutorService executor = Executors.newFixedThreadPool(4);   // compute:  44696 ЦП  65%; sleep: 137905 ЦП 1-15%
    private ExecutorService executor = Executors.newFixedThreadPool(8);   // compute: 41599 ЦП 100%; sleep: 69717 ЦП 1-15%
//    private ExecutorService executor = Executors.newFixedThreadPool(16);  // compute:  43521 ЦП 100%; sleep: 36383 ЦП 1-15%
//    private ExecutorService executor = Executors.newFixedThreadPool(24);  // compute:  43153 ЦП 100%; sleep: 24246 ЦП 1-15%
//    private ExecutorService executor = Executors.newFixedThreadPool(32);  // compute:  43365 ЦП 100%; sleep: 19714 ЦП 1-15%
//    private ExecutorService executor = Executors.newFixedThreadPool(64);  // compute:  43412 ЦП 100%; sleep: 15184 ЦП 1-15%
//    private ExecutorService executor = Executors.newFixedThreadPool(128); // compute:  42709 ЦП 100%; sleep: 15178 ЦП 1-15%
//    private ExecutorService executor = Executors.newCachedThreadPool();   // compute:  52253 ЦП 100%; sleep: 15184 ЦП 1-15%
//    private ExecutorService executor = Executors.newWorkStealingPool();   // compute:  42162 ЦП 100%; sleep: 71246 ЦП 1-15%



    private LoadGenerator loadGenerator = new LoadGenerator();

    public Others.Report getReport() {
        CompletableFuture<Collection<Others.Item>> itemsCF =
                CompletableFuture.supplyAsync(() -> getItems(), executor);

        CompletableFuture<Collection<Others.Customer>> customersCF =
                CompletableFuture.supplyAsync(() -> getActiveCustomers(), executor);

        CompletableFuture<Others.Report> reportTask =
                customersCF.thenCombine(itemsCF,
                        (customers, orders) -> combineResults(orders, customers));

        return reportTask.join();
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
