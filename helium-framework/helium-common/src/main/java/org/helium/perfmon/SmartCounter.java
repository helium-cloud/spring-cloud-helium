package org.helium.perfmon;

/**
 * 计数器通用接口类
 * 
 * Created by Coral
 */
public interface SmartCounter {
	void reset();

	void increase();

	void decrease();

	void increaseBy(long value);

	void setRawValue(long value);

	void increaseRatio(boolean hitted);

	Stopwatch begin();

	/**
	 * 合并
	 * @param c1
	 * @param c2
	 * @return
	 */
	static SmartCounter combine(SmartCounter c1, SmartCounter c2) {
		return new SmartCounter() {
			@Override
			public void reset() {
				c1.reset();
				c2.reset();
			}

			@Override
			public void increase() {
				c1.increase();
				c2.increase();
			}

			@Override
			public void decrease() {
				c1.decrease();
				c2.decrease();
			}

			@Override
			public void increaseBy(long value) {
				c1.increaseBy(value);
				c2.increaseBy(value);
			}

			@Override
			public void setRawValue(long value) {
				c1.setRawValue(value);
				c2.setRawValue(value);
			}

			@Override
			public void increaseRatio(boolean hitted) {
				c1.increaseRatio(hitted);
				c2.increaseRatio(hitted);
			}

			@Override
			public Stopwatch begin() {
				Stopwatch w1 = c1.begin();
				Stopwatch w2 = c2.begin();

				return new StopwatchArray(w1, w2);
			}
		};
	}
}
