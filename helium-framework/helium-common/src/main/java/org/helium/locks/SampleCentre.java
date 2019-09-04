package org.helium.locks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lvmingwei on 16-6-14.
 */
public class SampleCentre implements ULockCentre {

    private static final String serviceName = ManagementFactory.getRuntimeMXBean().getName();

    private static final String pid = serviceName.substring(0, serviceName.indexOf('@'));

    private static final Object lock = new Object();

    private static final Repository repo = new Repository(lock);

    private static final Map<String, ULockEvent> EVENT_MAP = new ConcurrentHashMap();

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleCentre.class);

    @Override
    public void reg(String id, ULockEvent event) {
        synchronized (lock) {
            String selfId = getSelfId();
            LOGGER.debug("reg.selfId:" + selfId);
            repo.put(id, selfId);
            EVENT_MAP.put(selfId, event);
        }
    }

    @Override
    public void unreg(String id) {
        synchronized (lock) {
            // 若时首个正在执行的内容进行解除,那么需要进行release通知
            if (isFirst(id)) {
                String ownerId = repo.poll(id);
                LOGGER.debug("unreg.selfId:" + getSelfId() + "; ownerId:" + ownerId);
                if (ownerId != null) {
                    ULockEvent event = EVENT_MAP.remove(ownerId);
                    if (event != null) {
                        event.doRun();
                    }
                }
            } else {
                repo.remove(id, getSelfId());
            }
        }
    }

    @Override
    public boolean isFirst(String id) {
        synchronized (lock) {
            String selfId = getSelfId();
            String ownerId = repo.peek(id);
            LOGGER.debug("isFirst.selfId:" + selfId + "; ownerId" + ownerId);
            boolean isTrue = selfId.equals(ownerId);
            if (!isTrue) {
                LOGGER.debug("isTrue.SelfId:" + selfId + "; OwnerId:" + ownerId + "; Result:" + isTrue);
            }
            return selfId.equals(ownerId);
        }
    }

    private String getSelfId() {
        synchronized (lock) {
            Thread current = Thread.currentThread();
            return pid + "-" + current.getName() + "-" + current.getId();
        }
    }

    private static class Repository {

        private static final Map<String, Deque<String>> LOCK_MAP = new ConcurrentHashMap();

        private Object lock;

        public Repository(Object lock) {
            this.lock = lock;
        }

        private boolean put(String id, String owner) {
            synchronized (lock) {
                Deque<String> lockQueue = LOCK_MAP.get(id);
                if (lockQueue == null) {
                    lockQueue = new LinkedList<>();
                    LOCK_MAP.put(id, lockQueue);
                }
                if (lockQueue.contains(owner)) {
                    return false;
                } else {
                    lockQueue.addLast(owner);
                    return true;
                }
            }
        }

        private void remove(String id, String ownerId) {
            synchronized (lock) {
                Deque<String> lockQueue = LOCK_MAP.get(id);
                if (lockQueue != null) {
                    lockQueue.remove(ownerId);
                }
            }
        }

        private String poll(String id) {
            synchronized (lock) {
                Deque<String> lockQueue = LOCK_MAP.get(id);
                if (lockQueue == null) {
                    return null;
                }
                return lockQueue.poll();
            }
        }

        private String peek(String id) {
            synchronized (lock) {
                Queue<String> lockQueue = LOCK_MAP.get(id);
                if (lockQueue == null) {
                    return null;
                }
                return lockQueue.peek();
            }
        }

    }

    public static void main(String args[]) {
        Deque<String> lockQueue = new LinkedList<>();
        lockQueue.addLast("A");
        lockQueue.addLast("B");
        lockQueue.addLast("C");
        lockQueue.addLast("D");
        lockQueue.addLast("E");
        LOGGER.debug(lockQueue.peek());
        LOGGER.debug(lockQueue.poll());
        LOGGER.debug(lockQueue.poll());
        LOGGER.debug(lockQueue.poll());
        LOGGER.debug(lockQueue.poll());
        LOGGER.debug(lockQueue.poll());
    }

}
