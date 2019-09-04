package org.helium.logging;

import org.slf4j.Marker;

/**
 * 用于日志系统中处理Marker
 * Created by Coral on 8/31/15.
 */
public interface MarkerFilter {
	FilterResult filter(Marker marker);
}
