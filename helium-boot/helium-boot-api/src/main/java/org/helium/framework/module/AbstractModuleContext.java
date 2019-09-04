package org.helium.framework.module;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Coral on 7/29/15.
 */
public class AbstractModuleContext implements ModuleContext {
	private boolean isTerminated;

	@Override
	public boolean isTerminated() {
		return isTerminated;
	}

	@Override
	public void setIsTerminated(boolean value) {
		isTerminated = value;
	}

	private Object lock = new Object();
	private Map<Object, Object> datas;
	
	@Override
	public void putModuleData(Object key, Object value) {
		synchronized (this) {
			if (datas == null) {
				datas = new HashMap<>();
			}
			datas.put(key, value);
		}
	}

	@Override
	public Object getModuleData(Object key) {
		synchronized (lock) {
			return datas.get(key);
		}
	}
}
