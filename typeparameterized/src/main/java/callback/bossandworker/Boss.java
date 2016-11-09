package callback.bossandworker;

/**
 * Created by w5921 on 2016/11/10.
 */
public class Boss implements CallbackInterface {
    private String name;

    public Boss(String name) {
        this.name = name;
    }

    @Override
    public void execute(String name) {
        System.out.println(this.name + "已經收到" + name + "工作完成的結果！");
    }

    private void sendWork(Worker worker, int jobNum) {
        System.out.println("將任務發送給" + worker.getName());
        new Thread(() -> {
            worker.work(Boss.this, jobNum);
        }).start();
        doOtherThings();
    }

    private void doOtherThings() {
        System.out.println(this.name + " 開始做其他事情");
    }

    public static void main(String[] args) {
        Boss boss1 = new Boss("boss1");
       /* Boss boss2 = new Boss("boss2");
        Boss boss3 = new Boss("boss3");*/

        boss1.sendWork(new Worker("張三"), 5);
        boss1.sendWork(new Worker("李四"), 8);
        boss1.sendWork(new Worker("王五"), 10);
        boss1.sendWork(new Worker("趙四"), 7);
    }
}
