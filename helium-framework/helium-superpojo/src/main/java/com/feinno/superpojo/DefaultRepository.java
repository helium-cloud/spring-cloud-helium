package com.feinno.superpojo;

import com.feinno.superpojo.util.ClassUtil;

/**
 * 默认的SuperPojo仓库，仅从当前环境变量寻找
 * 
 * @author lvmingwei
 * 
 */
public class DefaultRepository implements IRepository {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Object> BuilderFactory getBuilderFactory(Class<T> clazz, Class<?>... genericClass) {
		String factory = ClassUtil.getBuilderFactoryClassFullName(clazz);
		try {
			Class<BuilderFactory> factoryClass = (Class<BuilderFactory>) Class.forName(factory);
			return factoryClass.newInstance();
		} catch (Exception e) {
			// 本地资源库允许为空
			// e.printStackTrace();
			return null;
		}
	}

}
