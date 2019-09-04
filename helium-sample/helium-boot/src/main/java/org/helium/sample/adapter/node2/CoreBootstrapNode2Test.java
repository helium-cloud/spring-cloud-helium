package org.helium.sample.adapter.node2;

import org.helium.framework.annotations.AdapterTag;
import org.helium.framework.spi.Bootstrap;

/**
 * Created by Coral on 9/10/16.
 */
@AdapterTag(name ="grayrouter")
public class CoreBootstrapNode2Test {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample/src/main/java/org/helium/sample/adapter/consumer");
		Bootstrap.INSTANCE.initialize("bootstrap-core.xml");
	}
}
