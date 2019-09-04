package org.helium.framework;

import org.helium.util.Outer;
import org.helium.util.StringUtils;

/**
 * 处理group:name类型的字符串及操作
 * Created by Coral on 5/6/15.
 */
public class BeanIdentity {
	public static final String DELIMITER = ":";

	private String group;
	private String name;

	public String getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}



	public BeanIdentity(String group, String id) {
		if (StringUtils.isNullOrEmpty(group) || StringUtils.isNullOrEmpty(id)) {
			throw new IllegalArgumentException("Neither group nor name can't be Null or EMPTY");
		}
		this.group = group;
		this.name = id;
	}

	public static String format(String group, String id) {
		return group + DELIMITER + id;
	}

	public static BeanIdentity parseFrom(String str) {
		Outer<String> group = new Outer<String>();
		Outer<String> id = new Outer<String>();
		if (StringUtils.splitWithFirst(str, DELIMITER, group, id)) {
			return new BeanIdentity(group.value(), id.value());
		} else {
			throw new IllegalArgumentException("Unexcepted BeanIdentity:" + str);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BeanIdentity that = (BeanIdentity) o;

		if (!group.equals(that.group)) return false;
		return name.equals(that.name);

	}

	@Override
	public int hashCode() {
		int result = group.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return group + DELIMITER + name;
	}


}
