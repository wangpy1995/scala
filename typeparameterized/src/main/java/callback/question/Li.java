package callback.question;

/**
 * Created by w5921 on 2016/11/9.
 */
public class Li {

    public void execute(Callback callback, String question) {
        try {
            doSomething();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("小李开始思考小王的问题: " + question);
        String result = "2";
        callback.solve(result);
    }

    private void doSomething() throws InterruptedException {
        System.out.println("小李很累正在睡觉。。。。");
        Thread.sleep(6000);
    }
}
