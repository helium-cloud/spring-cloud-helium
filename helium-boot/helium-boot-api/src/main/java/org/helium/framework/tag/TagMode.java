package org.helium.framework.tag;

/**
 * Created by Coral on 7/4/15.
 */
public enum TagMode {
	/** 在Bean加载时运行, 启动器或某种启动工具 */
	ON_START,
	/** 在Bean卸载时运行 */
	ON_STOP,
}
