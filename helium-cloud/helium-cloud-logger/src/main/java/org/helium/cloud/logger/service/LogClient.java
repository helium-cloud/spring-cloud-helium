package org.helium.cloud.logger.service;

import org.helium.logging.args.LogArgs;

public interface LogClient {
	void log(LogArgs logArgs);
}
