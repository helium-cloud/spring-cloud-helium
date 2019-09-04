package org.helium.logging.spi;

import org.helium.logging.SimpleMarker;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;

import java.util.HashMap;
import java.util.Map;

/**
 * 实现了一个简单的BarkerFactory
 *
 * @author Lv.Mingwei
 *
 */
public class SimpleMarkerFactory implements IMarkerFactory {
	private static final int MARKER_CAPACITY = 256;
	private Map<String, Marker> markers;

	public SimpleMarkerFactory() {
		markers = new HashMap<>();
	}

	public synchronized Marker getMarker(String name) {
		Marker marker = markers.get(name);
		if (marker == null) {
			marker = new SimpleMarker(name);
			markers.put(name, marker);
		}
		return marker;
	}

	public synchronized boolean exists(String name) {
		return markers.containsKey(name);
	}

	public synchronized boolean detachMarker(String name) {
		return markers.remove(name) != null;
	}

	public Marker getDetachedMarker(String name) {
		return new SimpleMarker(name);
	}
}
