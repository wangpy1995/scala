package callback;

import java.io.File;

/**
 * Created by w5921 on 2016/11/9.
 */
public class CalculateInstance implements Calculatable, Callbackable {

    private File file;

    public CalculateInstance(File file) {
        this.file = file;
    }

    @Override
    public void calculate() {
        CallbackListDigest d = new CallbackListDigest(file);
        d.addCallback(this);
        Thread thread = new Thread(d);
        thread.start();
    }

    @Override
    public void callback(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append(" ");
        sb.append(file.toString()).append(":");
        for (byte b : bytes) {
            sb.append(b);
        }
        System.out.println(sb.toString());
    }

}
