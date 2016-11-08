package callback.question;

/**
 * Created by w5921 on 2016/11/9.
 */
public class Wang implements Callback {

    private Li li;

    public Wang(Li li) {
        this.li = li;
    }

    public void ask(String question) {
        new Thread(() -> li.execute(Wang.this, question)).start();
        play();
    }

    private void play() {
        System.out.println("小王问完问题逛街去了。。。。。");
    }

    @Override
    public void solve(String result) {
        System.out.println("答案是" + result);
    }

    public static void main(String[] args) {
        Li li = new Li();
        Wang wang = new Wang(li);
        wang.ask("1+1=?");
    }
}
