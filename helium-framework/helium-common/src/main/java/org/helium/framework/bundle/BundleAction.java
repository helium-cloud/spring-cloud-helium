package org.helium.framework.bundle;

import org.helium.framework.utils.StateAction;

import static org.helium.framework.bundle.BundleState.*;

/**
 * Created by Coral on 7/25/15.
 */
public enum BundleAction implements StateAction<BundleState> {
	INSTALL(INSTALLING, INSTALLED, INSTALL_FAILED, new BundleState[] {INITIAL}),
	RESOLVE(RESOLVING, RESOLVED, RESOLVE_FAILED, new BundleState[] {INSTALLED, RESOLVE_FAILED}),
	REGISTER(REGISTERING, REGISTERED, REGISTER_FAILED, new BundleState[] {RESOLVED}),
	ASSEMBLE(ASSEMBLING, ASSEMBLED, ASSEMBLE_FAILED, new BundleState[] {REGISTERED}),
	START(STARTING, STARTED, START_FAILED, new BundleState[] {INSTALLED, RESOLVED, ASSEMBLED, START_FAILED}),
	STOP(STOPPING, STOPPED, STOP_FAILED, new BundleState[] {STARTED}),
	UNINSTALL(UNINSTALLING, UNINSTALLED, UNINSTALL_FAILED,
			new BundleState[] {INSTALLED, INSTALL_FAILED, RESOLVED, RESOLVE_FAILED, REGISTERED, REGISTER_FAILED,
					ASSEMBLED, ASSEMBLE_FAILED, START_FAILED, STOPPED, STOP_FAILED}),
	;
	private BundleState running;
	private BundleState success;
	private BundleState failed;
	private BundleState[] prerequisites;

	BundleAction(BundleState running, BundleState success, BundleState failed, BundleState[] prerequisites) {
		this.running = running;
		this.success = success;
		this.failed = failed;
		this.prerequisites = prerequisites;
	}

	@Override
	public boolean canDo(BundleState state) {
		for (BundleState before : prerequisites) {
			if (before == state) {
				return true;
			}
		}
		return false;
	}

	@Override
	public BundleState getRunningState() {
		return running;
	}

	@Override
	public BundleState getSuccessState() {
		return success;
	}

	@Override
	public BundleState getFailedState() {
		return failed;
	}
}
