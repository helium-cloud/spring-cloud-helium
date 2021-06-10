
package org.helium.common.status.support;

import org.helium.common.extension.Activate;
import org.helium.common.status.Status;
import org.helium.common.status.StatusChecker;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;

/**
 * Load Status
 */
@Activate
public class LoadStatusChecker implements StatusChecker {

    @Override
    public Status check() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        double load;
        try {
            Method method = OperatingSystemMXBean.class.getMethod("getSystemLoadAverage", new Class<?>[0]);
            load = (Double) method.invoke(operatingSystemMXBean, new Object[0]);
            if (load == -1) {
                com.sun.management.OperatingSystemMXBean bean =
                        (com.sun.management.OperatingSystemMXBean) operatingSystemMXBean;
                load = bean.getSystemCpuLoad();
            }
        } catch (Throwable e) {
            load = -1;
        }
        int cpu = operatingSystemMXBean.getAvailableProcessors();
        return new Status(load < 0 ? Status.Level.UNKNOWN : (load < cpu ? Status.Level.OK : Status.Level.WARN),
                (load < 0 ? "" : "load:" + load + ",") + "cpu:" + cpu);
    }

}
