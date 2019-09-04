package org.helium.logging.spi;

import org.helium.logging.FilterResult;
import org.helium.logging.MarkerFilter;
import org.slf4j.Marker;

/**
 * Created by Coral on 9/10/15.
 */
public class SimpleMarkerFilter implements MarkerFilter {
	private String name;

	public SimpleMarkerFilter() {
	}

	public SimpleMarkerFilter(String name) {
		this.name = name;
	}

	@Override
	public FilterResult filter(Marker marker) {
		if (name == null) {
			return FilterResult.NEUTRAL;
		}
		if (marker.contains(name)) {
			return FilterResult.ACCEPT;
		} else {
			return FilterResult.NEUTRAL;
		}
	}
}
