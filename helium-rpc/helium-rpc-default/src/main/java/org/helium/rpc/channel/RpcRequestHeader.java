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
 * 请求Protobuf序列化实体类
 * <hr>
 * <p>
 * <code>
 * message RpcRequestHeader { <br>
 * required int32 FromId = 1;         // 交换后使用缩写的from代替service@computer, 0表示未交换, 交换数据从1开始<br>
 * required int32 ToId = 2;           // 交换后用id代替service.method, 0表示未交换, 交换数据从1开始<br>
 * required int32 Sequence = 3;       // 事务序号, 客户端生成, 如果不复用连接，则都为0<br>
 * required int32 BodyLength = 4;     // 包体长度: 0代表传空，1代表全默认，len-1为实际长度<br>
 * optional int32 Option = 5;         // 消息可选项<br>
 * optional string ContextUri = 6;    // 上下文标识<br>
 * optional string FromComputer = 7;  // 来源计算机<br>
 * optional string FromService = 8;   // 来源服务    <br>
 * optional string ToService = 9;     // 访问服务    <br>
 * optional string ToMethod = 10;     // 访问方法名  <br>
 * repeated RpcRequestExtension = 11; // 扩展字段<br>
 * }<br>
 * </code>
 * <p>
 * Created by Coral
 */
public class RpcRequestHeader extends SuperPojo {
	@Field(id = 1, isRequired = true)
	private int fromId;

	@Field(id = 2, isRequired = true)
	private int toId;

	@Field(id = 3, isRequired = true)
	private int sequence;

	@Field(id = 4, isRequired = true)
	private int bodyLength;

	@Field(id = 5, isRequired = false)
	private int option;

	@Field(id = 6, isRequired = false)
	private String contextUri;

	@Field(id = 7, isRequired = false)
	private String fromComputer;

	@Field(id = 8, isRequired = false)
	private String fromService;

	@Field(id = 9, isRequired = false)
	private String toService;

	@Field(id = 10, isRequired = false)
	private String toMethod;

	@Field(id = 11, isRequired = false)
	private List<RpcBodyExtension> extensions;

	@Field(id = 12, isRequired = false)
	private String codecName;

	public int getFromId() {
		return fromId;
	}

	public void setFromId(int fromId) {
		this.fromId = fromId;
	}

	public int getToId() {
		return toId;
	}

	public void setToId(int toId) {
		this.toId = toId;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
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

	public String getContextUri() {
		return contextUri;
	}

	public void setContextUri(String contextUri) {
		this.contextUri = contextUri;
	}

	public String getFromComputer() {
		return fromComputer;
	}

	public void setFromComputer(String fromComputer) {
		this.fromComputer = fromComputer;
	}

	public String getFromService() {
		return fromService;
	}

	public void setFromService(String fromService) {
		this.fromService = fromService;
	}

	public String getToService() {
		return toService;
	}

	public void setToService(String toService) {
		this.toService = toService;
	}

	public String getToMethod() {
		return toMethod;
	}

	public void setToMethod(String toMethod) {
		this.toMethod = toMethod;
	}

	public List<RpcBodyExtension> getExtensions() {
		return extensions;
	}

	public void setExtensions(List<RpcBodyExtension> extensions) {
		this.extensions = extensions;
	}

	public String getCodecName() {
		return codecName;
	}

	public void setCodecName(String codecName) {
		this.codecName = codecName;
	}
}
