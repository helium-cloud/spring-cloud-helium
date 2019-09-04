package com.feinno.superpojo;

import com.feinno.superpojo.util.FileUtil;

import java.io.IOException;

/**
 * 
 * <b>描述: </b>这是一个用于序列化模板辅助生成的类，在代码自动生成时，自动生成的代码会来填充模板，此模板帮助类，
 * 仅仅是为了将一个文件类型的模板转换成字节数组的形式
 * <p>
 * <b>功能: </b>帮助文件或文字类型的模板转换成字节数组的表现形式
 * <p>
 * <b>用法: </b>不建议外部调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class ProtoTemplateHelper {

	public static String getBytesFrom(String path) throws IOException {
		String string = FileUtil.read(path).trim();
		System.out.println(path + ":");
		StringBuffer sb = new StringBuffer();
		for (byte b : string.getBytes()) {
			sb.append("0x");
			String hex = Integer.toHexString(b & 0xFF).toUpperCase();
			if (hex.length() == 1) {
				sb.append("0");
			}
			sb.append(hex).append(",");
		}
		sb = sb.length() > 0 ? sb.delete(sb.length() - 1, sb.length()) : sb;
		System.out.println(sb.toString());
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// if (ProtoTemplateHelper.class.getResource("/") != null) {
		// String path = ProtoTemplateHelper.class.getResource("/").getPath();
		// logger.info("NativeProtoEntity.template");
		// getBytesFrom(path + "protobuf/NativeProtoEntity.template");
		// logger.info("ProtoBuilder.template");
		// getBytesFrom(path + "protobuf/ProtoBuilder.template");
		// logger.info("ProtoBuilderFactory.template");
		// getBytesFrom(path + "protobuf/ProtoBuilderFactory.template");
		// }

		// System.out.println(ClassTemplate.PROTO_NATIVE_ENTITY_CODE_TEMPLATE);
		// getBytesFrom("/data/PROTO_NATIVE_ENTITY_CODE_TEMPLATE.txt");

		 System.out.println(ClassTemplate.PROTO_BUILDER_TEMPLATE);
		 getBytesFrom("/data/PROTO_BUILDER_TEMPLATE.txt");

		// getBytesFrom("/home/lvmingwei/Other/protobuf-2.5.0/test/PROTO_ENTITY_TEMPLATE.tmp");
	}
}
