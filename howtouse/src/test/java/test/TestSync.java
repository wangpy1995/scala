package test;

/**
 * Created by wpy on 2017/6/8.
 */
public class TestSync {

    private static String name = "";

    public static synchronized void f1() throws InterruptedException {
        System.out.println(name + " f1....");
        Thread.sleep(5000);
    }

    public static synchronized void f2() throws InterruptedException {
        System.out.println(name + " f2.....");
        Thread.sleep(5000);
    }

    public static void main(String[] args) {
        TestSync ts = new TestSync();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("ts");
                    ts.f2();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("TestSync");
                    TestSync.f1();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
