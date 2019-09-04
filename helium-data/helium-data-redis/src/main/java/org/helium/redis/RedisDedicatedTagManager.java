package org.helium.redis;

import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.task.DedicatedTagManager;

/**
 * Created by Lei Gao on 3/17/16.
 */
@ServiceImplementation
public class RedisDedicatedTagManager implements DedicatedTagManager {
	private static final int EXPIRES_SECOND = 60 * 60;
	private static final String PREFIX = "hdtag:";

	@FieldSetter("DTAGS")
	private RedisClient client;

	@Override
	public String getTag(String tag) {
		return client.get(PREFIX + tag);
	}

	@Override
	public void putTag(String tag, String value) {
		client.setex(PREFIX + tag, EXPIRES_SECOND, value);
	}

	/**
	 * 如果存在就设置一个值, 否则就返回老值
	 * @param tag
	 * @param value
	 * @return
	 */
	@Override
	public String getOrPutTag(String tag, String value) {
		/**
		 * 存储数据到缓存中，并制定过期时间和当Key存在时是否覆盖。
		 *
		 * @param key
		 * @param value
		 * @param nxxx
		 *            nxxx的值只能取NX或者XX，如果取NX，则只有当key不存在是才进行set，如果取XX，则只有当key已经存在时才进行set
		 *
		 * @param expx expx的值只能取EX或者PX，代表数据过期时间的单位，EX代表秒，PX代表毫秒。
		 * @param time 过期时间，单位是expx所代表的单位。
		 * @return
		 */
		client.set(PREFIX + tag, value, "NX", "EX", EXPIRES_SECOND);
		return client.get(PREFIX + tag);
	}

	@Override
	public void deleteTag(String tag) {
		client.del(PREFIX + tag);
	}
}
