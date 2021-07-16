package org.helium.superpojo;

/**
 * @author wuhao
 * @createTime 2021-07-15 17:53:00
 */
public class SuperPojo {
	public String toJsonString() {
		return JsonUtils.toJson(this);
	}

	public void parseFromJson(String json) {
		JsonUtils.toObject(json, this.getClass());
	}

	public byte[] toPbByteArray() {
		return SuperPojoManager.toPbByteArray(this);
	}

	public void parsePbFrom(byte[] buffer) {
		SuperPojoManager.parsePbFrom(buffer, this.getClass());
	}

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
}
