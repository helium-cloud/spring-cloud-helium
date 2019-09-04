package org.helium.sample.adapter.common;
import org.helium.framework.module.ModuleContext;

import java.util.HashMap;
import java.util.Map;

public class AdapterModuleContext implements ModuleContext {
	private Map<Object, Object> datas = new HashMap<>();
	@Override
	public boolean isTerminated() {
		return false;
	}

	@Override
	public void setIsTerminated(boolean value) {

	}

	@Override
	public void putModuleData(Object key, Object value) {
		datas.put(key, value);
	}

	@Override
	public Object getModuleData(Object key) {
		return datas.get(key);
	}
}
