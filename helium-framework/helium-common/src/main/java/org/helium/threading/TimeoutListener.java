/*
 * 
 * Author(s):
 * linsu (linsu@feinno.com)
 */

package org.helium.threading;

/** Listens for a Timer events. */
public interface TimeoutListener {
	/** When the Timer exceeds. */
	public void onTimeout(Timeout timeout);
}
