package org.helium.data.zookeeper.api;

import org.helium.framework.annotations.FieldLoaderType;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;

@FieldLoaderType(loaderType = ZooKeeperClientLoader.class)
public class ZooKeeperClientLoader implements FieldLoader {
    @Override
    public Object loadField(SetterNode node) {
        return null;
    }
}
