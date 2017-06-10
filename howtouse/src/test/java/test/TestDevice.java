package test;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class TestDevice extends Thread {
    boolean running = true;

    public synchronized boolean isRunning() {
        return running;
    }

    public void run() {
        running = true;
        try {
            System.out.println("Entering run...");
            ResourceBundle myResources = ResourceBundle.getBundle("sun.text.resources.FormatData", Locale.US);
            System.out.println(myResources.getKeys().nextElement());
            System.out.println(new Date());
            System.out.println("Finished reading data...");
        } finally {
            System.out.println("setting running=false");
            running = false;
        }
    }

    public static void main(String[] args) {

        TestDevice device = null;
        try {
            device = new TestDevice();
            synchronized (device) {
                device.start();
                while (device.isRunning()) {
                }
            }
        } finally {
            System.out.println("closing....");
            device = null;
        }
    }
}