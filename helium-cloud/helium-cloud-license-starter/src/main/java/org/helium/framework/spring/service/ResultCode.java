package org.helium.framework.spring.service;

public class ResultCode {
	private int code;
	private String msg;
	private String lic;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getLic() {
		return lic;
	}

	public void setLic(String lic) {
		this.lic = lic;
	}

	public static ResultCode OK(){
		ResultCode resultCode = new ResultCode();
		resultCode.code = 200;
		resultCode.msg = "OK";
		return resultCode;
	}
	public static ResultCode OK(String lic){
		ResultCode resultCode = new ResultCode();
		resultCode.code = 200;
		resultCode.msg = "OK";
		resultCode.lic = lic;
		return resultCode;
	}
	public static ResultCode ERROR(){
		ResultCode resultCode = new ResultCode();
		resultCode.code = 400;
		resultCode.msg = "ERROR";
		return resultCode;
	}
}
