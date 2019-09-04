package zconfig.configuration.table;

import com.feinno.superpojo.type.EnumInteger;

public enum UserBasicStatus implements EnumInteger {

	// 所有类型均适用
	Any(0),
	// 正常
	Normal(1),
	// CM用户停机状态
	Suspend(2),
	// 关闭服务
	Closed(3),
	// VGOP同步失败
	VgopSyncFailed(4),
	// 黑名单用户
	Blacklist(5),
	// 白名单用户
	Whitelist(6),
	// 受限用户
	Restricted(7);

	/**
	 * 枚举对象对应的值
	 */
	private int value;

	/**
	 * 带参构造器
	 * 
	 * @param value
	 *            枚举对象对应的 值
	 */
	private UserBasicStatus(int value) {
		this.value = value;
	}

	/**
	 * 获取枚举对象的整型值
	 * 
	 * @return 返回 int 值
	 */
	public int intValue() {
		return value;
	}

	/**
	 * 转换整型为 枚举类型,只能给IICUserType使用
	 * 
	 * @param value
	 *            整型值
	 * @return 返回整型值 所对应的枚举类型
	 */
	public static UserBasicStatus intConvert(int value ) {
		switch (value) {
			case 0:
				return Any;
			case 1:
				return Normal;
			case 2:
				return Suspend;
			case 3:
				return Closed;
			case 4:
				return VgopSyncFailed;
			case 5:
				return Blacklist;
			case 6:
				return Whitelist;
			case 7:
				return Restricted;
			default:
				return null;
		}
	}
}
