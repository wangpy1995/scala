package callback;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by w5921 on 2016/11/9.
 */
public class CallbackListDigest implements Runnable {

    private File file;
    public List<Callbackable> callbacks = new ArrayList<Callbackable>();

    public CallbackListDigest(File file) {
        this.file = file;
    }

    public void addCallback(Callbackable callback) {
        callbacks.add(callback);
    }

    public void removeCallback(Callbackable callback) {
        callbacks.remove(callback);
    }

    @Override
    public void run() {
        byte[] bytes = digest();
        sendDigest(bytes);
    }

    private byte[] digest() {
        DigestInputStream d = null;
        try {
            InputStream in = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("SHA");
            d = new DigestInputStream(in, md);
            int b;
            while ((b = d.read()) != -1) ;
            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (d != null) {
                    d.close();
                    d = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void sendDigest(byte[] bytes) {
        Iterator<Callbackable> iterator = callbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().callback(bytes);
        }
    }

    public static void main(String[] args) {
        String[] files = {
                "f:\\java.txt",
                "f:\\c.txt",
                "f:\\c++.txt",
                "f:\\javascript.txt",
                "f:\\html.txt"
        };
        for (int i = 0; i < files.length; i++) {
            (new CalculateInstance(new File(files[i]))).calculate();
        }
    }
}
