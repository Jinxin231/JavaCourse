package concurrency;

public class Concurrency03 {

    private static  int result;
    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法

        Thread t1 = new Thread(() -> {
            result = sum(); //这是得到的返回值
        });

        t1.start();
        Thread.sleep(5000);

        // 确保  拿到result 并输出
        System.out.println("异步计算结果为：" + result);

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
