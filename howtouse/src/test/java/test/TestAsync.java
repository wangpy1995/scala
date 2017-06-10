package test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by wpy on 2017/6/10.
 */
public class TestAsync {
    public static void main(String[] args) {
        //假设四个读者，两个作者
        Reader r1 = new Reader("谢广坤");
        Reader r2 = new Reader("赵四");
        Reader r3 = new Reader("七哥");
        Reader r4 = new Reader("刘能");
        Writer w1 = new Writer("谢大脚");
        Writer w2 = new Writer("王小蒙");
        //四人关注了谢大脚
        r1.subscribe("谢大脚");
        r2.subscribe("谢大脚");
        r3.subscribe("谢大脚");
        r4.subscribe("谢大脚");
        //七哥和刘能还关注了王小蒙
        r3.subscribe("王小蒙");
        r4.subscribe("王小蒙");
        // 作者发布新书就会通知关注的读者
        // 谢大脚写了设计模式
        w1.addNovel("设计模式");
        // 王小蒙写了JAVA编程思想
        w2.addNovel("JAVA编程思想");
        // 谢广坤取消关注谢大脚
        r1.unsubscribe("谢大脚");
        // 谢大脚再写书将不会通知谢广坤
        w1.addNovel("观察者模式");
    }
}

//事件监听
interface Listener {
}

class WriterEvent {
    private Writer writer;

    public WriterEvent(Writer writer) {
        if (writer == null) throw new IllegalArgumentException("null source");
        this.writer = writer;
    }

    public Writer getWriter() {
        return writer;
    }

    public String toString() {
        return getClass().getName() + "[source=" + writer + "]";
    }
}

interface WriterListener extends Listener {
    void addNovel(WriterEvent event);
}

class WriterManager {
    private static class Writers {
        private static Map<String, Writer> writerMap = new HashMap<>();
    }

    public static Map<String, Writer> getInstance() {
        return Writers.writerMap;
    }

}

class Writer {
    private String name;

    public String getName() {
        return name;
    }

    public String getLastNovel() {
        return lastNovel;
    }

    private String lastNovel;
    private Set<WriterListener> writerListeners = new HashSet<>();

    public Writer(String name) {
        super();
        this.name = name;
        WriterManager.getInstance().put(name, this);
    }

    //作者发布新小说了，要通知所有关注自己的读者
    public void addNovel(String novel) {
        System.out.println(name + "发布了新书《" + novel + "》！");
        lastNovel = novel;
        fireEvent();
    }

    //触发发布新书的事件，通知所有监听这件事的监听器
    private void fireEvent() {
        WriterEvent writerEvent = new WriterEvent(this);
        for (WriterListener writerListener : writerListeners) {
            writerListener.addNovel(writerEvent);
        }
    }

    //提供给外部注册成为自己的监听器的方法
    public void registerListener(WriterListener writerListener) {
        writerListeners.add(writerListener);
    }

    //提供给外部注销的方法
    public void unregisterListener(WriterListener writerListener) {
        writerListeners.remove(writerListener);
    }
}

class Reader implements WriterListener {
    private String name;

    public String getName() {
        return name;
    }

    public Reader(String name) {
        super();
        this.name = name;
    }

    //读者可以关注某一位作者，关注则代表把自己加到作者的监听器列表里
    public void subscribe(String writerName) {
        WriterManager.getInstance().get(writerName).registerListener(this);
    }

    //读者可以取消关注某一位作者，取消关注则代表把自己从作者的监听器列表里注销
    public void unsubscribe(String writerName) {
        WriterManager.getInstance().get(writerName).unregisterListener(this);
    }

    public void addNovel(WriterEvent writerEvent) {
        Writer writer = writerEvent.getWriter();
        System.out.println(name + "知道" + writer.getName() + "发布了新书《" + writer.getLastNovel() + "》，非要去看！");
    }
}

