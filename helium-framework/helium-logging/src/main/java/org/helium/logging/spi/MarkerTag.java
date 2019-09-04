package org.helium.logging.spi;


import org.helium.logging.FilterResult;
import org.slf4j.Marker;

/**
 * Created by Coral on 9/2/15.
 */
class MarkerTag {
	FilterResult result;
	Marker marker;
	LogOutput output;
}
