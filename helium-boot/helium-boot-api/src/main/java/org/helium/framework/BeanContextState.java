package org.helium.framework;

/**
 * Created by Coral on 7/4/15.
 */
public enum BeanContextState {
	INITIAL,
	RESOLVING,
	RESOLVED,
	RESOLVE_FAILED,
	REGISTERING,
	REGISTERED,
	REGISTER_FAILED,
	ASSEMBLING,
	ASSEMBLED,
	ASSEMBLE_FAILED,
	STARTING,
	STARTED,
	START_FAILED,
	STOPPING,
	STOPPED,
	STOP_FAILED,
	;

	@Override
	public String toString() {
		return this.name();
	}
}
