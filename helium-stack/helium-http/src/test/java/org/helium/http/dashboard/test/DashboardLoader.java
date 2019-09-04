package org.helium.http.dashboard.test;

import org.helium.framework.spi.Bootstrap;

import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Coral on 10/1/15.
 */
public class DashboardLoader {
	public static void main(String[] args) throws Exception {
		// testLoadJar();
		Bootstrap.INSTANCE.addPath("helium-http/build/resources/test/META-INF");
		Bootstrap.INSTANCE.addPath("helium-dashboard/build/libs/");
		Bootstrap.INSTANCE.initialize("bootstrap-dashboard-test.xml");
		Bootstrap.INSTANCE.run();
	}

	public static void testLoadJar() throws Exception {
		JarFile jar = new JarFile("helium-dashboard/build/libs/helium-dashboard-2.1.7-SNAPSHOT.jar");
		JarEntry e = jar.getJarEntry("META-INF/bundle-console2.xml");
		if (e == null) {
			throw new IllegalArgumentException("FileNotFound in Jar");
		}
		InputStream input = jar.getInputStream(e);

		byte[] buffer = new byte[4096];
		while (true) {
			int len = input.read(buffer);
			if (len > 0) {
				System.out.print(new String(buffer, 0, len));
			} else {
				return;
			}
		}
	}
}
