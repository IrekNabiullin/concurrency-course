package course.concurrency.m2_async.cf.min_price;

import course.concurrency.m2_async.cf.report.Others;

import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

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
//        Double result = Double.NaN;
        long startTime = System.currentTimeMillis();
        System.out.println("start time = " + startTime);
        List<Double> prices = new ArrayList<>();

        System.out.println("1");

        CompletableFuture[] futures = shopIds.stream()
                .map(shopId -> {
                    CompletableFuture f = CompletableFuture
                            .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor)
                            .handle((price, ex) -> {
                                if (ex != null) {
                                    System.out.println("Thread: " + Thread.currentThread().getName() + " shopId = " + shopId + "return Double.NaN");
                                    return Double.NaN;
                                } else {
                                    if (price > 0) {
                                        prices.add(price);
                                    }
                                    long currentTimeMillis = System.currentTimeMillis();
                                    System.out.println("Thread: " + Thread.currentThread().getName() + " shopId = " + shopId + "price = " + price +
                                            " currentTimeMillis = " + currentTimeMillis + " time elapsed = " + (currentTimeMillis - startTime));
                                    return price;
                                }
                            });
                    return f;
                })
                .toArray(CompletableFuture[]::new);

        long currentTimeMillis = System.currentTimeMillis();
        System.out.println("2 currentTimeMillis = " + currentTimeMillis + " time elapsed = " + (currentTimeMillis - startTime));

        CompletableFuture<Void> all = CompletableFuture.allOf(futures);

        currentTimeMillis = System.currentTimeMillis();
        System.out.println("3 currentTimeMillis = " + currentTimeMillis + " time elapsed = " + (currentTimeMillis - startTime));

        System.out.println("futures.length = " + futures.length);
        System.out.println("prices.size() = " + prices.size());
        currentTimeMillis = System.currentTimeMillis();
        System.out.println("4 currentTimeMillis = " + currentTimeMillis + " time elapsed = " + (currentTimeMillis - startTime));


        try {
            return all
                    .thenApply((prc) -> {
                        if (prices.size() == 0) {
//                            System.out.println("prices.size() = " + prices.size());
//                            System.out.println("Thread: " + Thread.currentThread().getName() + " return Double.NaN;");
                            return Double.NaN;
                        } else {
                            System.out.println("prices.sizzze() = " + prices.size());
                            prices.forEach(System.out::println);
                            return prices.stream().reduce(Double::min).get();
//                            System.out.println("res = " + res);
//                            return res;
                        }
                    }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

/*
        try {
            return CompletableFuture.supplyAsync(() -> {
                        for (long shopId : shopIds) {
                            double currentPrice;
                            try {
                                currentPrice = CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor)
                                        .handle((price, ex) -> {
                                            if (ex != null) {
                                                System.out.println("Thread: " + Thread.currentThread().getName() + " shopId = " + shopId + "return Double.NaN");
                                                return Double.NaN;
                                            } else {
                                                System.out.println("Thread: " + Thread.currentThread().getName() + " shopId = " + shopId + "price = " + price);
//                                                prices.add(price);
                                                return price;
                                            }
                                        })
                                        .get(3, SECONDS);
                            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                                throw new RuntimeException(ex);
                            }
                            prices.add(currentPrice);
                        }
                        System.out.println("prices.size = " + prices.size());
                        return prices;
                    })
                    .handle((price, ex) -> {
                        if (ex != null) {
                            System.out.println("Thread: " + Thread.currentThread().getName() + " returning Double.NaN");
//                                return Double.NaN;
                        } else {
                            System.out.println("Thread: " + Thread.currentThread().getName() + "prices.size  = " + prices.size());
//                                return prices;
                        }
                        return prices;
                    }).thenApply((prc) -> {
                        if (prices.size() == 0) {
                            System.out.println("Thread: " + Thread.currentThread().getName() + " return Double.NaN;");
                            return Double.NaN;
                        } else {
                            Double res = prices.stream().reduce(Double::min).get();
                            System.out.println("res = " + res);
                            return res;
                        }
                    }).get(3, SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
//            throw new RuntimeException(e);
            System.out.println("Exception in 73");
            return Double.NaN;
        }
    }

 */

    /*
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

     */
}
