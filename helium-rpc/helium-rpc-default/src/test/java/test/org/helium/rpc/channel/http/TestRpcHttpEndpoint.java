package test.org.helium.rpc.channel.http;//package test.org.helium.rpc.channel.http;
//
//import java.net.InetSocketAddress;
//
//import org.junit.Test;
//
//import org.helium.perfmon.monitor.ObservationHandler;
//import org.helium.perfmon.monitor.ParameterFilter;
//import org.helium.perfmon.monitor.PullManager;
//import org.helium.rpc.RpcProxyFactory;
//import org.helium.rpc.channel.http.RpcHttpEndpoint;
//
//public class TestRpcHttpEndpoint {
//
//	@Test
//	public void test() {
//		try {
//			RpcProxyFactory.getProxy("http://127.0.0.1:8080", "test");
//		} catch (Exception e) {
//		}
//
//		RpcHttpEndpoint testEndpoint = new RpcHttpEndpoint(new InetSocketAddress("127.0.0.1", 8080));
//		testEndpoint.getAddress();
//		testEndpoint.getClientChannel();
//		testEndpoint.getInetSocketAddress();
//		testEndpoint.getPort();
//		testEndpoint.getProtocol();
//		RpcHttpEndpoint.parse("http://127.0.0.1:8080");
//		RpcHttpEndpoint.parse("http:127.0.0.1:8080");
//		testEndpoint.equals(testEndpoint);
//		testEndpoint.equals(null);
//		testEndpoint.equals("");
//		String string = null;
//		testEndpoint.equals(new RpcHttpEndpoint(new InetSocketAddress("127.0.0.2", 8080)));
//		testEndpoint.equals(new RpcHttpEndpoint(new InetSocketAddress("127.0.0.1", 8081)));
//		testEndpoint.equals(new RpcHttpEndpoint(new InetSocketAddress("127.0.0.1", 8080)));
//		testEndpoint.hashCode();
//		testEndpoint.toString();
//		try {
//			RpcHttpEndpoint.parse("127.0.0.1:8080");
//		} catch (Exception e) {
//		}
//		try {
//			testEndpoint.equals(new RpcHttpEndpoint(new InetSocketAddress(string, 8080)));
//		} catch (Exception e) {
//		}
//	}
//
//	@Test
//	public void test2() {
//		PullManager pullManager = PullManager.getInstance("Test", true);
//		pullManager.isActive();
//		pullManager.pull();
//		pullManager.close();
//		PullManager.removeInstance("Test");
//		new ObservationHandler().inspector();
//		new ParameterFilter().description();
//	}
//
//}
