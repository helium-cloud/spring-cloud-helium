/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 8, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel.tcp;


import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

/**
 * Created by Coral
 */
@PerformanceCounterCategory("rpc:tcp-channel")
public class RpcTcpPerformanceCounter {

	@PerformanceCounter(name = "request", type = PerformanceCounterType.THROUGHPUT)
	private SmartCounter request;

	@PerformanceCounter(name = "response", type = PerformanceCounterType.THROUGHPUT)
	private SmartCounter response;

	@PerformanceCounter(name = "connections", type = PerformanceCounterType.NUMBER)
	private SmartCounter connections;

	@PerformanceCounter(name = "connect pendings", type = PerformanceCounterType.NUMBER)
	private SmartCounter connectPendings;

	@PerformanceCounter(name = "send pendings", type = PerformanceCounterType.NUMBER)
	private SmartCounter sendPendings;

	@PerformanceCounter(name = "errors", type = PerformanceCounterType.NUMBER)
	private SmartCounter errors;

	public SmartCounter getRequest() {
		return request;
	}

	public void setRequest(SmartCounter request) {
		this.request = request;
	}

	public SmartCounter getResponse() {
		return response;
	}

	public void setResponse(SmartCounter response) {
		this.response = response;
	}

	public SmartCounter getConnections() {
		return connections;
	}

	public void setConnections(SmartCounter connections) {
		this.connections = connections;
	}

	public SmartCounter getConnectPendings() {
		return connectPendings;
	}

	public void getConnectPendings(SmartCounter pendingConnections) {
		this.connectPendings = pendingConnections;
	}

	public SmartCounter getErrors() {
		return errors;
	}

	public void setErrors(SmartCounter errors) {
		this.errors = errors;
	}

	public SmartCounter getSendPendings() {
		return sendPendings;
	}

	public void setSendPendings(SmartCounter sendPendings) {
		this.sendPendings = sendPendings;
	}
}
