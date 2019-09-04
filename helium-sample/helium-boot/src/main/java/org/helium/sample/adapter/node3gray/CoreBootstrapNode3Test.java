package org.helium.sample.adapter.node3gray;

import org.helium.framework.annotations.AdapterTag;
import org.helium.framework.spi.Bootstrap;

/**
 * Created by Coral on 9/10/16.
 */
@AdapterTag(name ="grayrouter")
public class CoreBootstrapNode3Test {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample/src/main/java/org/helium/sample/adapter/node3gray");
		Bootstrap.INSTANCE.initialize("bootstrap-core.xml");
	}
}
