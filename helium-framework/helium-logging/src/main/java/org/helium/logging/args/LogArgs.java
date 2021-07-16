package org.helium.logging.args;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.helium.superpojo.SuperPojo;
import org.helium.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LogArgs extends SuperPojo {
	private static final int OFFSET = 20;

	/**
	 * 业务名称(服务名)
	 */
	private String business;

	/**
	 * 子业务类型
	 */
	private String type;

	/**
	 * 事务ID（消息ID、通话的sessionid）
	 */
	private String tid;


	/**
	 * 主要操作方【】
	 */
	private String owner;

	/**
	 * 被操作方【】
	 */

	private String peer;

	/**
	 * 记录时间
	 */

	private long time = System.currentTimeMillis();
	/**
	 * 耗时
	 */

	private long costNano;

	/**
	 * 结果
	 */

	private String result ;

	/**
	 * 本机IP、地址
	 */

	private String localAddr;

	/**
	 * 远端的IP、地址
	 */

	private String peerAddr;

	/**
	 * 扩展消息内容
	 */

	private String request;


	private String response;



	private String content;

	/**
	 * 扩展消息内容
	 */

	private List<LogExt> extContent = new ArrayList<>();


	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPeer() {
		return peer;
	}

	public void setPeer(String peer) {
		this.peer = peer;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getCostNano() {
		return costNano;
	}

	public void setCostNano(long costNano) {
		this.costNano = costNano;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getLocalAddr() {
		return localAddr;
	}

	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}

	public String getPeerAddr() {
		return peerAddr;
	}

	public void setPeerAddr(String peerAddr) {
		this.peerAddr = peerAddr;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<LogExt> getExtContent() {
		return extContent;
	}

	public void setExtContent(List<LogExt> extContent) {
		this.extContent = extContent;
	}

	public String toJson() throws JsonProcessingException {
		return toJsonString();
	}

	public static LogArgs create(String tid, String req, String resp){
		LogArgs logArgs = new LogArgs();
		logArgs.setTid(tid);
		logArgs.setBusiness("urcs");
		logArgs.setRequest(req);
		logArgs.setResponse(resp);
		logArgs.setLocalAddr(NetworkUtils.getLocalIp());
		logArgs.getExtContent().add(new LogExt("server", "im"));
		return logArgs;
	}

	public static LogArgs createSimple(String tid, String owner, String content){
		LogArgs logArgs = new LogArgs();
		logArgs.setTid(tid);
		logArgs.setOwner(owner);
		logArgs.setBusiness("urcs");
		logArgs.setRequest(content);
		logArgs.setLocalAddr(NetworkUtils.getLocalIp());
		return logArgs;
	}


	public static void main(String[] args) throws JsonProcessingException {
		LogArgs logArgs = LogArgs.create(UUID.randomUUID().toString(), "MESSAGE ", "RESPONSE");
		System.out.println(logArgs.toJson());
		LogArgs logArgsSimple = LogArgs.createSimple(UUID.randomUUID().toString(), "张三", "参加");
		System.out.println(logArgsSimple.toJson());
	}
}
