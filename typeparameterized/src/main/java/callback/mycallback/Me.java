package callback.mycallback;

/**
 * Created by w5921 on 2016/11/9.
 */
public class Me implements MyCallback {
    private You you;

    public Me(You you) {
        this.you = you;
    }

    public void callYou(String status) {
        System.out.println("Me: I am calling you now,");
        new Thread(() -> {
            you.callMe(Me.this, status);
        }).start();
        System.out.println("Me: I am doing other things now");
    }

    @Override
    public void callback(String status) {
        System.out.println("mission: " + status);
    }

    public static void main(String[] args) {
        You you = new You();
        Me me = new Me(you);
        me.callYou("failed");
    }
}
