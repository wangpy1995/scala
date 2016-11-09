package callback.bossandworker;

/**
 * Created by w5921 on 2016/11/10.
 */
public class Worker {

    private String name;

    public Worker(String name) {
        this.name = name;
    }

    public void work(CallbackInterface callbackInterface, int jobNum) {
        for (int i = 0; i < jobNum; i++) {
            if (i == jobNum - 1)
                System.out.println(name + "正在開始工作" + i + "......工作完成");
        }
        callbackInterface.execute(name);
    }

    public String getName() {
        return name;
    }
}
