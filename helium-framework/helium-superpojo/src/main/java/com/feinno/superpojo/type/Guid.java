package com.feinno.superpojo.type;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.util.StringUtils;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

/**
 * 
 * <b>描述: </b>为与C#通讯时的Guid提供方便而提供此类，它支持和C#的Guid的序列化和反序列化。 toStr输出也和C#一致。
 * <p>
 * <b>功能: </b>它支持和C#的Guid的序列化和反序列化。 toStr输出也和C#一致。
 * <p>
 * <b>用法: </b>与标准的ProtoEntity的protobuf序列化和反序列化相同，详见{@link ProtoEntity}、
 * {@link ProtoManager}
 * <p>
 * 
 * @author lichunlei
 * 
 */
public class Guid extends SuperPojo {
	private static final String strFormat = "%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x";

	public static final Guid Empty = Guid.fromStr("00000000-0000-0000-0000-000000000000");

	// 高位64位 Fixed64
	@Field(id = 1)
	private byte[] data1;

	// 低位64位
	@Field(id = 2)
	private byte[] data2;

	public byte[] getData1() {
		return data1;
	}

	public void setData1(byte[] d1) {
		this.data1 = d1;

	}

	public byte[] getData2() {
		return data2;
	}

	public void setData2(byte[] d2) {
		this.data2 = d2;
	}

	/**
	 * 禁止使用此种方式构造Guid,此方法只是为了给ProtoEntity用 请使用randomGuid()生成新的Guid
	 */
	public Guid() {

	}

	/**
	 * 为了和C#的保持一致
	 * 
	 * @return
	 */
	public String toStr() {
		return String.format(strFormat, data1[3], data1[2], data1[1], data1[0], data1[5], data1[4], data1[7], data1[6],
				data2[0], data2[1], data2[2], data2[3], data2[4], data2[5], data2[6], data2[7]);
	}

	@Override
	public byte[] toPbByteArray() {
		byte[] result = new byte[16];
		for (int i = 0; i < 8; i++)
			result[i] = data1[i];
		for (int i = 0; i < 8; i++)
			result[i + 8] = data2[i];
		return result;
	}

	public static Guid fromByteArray(byte[] bytes) {
		Guid guid = new Guid();
		byte[] d1 = new byte[8];
		byte[] d2 = new byte[8];
		for (int i = 0; i < 8; i++)
			d1[i] = bytes[i];
		for (int i = 0; i < 8; i++)
			d2[i] = bytes[i + 8];
		guid.setData1(d1);
		guid.setData2(d2);
		return guid;

	}

	public static Guid randomGuid() {
		Random rand = new Random();
		byte[] bytes = new byte[16];
		rand.nextBytes(bytes);
		return fromByteArray(bytes);

	}

	public static Guid fromStr(String str) {
		if (StringUtils.isNullOrEmpty(str))
			return null;
		Guid guid = new Guid();

		UUID u = UUID.fromString(str);
		long l1 = u.getMostSignificantBits();
		byte[] d1 = new byte[8];
		ByteBuffer bb1 = ByteBuffer.wrap(d1);
		bb1.putLong(l1);
		d1 = bb1.array();
		byte[] dd1 = new byte[8];
		// 反转
		for (int i = 0; i < 4; i++)
			dd1[i] = d1[3 - i];
		dd1[4] = d1[5];
		dd1[5] = d1[4];
		dd1[6] = d1[7];
		dd1[7] = d1[6];
		guid.setData1(dd1);

		byte[] d2 = new byte[8];
		ByteBuffer bb2 = ByteBuffer.wrap(d2);
		long l2 = u.getLeastSignificantBits();
		bb2.putLong(l2);
		guid.setData2(bb2.array());

		return guid;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Guid))
			return false;
		Guid target = (Guid) obj;
		return data1.equals(target.data1) && data2.equals(target.data2);
	}

	public int hashCode() {
		return data1.hashCode() ^ data2.hashCode();
	}
}
