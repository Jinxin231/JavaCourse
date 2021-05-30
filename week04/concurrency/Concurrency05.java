package concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;

public class Concurrency05 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        int num = 2;
        CyclicBarrier barrier = new CyclicBarrier(num);
        List<CompletableFuture> list = new ArrayList<>(num);

        for (int i = 0; i < num; i++) {
            CompletableFuture<Integer> future =
                    CompletableFuture.supplyAsync(() ->{
                        CyclicBarrierTask cyclicBarrierTask = new CyclicBarrierTask(barrier);
                        try {
                            return cyclicBarrierTask.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return -1;
                    });
            list.add(future);
        }

        for (CompletableFuture future : list) {
            // 确保  拿到result 并输出
            System.out.println("异步计算结果为：" + future.get());

            System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
        }
        barrier.reset();
    }
}


class CyclicBarrierTask  implements Callable<Integer> {

    private CyclicBarrier barrier;
    public CyclicBarrierTask(CyclicBarrier barrier) {
        this.barrier = barrier;
    }

    @Override
    public Integer call() throws Exception {
        int sum = -1;
        sum = sum();
        this.barrier.await(); // 线程阻塞
        System.out.println("开吃 :" + Thread.currentThread().getName());
        return sum;
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if (a < 2)
            return 1;
        return fibo(a - 1) + fibo(a - 2);
    }
}
