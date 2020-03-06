package org.helium.framework;

import org.helium.framework.utils.StateAction;

import static org.helium.framework.BeanContextState.*;

/**
 * Created by Coral on 7/25/15.
 */
public enum BeanContextAction implements StateAction<BeanContextState> {
	RESOLVE(RESOLVING, RESOLVED, RESOLVE_FAILED, new BeanContextState[] {INITIAL}),
	REGISTER(REGISTERING, REGISTERED, REGISTER_FAILED, new BeanContextState[]{RESOLVED}),
	ASSEMBLE(ASSEMBLING, ASSEMBLED, ASSEMBLE_FAILED, new BeanContextState[] {REGISTERED, ASSEMBLE_FAILED}),
	START(STARTING, STARTED, START_FAILED, new BeanContextState[] {ASSEMBLED, START_FAILED}),
	STOP(STOPPING, STOPPED, STOP_FAILED, new BeanContextState[] {STARTED}),
	;
	private BeanContextState running;
	private BeanContextState success;
	private BeanContextState failed;
	private BeanContextState[] allowStates;

	BeanContextAction(BeanContextState running, BeanContextState success, BeanContextState failed, BeanContextState[] allowStates) {
		this.running = running;
		this.success = success;
		this.failed = failed;
		this.allowStates = allowStates;
	}

	@Override
	public boolean canDo(BeanContextState state) {
		for (BeanContextState before: allowStates) {
			if (before == state) {
				return true;
			}
		}
		return false;
	}

	@Override
	public BeanContextState getRunningState() {
		return running;
	}

	@Override
	public BeanContextState getSuccessState() {
		return success;
	}

	@Override
	public BeanContextState getFailedState() {
		return failed;
	}
}
