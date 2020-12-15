import io.netty.handler.codec.http.HttpMethod;
import org.helium.http.client.HttpClient;
import org.helium.http.client.HttpClientRequest;
import org.helium.http.client.HttpClientResponse;
import org.helium.threading.Future;

public class HttpClientTest {
	public static void main(String[] args) {

		// false: page mode, true: large mode
		HttpClient c = new HttpClient();

		HttpClientRequest req = HttpClient.createHttpRequest(HttpMethod.GET.toString(), "http://10.10.208.198:8888/", false);
		try {
			Future<HttpClientResponse> f = c.sendData(req);
			f.addListener(result -> {
				System.out.print(result.getValue().toString());
				System.out.print("\r\n");
			});
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpClientRequest req1 = HttpClient.createHttpRequest(HttpMethod.GET.toString(), "http://10.10.208.198:8888/", false);
		try {
			Future<HttpClientResponse> f = c.sendData(req1);
			f.addListener(result -> {
				System.out.print(result.getValue().toString());
				System.out.print("\r\n");
			});
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
