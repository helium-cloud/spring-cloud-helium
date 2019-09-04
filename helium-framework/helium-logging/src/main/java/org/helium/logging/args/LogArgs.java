package org.helium.logging.args;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

import java.util.ArrayList;
import java.util.List;

public class LogArgs extends SuperPojo{
	private static final int OFFSET = 20;
	/**
	 * 平台名称(URCS)
	 */
	@Field(id = 1)
	private String platform;

	/**
	 * 业务名称(服务名)
	 */
	@Field(id = 2)
	private String business;

	/**
	 * 子业务类型
	 */
	@Field(id = 3)
	private String type;

	/**
	 * 事务ID（消息ID、通话的sessionid、）
	 */
	@Field(id = 4)
	private String tid = null;


	/**
	 * 主要操作方【】
	 */
	@Field(id = 5)
	private String owner = null;

	/**
	 * 被操作方【】
	 */
	@Field(id = 6)
	private String peer = null;

	/**
	 * 记录时间
	 */
	@Field(id = 7)
	private long time = System.currentTimeMillis();
	/**
	 * 耗时
	 */
	@Field(id = 8)
	private long costNano;

	/**
	 * 结果
	 */
	@Field(id = 9)
	private String result;

	/**
	 * 本机IP、地址
	 */
	@Field(id = 10)
	private String ownerAddress = null;

	/**
	 * 远端的IP、地址
	 */
	@Field(id = 11)
	private String peerAddress = null;

	/**
	 * 扩展消息内容
	 */
	@Field(id = 12)
	private List<LogExt> extContent = new ArrayList<>();

	/**
	 * 扩展消息内容
	 */
	@Field(id = 14)
	private String content;

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

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

	public String getOwnerAddress() {
		return ownerAddress;
	}

	public void setOwnerAddress(String ownerAddress) {
		this.ownerAddress = ownerAddress;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public List<LogExt> getExtContent() {
		return extContent;
	}

	public void setExtContent(List<LogExt> extContent) {
		this.extContent = extContent;
	}

	public void addLogExt(LogExt logExt) {
		 extContent.add(logExt);
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public static void main(String[] args) {
		LogArgs logArgs = new LogArgs();
		logArgs.getExtContent().add(new LogExt("111", "11"));
		System.out.println(logArgs.toJsonObject());
	}
}
