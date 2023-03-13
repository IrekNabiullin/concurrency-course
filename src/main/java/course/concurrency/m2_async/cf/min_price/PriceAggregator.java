package course.concurrency.m2_async.cf.min_price;

import course.concurrency.m2_async.cf.report.Others;

import java.util.*;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

public class PriceAggregator {

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
        double result;
        List<Double> prices = new ArrayList<>();
        ExecutorService executor = Executors.newCachedThreadPool();

        CompletableFuture<List<Double>> pricesResult = CompletableFuture.supplyAsync(() -> {
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

                        if(!currentPrice.equals(Double.NaN)) {
                            prices.add(currentPrice);
                        }
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        e.printStackTrace();
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
        });

        Callable <Double>  minDoublePrice = () -> {
            double minPrice = 0;
            try {
                minPrice = pricesResult.thenApply((allPrices) -> {
                    if (allPrices.size() == 0) {
                        return Double.NaN;
                    } else {
                        return allPrices.stream().reduce(Double::min).get();
                    }
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return minPrice;
        };

        try {
            result = minDoublePrice.call();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result;
    }
}
