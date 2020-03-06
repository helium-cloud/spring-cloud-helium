/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2012-2-15
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

import java.util.List;

/**
 * Rpc应答protobuf实体类
 * <hr>
 * <p/>
 * <code>
 * message RpcResponseHeader {<br>
 * required int32 Sequence = 1;       // 事务序号, 客户端生成, 活动事务唯一，可复用<br>
 * required int32 ResponseCode = 2;   // 返回码<br>
 * required int32 BodyLength = 3;     // 包体长度: 0代表传空，1代表全默认，len-1为实际长度<br>
 * optional int32 Option = 4;         // 消息可选项<br>
 * optional int32 ToId = 5;           // 用于优化消息长度的交换后id, 针对服务器端为单服务器上有效<br>
 * optional int32 FromId = 6;         // 用于优化消息长度的交换后id, 针对服务器端为单服务器上有效<br>
 * }<br>
 * </code>
 * <p>
 * Created by Coral
 */
public class RpcResponseHeader extends SuperPojo {
	@Field(id = 1, isRequired = true)
	private int sequence;

	@Field(id = 2, isRequired = true)
	private int responseCode;

	@Field(id = 3, isRequired = true)
	private int bodyLength;

	@Field(id = 4)
	private int option;

	@Field(id = 5)
	private int toId;

	@Field(id = 6)
	private int fromId;

	@Field(id = 7)
	private List<RpcBodyExtension> extensions;

	@Field(id = 12, isRequired = false)
	private String codecName;

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responceCode) {
		this.responseCode = responceCode;
	}

	public int getBodyLength() {
		return bodyLength;
	}

	public void setBodyLength(int bodyLength) {
		this.bodyLength = bodyLength;
	}

	public int getOption() {
		return option;
	}

	public void setOption(int option) {
		this.option = option;
	}

	public int getToId() {
		return toId;
	}

	public void setToId(int toId) {
		this.toId = toId;
	}

	public int getFromId() {
		return fromId;
	}

	public void setFromId(int fromId) {
		this.fromId = fromId;
	}

	public void setExtensions(List<RpcBodyExtension> extensions) {
		this.extensions = extensions;
	}

	public List<RpcBodyExtension> getExtensions() {
		return extensions;
	}

	public String getCodecName() {
		return codecName;
	}

	public void setCodecName(String codecName) {
		this.codecName = codecName;
	}
}
