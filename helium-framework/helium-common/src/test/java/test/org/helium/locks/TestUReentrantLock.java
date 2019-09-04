package test.org.helium.locks;

import org.helium.locks.SampleCentre;
import org.helium.locks.ULockManager;
import org.helium.locks.UReentrantLock;
import org.helium.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;

/**
 * Created by lvmingwei on 16-6-17.
 */
public class TestUReentrantLock {

    private static SimpleDateFormat format = new SimpleDateFormat(DateUtil.DEFAULT_DATETIME_HYPHEN_FORMAT_LONG);

    public static void main(String args[]) {

        ULockManager.initial(new SampleCentre());
        Lock lock = null;
        lock = new UReentrantLock();
        // lock = new ReentrantLock();
        for (int i = 0; i < 200; i++) {
            createThread("T" + i, lock).start();
        }
    }


    private static void printTime(String body) {
        System.out.println(format.format(new Date()) + " : " + body);
    }

    private static Thread createThread(String name, Lock lock) {
        return new Thread(() -> {
            try {
                lock.lock();
                printTime(name);
                Thread.sleep(10);
                lock.unlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
