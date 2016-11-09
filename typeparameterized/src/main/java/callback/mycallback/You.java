package callback.mycallback;

/**
 * Created by w5921 on 2016/11/9.
 */
public class You {

    public void callMe(MyCallback myCallback, String status) {
        System.out.println("You: start now.....");
        try {
            System.out.println("You: there is something wrong,trying to fix it....");
            Thread.sleep(3000);
            myCallback.callback(status);
            System.out.println("You: start doing other things.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
