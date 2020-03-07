package org.helium.plugin.mybatis;

import org.apache.ibatis.annotations.Mapper;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapper
public @interface MapperSelector {

    String dataSourceName();

}
