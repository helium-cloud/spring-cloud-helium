package org.helium.rpc.channel;

import java.util.Arrays;

/**
 * Created by Coral on 2015/5/7.
 */
public class RpcMessageUtils {

	public static String dumpRpcMessageBody(RpcMessage message, String title) {
		StringBuilder sb = new StringBuilder();
		sb.append(title);
		sb.append(" = ");
		if (message.getBody().getValue() != null) {
			sb.append(message.getBody().getValue());
		} else if (message.getBody().getBuffer() != null) {
			sb.append(Arrays.toString(message.getBody().getBuffer()));
		} else {
			try {
				message.getBody().getError();
			} catch (Exception e) {
				sb.append(e.getMessage());
			}
		}
		return sb.toString();
	}
}
