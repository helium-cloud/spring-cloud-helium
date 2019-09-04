package org.helium.logging.spi;

import org.helium.logging.LoggingConfiguration.FilterNode;
import org.helium.logging.MarkerFilter;
import org.slf4j.Marker;

/**
 * Created by Coral on 9/1/15.
 */
class LogFilter {
	MarkerFilter filter;
	LogOutput output;

	public LogFilter(MarkerFilter filter) {
		this.filter = filter;
	}

	public LogFilter(FilterNode node) {
		filter = (MarkerFilter) ObjectCreator.createObject(node.getClazz(), node.getParams(), node.getSetters());
	}

	public MarkerTag apply(Marker marker) {
		MarkerTag tag = new MarkerTag();
		tag.result = filter.filter(marker);
		tag.marker = marker;
		tag.output = output;
		return tag;
	}
}
