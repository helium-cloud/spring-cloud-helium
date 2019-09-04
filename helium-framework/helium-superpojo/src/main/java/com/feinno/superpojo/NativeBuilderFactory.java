package com.feinno.superpojo;

/**
 * 序列化POJO对象的辅助类
 * 
 * @author lvmingwei
 * 
 * @param <T1>
 */
public class NativeBuilderFactory implements BuilderFactory {

	private NativeSuperPojo<?> nativeSuperPojo;

	private BuilderFactory builderFactory;

	protected NativeBuilderFactory(NativeSuperPojo<?> nativeSuperPojo, BuilderFactory builderFactory) {
		this.nativeSuperPojo = nativeSuperPojo;
		this.builderFactory = builderFactory;
	}

	@Override
	public <T2> Builder<T2> newBuilder(T2 t) {
		@SuppressWarnings("unchecked")
		NativeSuperPojo<T2> currentSuperPojo = (NativeSuperPojo<T2>) nativeSuperPojo.newInstance();
		currentSuperPojo.setData(t);
		NativeBuilder<T2> nativeBuilder = new NativeBuilder<T2>(t);
		Builder<NativeSuperPojo<T2>> effectiveBuilder = builderFactory.newBuilder(currentSuperPojo);
		nativeBuilder.regEffectiveBuilder(effectiveBuilder);
		return nativeBuilder;
	}
}
