package org.helium.test.superpojo;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.SuperPojoManager;
import org.helium.test.superpojo.bean.DataCreater;
import org.helium.test.superpojo.bean.Table;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

public class PerformanceTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PerformanceTester tester = new PerformanceTester();
		tester.testPerformance();
	}

	public void testPerformance() {
		int count = 1000;
		count = count * 100;
		// count = count * 10000 * 10;
		Table table = DataCreater.newTable(true);
		SuperPojoManager.getSuperPojoBuilder(table);
		System.out.println("count : " + count);
		System.out.println("testSerializerPb     : " + testSerializerPb(table, count));
		System.out.println("testSerializerXml    : " + testSerializerXml(table, count));
		System.out.println("testSerializerJson   : " + testSerializerJson(table, count));
		System.out.println("testDeserializerPb   : " + testDeserializerPb(table, count));
		System.out.println("testDeserializerXml  : " + testDeserializerXml(table, count));
		System.out.println("testDeserializerJson : " + testDeserializerJson(table, count));
	}

	public long testSerializerPb(final SuperPojo superPojo, int count) {
		Action action = new Action() {
			@Override
			public void run() {
				superPojo.toPbByteArray();
			}
		};
		return test(action, count);
	}

	public long testSerializerJson(final SuperPojo superPojo, int count) {
		Action action = new Action() {
			@Override
			public void run() {
				superPojo.toJsonObject();
			}
		};
		return test(action, count);
	}

	public long testSerializerXml(final SuperPojo superPojo, int count) {
		Action action = new Action() {
			@Override
			public void run() {
				superPojo.toXmlByteArray();
			}
		};
		return test(action, count);
	}

	public long testDeserializerPb(final SuperPojo superPojo, int count) {
		final byte[] buffer = superPojo.toPbByteArray();
		Action action = new Action() {
			@Override
			public void run() {
				superPojo.parsePbFrom(buffer);
			}
		};
		return test(action, count);
	}

	public long testDeserializerXml(final SuperPojo superPojo, int count) {
		final byte[] buffer = superPojo.toXmlByteArray();
		Action action = new Action() {
			@Override
			public void run() {
				superPojo.parseXmlFrom(new ByteArrayInputStream(buffer));
			}
		};
		return test(action, count);
	}

	public <T extends SuperPojo> long testDeserializerJson(final T t, int count) {
		final String json = t.toJsonObject().toString();
		Action action = new Action() {
			@Override
			public void run() {
				SuperPojoManager.parseJsonFrom(json, t.getClass());
			}
		};
		return test(action, count);
	}

	public static long test(Action action, int count) {
		long startTime = System.nanoTime();
		for (int i = 0; i < count; i++) {
			action.run();
		}
		long endTime = System.nanoTime();
		return TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
	}

	public static interface Action {
		public void run();
	}
}
