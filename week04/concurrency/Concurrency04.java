package concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class Concurrency04 {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        int num = 1;
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(num);

        List<CompletableFuture> list = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            CompletableFuture<Integer> future =
                    CompletableFuture.supplyAsync(() -> {
                        CountDownLatchTask countDownLatchTask = new CountDownLatchTask(countDownLatch);
                                try {
                                    return countDownLatchTask.call();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return -1;
                            }
                    );
            list.add(future);
        }

        for (CompletableFuture future : list) {

            // 确保  拿到result 并输出
            System.out.println("异步计算结果为：" + future.get());

            System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
        }
    }



}

class CountDownLatchTask implements Callable<Integer> {
    private CountDownLatch latch;
    public CountDownLatchTask(CountDownLatch latch) {
        this.latch = latch;
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if (a < 2)
            return 1;
        return fibo(a - 1) + fibo(a - 2);
    }

    @Override
    public Integer call() throws Exception {
        int sum = -1;
        try {
            sum = sum();
            this.latch.countDown();
            System.out.println( " 我的任务 OK 了 :" +Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sum;
    }
}
