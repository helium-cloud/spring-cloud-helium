package org.helium.locks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by lvmingwei on 16-6-1.
 */
public class UReentrantLock implements Lock {

    private Sync sync = null;

    private String lockId = null;

    private ULockCentre centre = ULockManager.getCentre();

    private static final Logger LOGGER = LoggerFactory.getLogger(UReentrantLock.class);

    public UReentrantLock() {
        this(UUID.randomUUID().toString());
    }

    public UReentrantLock(String lockId) {
        this.lockId = lockId;
        sync = new FairSync();
    }

    @Override
    public void lock() {
        sync.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException("UReentrantLock unsupported lockInterruptibly method.");
    }

    @Override
    public boolean tryLock() {
        return sync.tryLock();
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryLock(timeout, unit);
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("UReentrantLock unsupported newCondition method.");
    }

    /**
     * 同步控制器，目的为实现阻塞，继承自AQS
     *
     * @author Lv.Mingwei
     */
    private abstract class Sync extends AbstractQueuedSynchronizer {

        public Sync() {
        }

        protected synchronized final void lock() {
            LOGGER.debug("lock:" + Thread.currentThread().getName() + "-" + Thread.currentThread().getId());
            // 发起lock操作时,将需求加入到仓库中
            regCentre();
            acquire(1);
        }

        protected synchronized final boolean tryLock() {
            // 向調度中心註冊
            regCentre();
            LOGGER.debug("tryLock");
            // 暂时采用公平锁
            // nonfairTryAcquire(1)
            if (tryAcquire(1)) {
                return true;
            } else {
                // 如果锁获取失败,则解除注册
                unregCentre();
                return false;
            }
        }

        protected synchronized final boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
            regCentre();
            // 尝试获取锁
            if (tryAcquireNanos(1, unit.toNanos(timeout))) {
                return true;
            } else {
                // 如果锁获取失败,则解除注册
                unregCentre();
                return false;
            }
        }

        protected final boolean tryAcquire(int acquires) {
            try {
                if (!centre.isFirst(lockId)) {
                    return false;
                } else {
                    LOGGER.debug("TryAcquire True");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            final Thread current = Thread.currentThread();
            int c = getState();
            LOGGER.debug("try.Acquire.c: " + c);
            if (c == 0) {
                if (!hasQueuedPredecessors() &&
                        compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    LOGGER.debug("try.Acquire: " + true);
                    return true;
                }
            } else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            LOGGER.debug("try.Acquire: " + false);
            return false;
        }

//        // 暂时取消非公平锁
//        protected final boolean nonfairTryAcquire(int acquires) {
//            if (!centre.isFirst(lockId)) {
//                return false;
//            }
//            final Thread current = Thread.currentThread();
//            int c = getState();
//            if (c == 0) {
//                if (compareAndSetState(0, acquires)) {
//                    setExclusiveOwnerThread(current);
//                    return true;
//                }
//            } else if (current == getExclusiveOwnerThread()) {
//                int nextc = c + acquires;
//                if (nextc < 0) // overflow
//                    throw new Error("Maximum lock count exceeded");
//                setState(nextc);
//                return true;
//            }
//            return false;
//        }

        /**
         * 锁的释放
         *
         * @param releases
         * @return
         */
        protected final boolean tryRelease(int releases) {
            // 若为Integer.MIN_VALUE,则代表由调度中心发送的强制释放标志,此时进行强制释放
            LOGGER.debug("tryRelease.releases:" + releases);
            if (releases == Integer.MAX_VALUE) {
                setState(0);
                setExclusiveOwnerThread(null);
                return true;
            }
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            int c = getState() - releases;
            setState(c);
            if (c == 0) {
                // 锁已全部退出,通知調度中心解除註冊,释放资源
                unregCentre();
            }
            return false;
        }

        private synchronized void regCentre() {
            // 发起lock操作时,将需求加入到仓库中
            try {
                if (Thread.currentThread() != getExclusiveOwnerThread()) {
                    centre.reg(lockId, new ULockEvent() {
                        @Override
                        public void doRun() {
                            sync.release(Integer.MAX_VALUE);
                        }
                    });
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        private void unregCentre() {
            try {
                centre.unreg(lockId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 這是一個公平鎖,FIFO,按照顺序先入先出
     */
    class FairSync extends Sync {


    }

}
