package org.helium.perfmon.recoder;


import org.helium.common.extension.ExtensionLoader;
import org.helium.common.utils.ConfigUtils;
import org.helium.perfmon.h2.CounterStorageH2;
import org.helium.perfmon.observation.Observable;

/**
 * Created by Coral on 11/11/15.
 */
public class CounterStorageFactory {
	public static final String STORAGE_TYPE =  "STORAGE_TYPE";
	public volatile static ICounterStorage iCounterStorage = null;
	public static ICounterStorage getStorage(Observable ob, String dateFormat, String tableNameFormat){
		if (iCounterStorage == null){
			synchronized (STORAGE_TYPE){
				iCounterStorage = ExtensionLoader.loadOrByDefaultFactory(ICounterStorage.class,
						ConfigUtils.getProperty(STORAGE_TYPE, "h2"), () -> new CounterStorageH2());
				iCounterStorage.setEnv(ob, dateFormat, tableNameFormat);
			}

		}
		return iCounterStorage;
	}
}
