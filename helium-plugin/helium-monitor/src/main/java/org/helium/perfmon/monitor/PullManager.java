/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-10-19
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.perfmon.monitor;


import com.feinno.superpojo.type.DateTime;
import org.helium.perfmon.observation.ObserverReport;

import java.util.*;

/**
 * {在这里补充类的功能说明}
 *
 * Created by Coral
 */
public class PullManager {
    private static Map<String, PullManager> instances;

    static {
        instances = new HashMap<String, PullManager>();
    }

    public synchronized static PullManager getInstance(String key, boolean createIfNotExists) {
        PullManager m;
        m = instances.get(key);
        if (m == null & createIfNotExists) {
            m = new PullManager(key);
            instances.put(key, m);
        }
        return m;
    }

    public synchronized static void removeInstance(String key) {
        instances.remove(key);
    }

    private String key;
    private Object syncQueue = new Object();//
    private Queue<ObserverReport> queue;
    private DateTime lastPull;

    private PullManager(String key) {
        this.key = key;
        queue = new LinkedList<ObserverReport>();
    }

    public boolean isActive() {
        if (DateTime.now().substract(lastPull == null ? DateTime.now() : lastPull).getTotalSeconds() > 5) {
            return false;
        } else {
            return true;
        }
    }

    public void close() {
        removeInstance(key);
    }

    public void enqueueReport(ObserverReport report) {
        synchronized (syncQueue) {
            queue.add(report);
        }
    }

    public List<ObserverReport> pull() {
        List<ObserverReport> list = new ArrayList<ObserverReport>();
        ObserverReport r;
        DateTime now = DateTime.now();
        synchronized (syncQueue) {
            while (true) {
                r = queue.peek();
                if (r == null) {
                    break;
                }

                //
                // 将超过一秒的数据都输出
                if (now.substract(r.getTime()).getTotalMillseconds() > 1000) {
                    queue.poll();
                    list.add(r);
                } else {
                    break;
                }
            }
        }
        lastPull = now;
        return list;
    }
}
