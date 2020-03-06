package org.helium.framework.configuration;

import org.helium.framework.entitys.EnvironmentsNode;
import org.helium.framework.entitys.KeyValueNode;

import java.util.List;

/**
 * 环境变量加载器的借口
 * 为特殊的环境变量准备的加载借口
 *
 * Created by Lv Mingwei on 12/23/15.
 */
public interface EnvironmentLoader {
    /**
     * 根据环境变量的内容,选择增加的加载解析方式
     *
     * @param node
     * @return
     */
    List<KeyValueNode> loadEnv(EnvironmentsNode node);
}
