package org.helium.module;

/**
 * 
 * @author Lv.Mingwei
 * 
 * @param <R>
 * @param <D>
 */
public class ModuleResult<R, D> {

	/** 模块处理结果的状态 */
	private ModuleState state;

	private R message;
	private D data;

	private ModuleResult(ModuleState state, R message, D data) {
		this.state = state;
		this.message = message;
		this.data = data;
	}

	public static <R, D> ModuleResult<R, D> newContinue(R request, D data) {
		return new ModuleResult<R, D>(ModuleState.CONTINUE, request, data);
	}

	public static <R, D> ModuleResult<R, D> newCompled(R request, D data) {
		return new ModuleResult<R, D>(ModuleState.COMPLED, request, data);
	}

	public static <R, D> ModuleResult<R, D> newSuspend() {
		return new ModuleResult<R, D>(ModuleState.SUSPEND, null, null);
	}

	public ModuleState getState() {
		return state;
	}

	public R getMessage() {
		return message;
	}

	public D getData() {
		return data;
	}

}
