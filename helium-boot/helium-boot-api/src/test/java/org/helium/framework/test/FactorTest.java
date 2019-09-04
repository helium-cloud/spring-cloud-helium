package org.helium.framework.test;

import org.helium.framework.entitys.FactorGroupNode;
import org.helium.framework.entitys.FactorNode;
import org.helium.framework.module.ModuleContext;
import org.helium.framework.route.abtest.Factor;
import org.helium.framework.route.abtest.FactorFactory;

/**
 * Created by Lei Gao on 8/19/15.
 */
public class FactorTest {

	private static void testFactor(String xml, String value, boolean expect) {
		FactorGroupNode node = new FactorGroupNode();
		node.parseXmlFrom(xml);
		Factor factor = FactorFactory.createFrom(node);
		// System.out.println("loading factor from:" + xml);

		ModuleContext ctx = new ModuleContext() {
			@Override
			public boolean isTerminated() {
				return false;
			}

			@Override
			public void setIsTerminated(boolean value) {

			}

			@Override
			public void putModuleData(Object key, Object value) {

			}

			@Override
			public Object getModuleData(Object key) {
				return value;
			}
		};
		boolean result = factor.apply(ctx);
//		if (expect != result) {
//			System.out.printf("ERROR! value=%s expected %b, but %b\n", value, expect, result);
//			// throw new RuntimeException();
//		} else {
//			System.out.printf("BINGO! value=%s expected %b, and get %b\n", value,  expect, result);
//
//		}
	}

	private static String f1 = "<factors condition=\"or\">\n" +
			"\t<factor operator=\"equals\" value=\"customer\"/>\n" +
			"</factors>";

	private static String f2= "<factors condition=\"and\">\n" +
			"\t<factor operator=\"gt\" value=\"1000\"/>\n" +
			"\t<factor operator=\"lt\" value=\"2000\"/>\n" +
			"</factors>";

	private static String f3 = "<factors condition=\"or\">\n" +
			"\t<factor operator=\"regex\" value=\"customer.*\"/>\n" +
			"</factors>";

	public static void main(String[] args) {
//		testFactor(f1, "1234", true);
//		testFactor(f1, "1235", false);
//
//		testFactor(f2, "1000", true);
//		testFactor(f2, "1001", true);
//		testFactor(f2, "999", false);
//		testFactor(f2, "2000", false);
//		testFactor(f2, "2001", false);
//		testFactor(f2, "0", false);
//		testFactor(f2, "-1", false);
//
//		testFactor(f3, "13", true);
//		testFactor(f3, "223", true);
//		testFactor(f3, "122", false);
//		testFactor(f3, "10999", true);
		long start = System.currentTimeMillis();
		int value = 100000;
		System.out.println("start for reg count:" + value + " time:" + start);
		for (int i = 0; i < value; i ++) {
			testFactor(f3, "customer:1", true);
		}
		System.out.println("end use " + (System.currentTimeMillis() - start));



		start = System.currentTimeMillis();
		System.out.println("start for equals count:" + value + " time:" + start);
		for (int i = 0; i < value; i ++) {
			testFactor(f1, "customer", true);
		}
		System.out.println("end use " + (System.currentTimeMillis() - start));

	}
}
/*
<factors condition="or">
	<factor operator="equals" value="1234"/>
</factors>

<factors condition="and">
	<factor operator="gt" value="1000"/>
	<factor operator="lt" value="2000"/>
</factors>

<factors condition="or">
	<factor operator="regex" value=".*[13579]^"/>
	<factor operator="gt" value="10000"/>
</factors>
*/

