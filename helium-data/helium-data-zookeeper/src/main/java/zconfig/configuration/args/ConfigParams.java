package zconfig.configuration.args;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

/**
 * 针对键值对的封装, 用于处理配置特例化,
 * <p>
 * 形如: computer=IIC-IGS-01;service=IBS;app=core:GetUserInfo 这几个参数是有顺序的,
 * 处理时会拆散为键值对的散列表<br>
 * "computer" -> "IIC-IGS-01"<br>
 * "service" -> "IBS"<br>
 * "app" -> "core:GetUserInfo"<br>
 * 
 * 文本中"="与";"作为分隔符出现, 暂时不允许配置值中出现分隔符
 *  
 * TODO: 在需要使用分隔符时使用"\;"或"\="进行替换
 * 
 * @author 高磊 gaolei@feinno.com
 */
public class ConfigParams extends LinkedHashMap<String, String> {
	private static final long serialVersionUID = 3793255485583004786L;

	public ConfigParams() {
	}

	public ConfigParams(ConfigParams args) {
		super(args);
	}

	public ConfigParams(String text) {
		if (text != null && text.length() > 0) {
			/*
			 * if(text.endsWith(";")) text = text.substring(0,text.length()-1);
			 * int index = text.indexOf(';'); String temp = null; while(index >
			 * 0) { if(text.charAt(index-1)== '\\') { String part =
			 * text.substring(0, index-1)+";"; temp = temp == null ? part : temp
			 * + part; } else { String part = text.substring(0, index); temp =
			 * temp == null ? part : temp + part; doParam(temp); temp = null; }
			 * text = text.substring(index+1); index = text.indexOf(';');
			 * if(index<0) doParam(text); }
			 */
			String[] params = text.split(";");
			for (String param : params) {
				if (param != null && param.length() > 0) {
					String[] fields = param.split("=");
					if (fields.length == 2)
						this.put(fields[0].trim(), fields[1].trim());
				}
			}
		}
	}

	/*
	 * private void doParam(String param) { if(param != null &&
	 * param.length()>0) { int index = param.indexOf('='); String temp = null;
	 * while(index > 0) { if(param.charAt(index-1)== '\\') { String part =
	 * param.substring(0, index-1)+"="; temp = temp == null ? part : temp +
	 * part; } else { String part = param.substring(0, index); temp = temp ==
	 * null ? part : temp + part; this.put(temp,
	 * param.substring(index+1).replace("\\=", "=")); temp = null; return; }
	 * param = param.substring(index+1); index = param.indexOf('='); } }
	 * 
	 * }
	 */

	/**
	 * 
	 * 与另外一个ConfigArgs进行merge操作,
	 * 
	 * @param args
	 * @param replaceOnConflict
	 *            当存在key冲突时, true用传入参数中的覆盖, false保持原有值不变
	 * @return
	 */
	public ConfigParams merge(ConfigParams args, boolean replaceOnConflict) {
		ConfigParams result = new ConfigParams(this);
		for (String key : args.keySet()) {
			if (replaceOnConflict)
				result.put(key, args.get(key));
			else if (!result.containsKey(key))
				result.put(key, args.get(key));
		}
		return result;
	}

	/**
	 * 转换为原始字符串格式
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (String key : this.keySet()) {
			i++;
			if (i != this.keySet().size())
				sb.append(key).append("=").append(this.get(key)).append(";");
			else
				sb.append(key).append("=").append(this.get(key));
		}
		return sb.toString();

	}

	/**
	 *
	 * <p>
	 * example: this="computer=A" input="app=A;computer=A" return: 1 this=""
	 * input="app=A;computer=A" return: 0
	 * 
	 * @param params
	 * @return
	 */
	public int match(ConfigParams params) {
		// 第一层过滤，如果被比较的对象为空或任何一方没有内容，则返回匹配结果一定是0
		if (params == null || params.keySet().size() == 0 || this.keySet().size() == 0) {
			return 0;
		}
		// 第二层过滤，逐个比对
		int mark = 0;
		for (final String key : params.keySet()) {
			String myValue = this.get(key);
			String newValue = params.get(key);
			// 如果内存地址相同或同时为空，那么记为相同
			if (myValue == newValue) {
				mark++;
				continue;
			} else if (myValue != null && newValue != null && myValue.equals(newValue)) {
				// 当前类继承自LinkedHashMap，间接继承自HashMap，因此value允许为空，所以一定要判空
				mark++;
				continue;
			}
		}
		return mark;
	}

	public int matchRegex(ConfigParams params) {
		// 第一层过滤，如果被比较的对象为空或任何一方没有内容，则返回匹配结果一定是0
		if (params == null || params.keySet().size() == 0 || this.keySet().size() == 0) {
			return 0;
		}
		// 第二层过滤，逐个比对
		int mark = 0;
		for (final String key : params.keySet()) {
			String myValue = this.get(key);
			String newValue = params.get(key);
			// 如果内存地址相同或同时为空，那么记为相同
			if (myValue == newValue) {
				mark++;
				continue;
			} else if (myValue != null && newValue != null
					&& (newValue.equals(myValue) || Pattern.compile(newValue).matcher(myValue).find())) {
				mark++;
				continue;
			}
		}
		return mark;
	}

	// Pattern packPattern = Pattern.compile("package\\s+[^\\s]+;{1}");
}
