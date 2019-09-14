package org.helium.cloud.configcenter.utils;

import org.helium.cloud.configcenter.utils.KeyUtils;
import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.entitys.SetterNode;
import org.helium.framework.entitys.SetterNodeLoadType;

import java.lang.reflect.Field;

public class LoaderUtils {
    public static SetterNode toSetNode(FieldSetter fieldSetter, Field field, String value){

        String indexKey = KeyUtils.getKey(fieldSetter.value(), fieldSetter.group());
        SetterNode setterNode = new SetterNode();
        setterNode.setField(field.getName());
        setterNode.setKey(indexKey);
        setterNode.setValue(value);
        setterNode.setTimeout(0);
        if (!fieldSetter.loader().getSimpleName().equalsIgnoreCase("null")) {
            setterNode.setLoader(fieldSetter.loader().getName());
        }
        if (fieldSetter.dynamic()){
            setterNode.setLoadType(SetterNodeLoadType.CONFIG_DYNAMIC);
        } else {
            setterNode.setLoadType(SetterNodeLoadType.CONFIG_VALUE);
        }
        return setterNode;
    }



}
