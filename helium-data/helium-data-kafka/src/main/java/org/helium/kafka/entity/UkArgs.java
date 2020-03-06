package org.helium.kafka.entity;

import com.alibaba.fastjson.JSONObject;
import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

import java.util.UUID;

/**
 * UkArgs 用于
 */
public class UkArgs extends SuperPojo {

    /**
     * 平台名称
     */
    @Field(id = 1)
    private String platform = "ott";

    /**
     * 业务名称
     */
    @Field(id = 2)
    private String business = "im";

    /**
     * 业务名称
     */
    @Field(id = 3)
    private String type = "log";

    /**
     * 索引id
     */
    @Field(id = 4)
    private String uuid = null;


	/**
	 * 索引id
	 */
	@Field(id = 5)
	private String owner = null;

	/**
	 * 索引id
	 */
	@Field(id = 6)
	private String peer = null;

    /**
     * 记录时间
     */
    @Field(id = 7)
    private long time = System.currentTimeMillis();

    /**
     * 内容
     *
     */
    @Field(id = 8)
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


	public String getUuid() {
		if (uuid == null){
			UUID.randomUUID().toString();
		}
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String toJson(){
		String jsonContent = null;
		try {
			JSONObject jsonObject = JSONObject.parseObject(getContent());
			jsonObject.put("platform", getPlatform());
			jsonObject.put("business", getBusiness());
			jsonObject.put("time", getTime());
			jsonObject.put("type", getType());
			jsonObject.put("uuid", getUuid());
			jsonObject.put("owner", getOwner());
			jsonObject.put("peer", getPeer());
			jsonContent = jsonObject.toString();
		} catch (Exception e){
			jsonContent = toJsonObject().toString();
		}
		return jsonContent;
	}

	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString());
	}

}