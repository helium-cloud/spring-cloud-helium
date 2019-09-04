package com.feinno.superpojo;

/**
 * SuperPojoBuilder及SuperProtoFactory的仓库<br>
 * 
 * @author lvmingwei
 * 
 */
public interface IRepository {

	public <T extends Object> BuilderFactory getBuilderFactory(Class<T> clazz, Class<?>... genericClass);

}
