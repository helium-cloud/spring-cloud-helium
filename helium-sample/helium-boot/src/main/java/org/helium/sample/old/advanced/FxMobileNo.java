//package org.helium.sample.old.advanced;
//
//import com.feinno.superpojo.SuperPojo;
//import com.feinno.superpojo.annotation.Field;
//
///**
// * 飞信测的 MobileNo 封装版本. 为了区分RCS MobileNo
// *
// * @author Li.Hongbo <lihongbo@feinno.com>
// */
//public class FxMobileNo extends SuperPojo {
//	public static final FxMobileNo ZEROVALUE = new FxMobileNo(0);
//
//	@Field(id = 1)
//	private long mobileNo;
//
//	public long longValue() {
//		return mobileNo;
//	}
//
//	public String strValue() {
//		if (mobileNo > 0) {
//			return "+86" + Long.toString(mobileNo);
//		} else if (mobileNo == 0) {
//			return "";
//		} else {
//			// remove "-900" add "+"
//			return "+" + Long.toString(- mobileNo).substring(3);
//		}
//	}
//
//
//	public FxMobileNo() {
//		this.mobileNo = 0;
//	}
//
//	public FxMobileNo(long no) {
//		check(no);
//		mobileNo = no;
//	}
//
//	public void setMobileNo(long mobile) {
//		check(mobile);
//		mobileNo = mobile;
//	}
//
//	public long getMobileNo() {
//		return mobileNo;
//	}
//
//	public FxMobileNo(String str) {
//		long no = 0;
//		if (str.startsWith("-")) {
//			throw new RuntimeException("Unexcepted MobileNo: " + str);
//		}
//
//		if (str.startsWith("+86")) {
//			no = Long.parseLong(str.substring(3));
//		} else if (str.startsWith("0086")) {
//			no = Long.parseLong(str.substring(4));
//		} else if (str.startsWith("86")) {
//			no = Long.parseLong(str.substring(2));
//		} else if (str.startsWith("12520")) {
//			no = Long.parseLong(str.substring(5));
//		} else if (str.startsWith("17951")) {
//			no = Long.parseLong(str.substring(5));
//		} else if (str.startsWith("12593")) {
//			no = Long.parseLong(str.substring(5));
//		} else if (str.startsWith("852")) {
//			no = Long.parseLong("-900" + str);
//		} else if (str.startsWith("00")) {
//			no = Long.parseLong("-9" + str);
//		} else if (str.startsWith("65")) {
//			no = Long.parseLong("-900" + str);
//		} else if (str.startsWith("92")) {
//			no = Long.parseLong("-900" + str);
//			//add by Fred for VINPHONE
//		} else if (str.startsWith("84")) {
//			no = Long.parseLong("-900" + str);
//		} else if (str.startsWith("0084")) {
//			no = Long.parseLong("-9" + str.substring(1));
//		} else if (str.startsWith("900")) {
//			no = - Long.parseLong(str);
//		} else {
//			no = Long.parseLong(str);
//		}
//		check(no);
//		mobileNo = no;
//	}
//
//	public static FxMobileNo parse(long no) {
//		return new FxMobileNo(no);
//	}
//
//	public static FxMobileNo parse(String str) {
//		return new FxMobileNo(str);
//	}
//
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!(obj instanceof FxMobileNo))
//			return false;
//		FxMobileNo target = (FxMobileNo) obj;
//		return mobileNo == target.mobileNo;
//	}
//
//	public int hashCode() {
//		return ((int) mobileNo) ^ ((int) (mobileNo >> 32));
//	}
//
//	public static void check(long no) {
//		if (!isMobileNo(no)) {
//			throw new RuntimeException("Unexcepted MobileNo: " + no);
//		}
//	}
//
//	public static boolean isMobileNo(long no) {
//		if (no >= +10000000000L && no <= +99999999999L) {
//			return true;
//		} else if (no < 0) {
//			//for singtel 90000000000000L ->9000000000000L 因为手机号是十位，去掉一个0.
//			return true;
//		} else if (no == 0) {
//			return true;
//		} else if (no >= 9008520000000L && no <= 9008529999999L) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public static boolean isNullOrEmpty(FxMobileNo mobile) {
//		return mobile == null || mobile.mobileNo == 0;
//	}
//
//	@Override
//	public String toString() {
//		return strValue();
//	}
//}
