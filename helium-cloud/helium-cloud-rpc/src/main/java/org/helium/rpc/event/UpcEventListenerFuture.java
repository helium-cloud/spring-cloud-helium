package org.helium.rpc.event;

import org.helium.threading.Timeout;
import org.helium.threading.TimeoutListener;
import org.helium.threading.UpcTimerTask;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 事件监听 回调函数
 *
 * @author coral
 * @version 创建时间：2015年1月27日
 */
public class UpcEventListenerFuture<E extends UpcEvent> {
	private Executor executor;

	private UpcEventListener<E> listener;
	private UpcEventMirror<E> mirror;

	private AtomicBoolean isExpire = new AtomicBoolean(false);

	private UpcTimerTask task;
	private long expireTime;

	public UpcEventListenerFuture(Executor executor, UpcEventListener<E> lis, boolean isRepeat, long time) {
		this.executor = executor;
		this.listener = lis;
		if (!isRepeat) {
			this.expireTime = time;
			final UpcEventListenerFuture<E> future = this;
			task = new UpcTimerTask(time, new TimeoutListener() {

				@Override
				public void onTimeout(Timeout timeout) {
					if (isExpire.compareAndSet(false, true)) {
						mirror.remove(future);
						listener.onTimeout();
					}
				}
			});
			task.start();
		}
	}

	void fire(final E e) {
		if (isExpire()) {
			return;
		}
		if (task != null) {
			task.cancel();
		}
		executor.execute(new Runnable() {

			@Override
			public void run() {
				listener.execute(e);
			}
		});
	}

	/**
	 * 设置任务超时时间<秒>
	 */
	public void setTimeout(int milliseconds) {
		if (task != null) {
			// s -> ms
			// 更新timer
			this.expireTime = milliseconds * 1000;
			task.updateTime(expireTime);
		}
	}

	public void cancel() {
		if (isExpire.compareAndSet(false, true)) {
			if (task != null) {
				task.cancel();
			}
			mirror.remove(this);
			executor.execute(new Runnable() {

				@Override
				public void run() {
					listener.onCancel();
				}
			});
		}
	}

	protected boolean isExpire() {
		return isExpire.get();
	}

	protected UpcEventMirror<E> getMirror() {
		return mirror;
	}

	protected void setMirror(UpcEventMirror<E> mirror) {
		this.mirror = mirror;
	}

}
