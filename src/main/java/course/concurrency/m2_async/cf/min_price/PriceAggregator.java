package course.concurrency.m2_async.cf.min_price;

import course.concurrency.m2_async.cf.report.Others;

import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class PriceAggregator {
    ExecutorService executor = Executors.newCachedThreadPool();
    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        // здесь будет ваш код
        CompletableFuture[] futures = shopIds.stream()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor)
                        .handle((price, ex) -> ex == null ? price : Double.NaN))
                .toArray(CompletableFuture[]::new);


        return Arrays.stream(futures)
                .filter(Objects::nonNull)
                .map((future) -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return (Double) future.get(1, MILLISECONDS);
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        return Double.NaN;
                    }
                }, executor).join())
                .min(Comparator.naturalOrder()).orElse(Double.NaN);
    }
}
