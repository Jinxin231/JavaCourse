package concurrency;

import java.util.concurrent.*;

public class Concurrency06 {

    private static  int result;

    private static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        long start = System.currentTimeMillis();

        ThreadTest threadTest = new ThreadTest(lock);


        FutureTask<Integer> futuretask= new FutureTask<Integer>(threadTest);
        new Thread(futuretask).start();
        synchronized (lock){
            lock.wait();
        }

        // 确保  拿到result 并输出
        System.out.println("异步计算结果为：" + futuretask.get());

        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");

        // 然后退出main线程
    }

}

class ThreadTest implements Callable<Integer> {

    private final Object lock;
    public ThreadTest(Object lock){
        this.lock = lock;
    }
    @Override
    public Integer call() throws Exception {

        synchronized(lock){
            // 做一些业务逻辑相关的事。。。。
            lock.notify();
            return sum();
        }
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
