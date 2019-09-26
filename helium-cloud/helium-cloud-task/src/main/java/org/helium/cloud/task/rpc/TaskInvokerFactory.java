package org.helium.cloud.task.rpc;

import org.apache.dubbo.common.extension.SPI;

public interface TaskInvokerFactory {
	TaskInvoker getInvoker();
}
