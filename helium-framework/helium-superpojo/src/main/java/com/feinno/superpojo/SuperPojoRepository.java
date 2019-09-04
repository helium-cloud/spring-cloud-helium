package com.feinno.superpojo;

import com.feinno.superpojo.generator.CodeGenerator;
import com.feinno.superpojo.generator.NativeCodeGenerator;
import com.feinno.superpojo.util.JavaEval;

public class SuperPojoRepository implements IRepository {

	@Override
	public <T> BuilderFactory getBuilderFactory(Class<T> clazz, Class<?>... genericClass) {
		if (SuperPojo.class.isAssignableFrom(clazz)) {
			return getSuperPojoBuilderFactory(clazz);
		} else {
			return getNativeBuilderFactory(clazz, genericClass);
		}
	}

	private <T extends Object> BuilderFactory getNativeBuilderFactory(Class<T> clazz, Class<?>... genericClass) {
		try {
			String nativeCode = NativeCodeGenerator.build(clazz, genericClass);
			@SuppressWarnings("unchecked")
			NativeSuperPojo<T> nativeSuperPojo = (NativeSuperPojo<T>) JavaEval.eval(clazz, nativeCode);
			BuilderFactory builderFactory = getSuperPojoBuilderFactory(nativeSuperPojo.getClass());
			NativeBuilderFactory nativeBuilderFactory = new NativeBuilderFactory(nativeSuperPojo, builderFactory);
			return nativeBuilderFactory;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private <T extends Object> BuilderFactory getSuperPojoBuilderFactory(Class<T> clazz) {
		CodeGenerator<T> generator = CodeGenerator.newInstance(clazz);
		try {
			return generator.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
