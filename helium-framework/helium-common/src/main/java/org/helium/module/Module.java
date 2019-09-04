package org.helium.module;

/**
 * 
 * @author Lv.Mingwei
 * 
 * @param <R>
 * @param <D>
 */
public interface Module<R, D> {

	/**
	 * 模块初始化方法
	 */
	public void init();

	/**
	 * 模块业务逻辑处理方法
	 * 
	 * @param <T1>
	 * @param <T2>
	 * 
	 * @return
	 */
	public ModuleResult<R, D> process(R request, D data);

	/**
	 * 模块业务逻辑匹配方法
	 * 
	 * @param <T1>
	 * @param <T2>
	 * 
	 * @return
	 */
	public boolean isMatch(R request, D data);

	/**
	 * 模块注销方法
	 */
	public void distory();

}
