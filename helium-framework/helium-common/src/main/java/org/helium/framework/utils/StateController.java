package org.helium.framework.utils;

import org.helium.threading.Future;
import org.helium.threading.FutureListener;
import org.helium.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;


/**
 * Created by Coral on 7/25/15.
 */
public class StateController<E extends Enum> {
	private static final Logger LOGGER = LoggerFactory.getLogger(StateController.class);

	private E state;
	private String marker;
	private Exception lastError;

	public StateController(String marker, E initialState) {
		this.marker = marker;
		this.state = initialState;
		this.lastError = null;
	}

	public E getState() {
		return state;
	}

	public String getMarker() {
		return marker;
	}

	public Throwable getLastError() {
		return lastError;
	}

	public boolean canDo(StateAction<E> action) {
		return action.canDo(state);
	}

	public boolean doAction(StateAction<E> action, org.helium.util.Runnable runner) {
		synchronized (this) {
			if (!action.canDo(state)) {
				throw new RuntimeException("action:" + action + " not support state:" + state);
			}

			try {
				state = action.getRunningState();
				LOGGER.info("{} {} doing...", marker, action);
				runner.run();
				state = action.getSuccessState();
				LOGGER.info("{} {} done.", marker, action);
				lastError = null;
				return true;
			} catch (Exception ex) {
				state = action.getFailedState();
				String msg = String.format("%s %s failed!!! {}", marker, action);
				LOGGER.error(msg, ex);
				lastError = ex;
				return false;
			}
		}
	}

	public void doActionAsync(StateAction<E> action, Supplier<Future<Exception>> runner) {
		Future<Exception> future;
		synchronized (this) {
			if (!action.canDo(state)) {
				throw new RuntimeException("action:" + action + " not support state:" + state);
			}
			state = action.getRunningState();
			LOGGER.info("{} {} doing async...", marker, action);
			future = runner.get();
		}
		future.addListener(new FutureListener<Exception>() {
			@Override
			public void run(Result<Exception> result) {
				synchronized (this) {
					if (result.getError() != null || result.getValue() != null) {
						lastError = result.getError() != null ? result.getError() : result.getValue();
						String msg = String.format("%s %s failed!!! {}", marker, action);
						LOGGER.error(msg, lastError);
						state = action.getFailedState();
					} else {
						state = action.getSuccessState();
						LOGGER.info("{} {} done.", marker, action);
						lastError = null;
					}
				}
			}
		});
	}
}
