package org.helium.module;

/**
 * 异步情况下的module操作
 * 
 * @author Lv.Mingwei
 * 
 * @param <R>
 *            请求
 * @param <D>
 *            数据
 */
public abstract class AsyncModule<R, D> implements Module<R, D> {

	private Module<R, D> next;

	/**
	 * 模块执行执行
	 * 
	 */
	public ModuleResult<R, D> process(R request, D data) {
		ModuleResult<R, D> result = null;
		if (isMatch(request, data)) {
			result = innerProcess(request, data);
		} else {
			// 如果没有命中当前项，则继续寻找
			result = ModuleResult.newContinue(request, data);
		}
		return next(result);
	}

	/**
	 * 子类需实现的业务逻辑方法
	 * 
	 * @param request
	 * @param data
	 * @return
	 */
	protected abstract ModuleResult<R, D> innerProcess(R request, D data);

	/**
	 * 模块运行恢复
	 * 
	 * @param result
	 */
	protected void resume(ModuleResult<R, D> result) {
		next(result);
	}

	private ModuleResult<R, D> next(ModuleResult<R, D> result) {
		// 如果模块运行结果为继续，并且存在下一跳，则执行下一个模块
		if (result.getState() == ModuleState.CONTINUE && next != null) {
			return next.process(result.getMessage(), result.getData());
		}
		return result;
	}

	/**
	 * 添加下一跳地址
	 * 
	 * @param module
	 */
	public void setNext(Module<R, D> module) {
		this.next = module;
	}

}
