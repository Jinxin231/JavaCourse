package concurrency;

import java.util.concurrent.*;

public class Concurrency02 {


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long start = System.currentTimeMillis();
        ExecutorService es = Executors.newSingleThreadExecutor();

        Future<Integer> future  = es.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(10000);
                return sum();
            }
        });

        es.shutdown();

        // 确保  拿到result 并输出
        System.out.println("异步计算结果为：" + future.get());

        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
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
