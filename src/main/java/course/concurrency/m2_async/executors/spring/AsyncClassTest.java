package course.concurrency.m2_async.executors.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

@EnableAsync
@Component
public class AsyncClassTest {

    @Autowired
    public ApplicationContext context;

    @Autowired
    @Qualifier("applicationTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    @Autowired @Lazy
    private AsyncClassTest self;



    @Async
    public void runAsyncTask() {
        System.out.println("runAsyncTask: " + Thread.currentThread().getName());
//        executor.submit(this::internalTask); //1
//        executor.submit(() -> this.internalTask()); //2
//        executor.execute(this::internalTask); //3
//        Runnable myInternalTask = () -> {
//            internalTask();
//        };
//        executor.execute(myInternalTask); //4
//        executor.submit(myInternalTask); //5

//        context.getBean(AsyncClassTest.class).executor.submit(this::internalTask);
//        context.getBean(AsyncClassTest.class).internalTask();

        self.internalTask();


//        Executors.newCachedThreadPool(new ThreadFactory() {
//            private int id = 0;
//            @Override
//            public Thread newThread(Runnable r) {
//                Thread t = new Thread(r);
//                t.setName("NewThread");
//                id++;
//                return t;
//            }
//        }).submit(this::internalTask);
//        internalTask();
    }

    @Async
    public void internalTask() {
        System.out.println("internalTask: " + Thread.currentThread().getName());
    }
}
