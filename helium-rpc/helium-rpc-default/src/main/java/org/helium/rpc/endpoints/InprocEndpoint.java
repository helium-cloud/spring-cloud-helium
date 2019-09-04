package org.helium.rpc.endpoints;


/**
 * 进程内Endpoint, 用于进程内调用, 在同一个进程内
 * Created by Coral
 */
public final class InprocEndpoint extends ServiceEndpoint {
	public static final InprocEndpoint INSTANCE = new InprocEndpoint();

	public static final String PROTOCOL = "inproc";

	@Override
	public String getProtocol() {
		return PROTOCOL;
	}

	/**
	 * SINGLETON
	 */
	private InprocEndpoint() {
	}

	@Override
	public String getValue() {
		return "";
	}
}
