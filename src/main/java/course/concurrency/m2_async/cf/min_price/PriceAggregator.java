package course.concurrency.m2_async.cf.min_price;

import course.concurrency.m2_async.cf.report.Others;

import java.util.*;
import java.util.concurrent.*;

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
        List<Double> prices = new ArrayList<>();

        try {
            return CompletableFuture.supplyAsync(() -> {
                for (long shopId : shopIds) {
                    executor.submit(() -> {
                        try {
                            Double currentPrice = CompletableFuture
                                    .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor)
                                    .handle((price, ex) -> {
                                        if (ex != null) {
                                            return Double.NaN;
                                        } else {
                                            return price;
                                        }
                                    }).get(3, SECONDS);

                            if (!currentPrice.equals(Double.NaN)) {
                                prices.add(currentPrice);
                            }
                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                        return prices;
                    });
                }

                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                return prices;
            }).thenApply((allPrices) -> {
                if (allPrices.size() == 0) {
                    return Double.NaN;
                } else {
                    return allPrices.stream().reduce(Double::min).get();
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
