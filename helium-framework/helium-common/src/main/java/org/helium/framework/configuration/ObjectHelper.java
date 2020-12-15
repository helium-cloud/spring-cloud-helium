package org.helium.framework.configuration;

import com.feinno.superpojo.util.DateUtil;
import com.feinno.superpojo.util.EnumParser;

import java.awt.*;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;

/**
 *
 * <b>描述: </b>ObjectHelper，本类提供了Object上的一些处理方法，例如为某一个{@link Field}
 * 赋值，或将一个字符串的值转换成某种具体类型，以及打印Object对象的dump
 * <p>
 * <b>功能: </b>本类提供了Object上的一些处理方法，例如为某一个{@link Field}
 * 赋值，或将一个字符串的值转换成某种具体类型，以及打印Object对象的dump
 * <p>
 * <b>用法: </b>
 *
 * <pre>
 * 部分示例:
 * public class ObjectHelperDemo {
 * 	public String field1;
 * 	public static void main(String args[]) {
 *
 * 		1.为某一个{@link Field}赋值
 * 		ObjectHelperDemo demo = new ObjectHelperDemo();
 * 		Field field = ObjectHelperDemo.class.getDeclaredFields()[0]
 * 		ObjectHelper.setValue(field,demo,"Feinno");
 *
 * 		2.将一个字符串的值转换成某种具体类型
 * 		ObjectHelper.convertTo("100",Long.class);
 *
 * 		3.打印Object对象的dump
 * 		ObjectHelper.dumpObject(demo);
 * 	}
 * }
 *
 * </pre>
 * <p>
 *
 * @author lichunlei
 *
 */
public class ObjectHelper {

    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    @SuppressWarnings("deprecation")
    public static Object convertTo(String value, Class valueClass) throws ParseException {
        if (value == null)
            return null;
        if (valueClass.equals(Character.class)) {
            return new Character(value.charAt(0));
        } else if (valueClass.equals(char.class)) {
            return new Character(value.charAt(0)).charValue();
        } else if (valueClass.equals(Byte.class)) {
            return new Byte(value);
        } else if (valueClass.equals(byte.class)) {
            return Byte.parseByte(value);
        } else if (valueClass.equals(Short.class)) {
            if (value.indexOf(".") > 0) {
                float f = Float.valueOf(value);
                return (short) f;
            } else {
                return Short.valueOf(value);
            }
        } else if (valueClass.equals(short.class)) {
            if (value.indexOf(".") > 0) {
                float f = Float.valueOf(value);
                return (short) f;
            } else {
                return Short.parseShort(value);
            }
        } else if (valueClass.equals(Integer.class)) {
            if (value.indexOf(".") > 0) {
                float f = Float.valueOf(value);
                return (int) f;
            } else {
                return Integer.valueOf(value);
            }
        } else if (valueClass.equals(int.class)) {
            if (value.indexOf(".") > 0) {
                float f = Float.valueOf(value);
                return (int) f;
            } else {
                return Integer.parseInt(value);
            }
        } else if (valueClass.equals(Long.class)) {
            if (value.indexOf(".") > 0) {
                double d = Double.valueOf(value);
                return (long) d;
            } else {
                return Long.valueOf(value);
            }
        } else if (valueClass.equals(long.class)) {
            if (value.indexOf(".") > 0) {
                double d = Double.valueOf(value);
                return (long) d;
            } else {
                return Long.parseLong(value);
            }
        } else if (valueClass.equals(Float.class)) {
            return new Float(value);
        } else if (valueClass.equals(float.class)) {
            return Float.parseFloat(value);
        } else if (valueClass.equals(Double.class)) {
            return new Double(value);
        } else if (valueClass.equals(double.class)) {
            return Double.parseDouble(value);
        }
        // Date目前只支持两种格式：yyyy-MM-dd hh:mm:ss 和 yyyy-MM-dd java太弱，受不了它了
        else if (valueClass.equals(Date.class)) {
            Date d = null;
            try {
                d = new Date(value);
            } catch (Exception e) {
                try {
                    d = DateUtil.getDefaultDate(value, DateUtil.DEFAULT_DATETIME_HYPHEN_FORMAT);
                } catch (ParseException e2) {
                    try {
                        d = DateUtil.getDefaultDate(value, DateUtil.DEFAULT_DATE);
                    } catch (ParseException e3) {
                        d = null;
                    }
                }
            }
            return d;
        } else if (valueClass.equals(boolean.class) || valueClass.equals(Boolean.class)) {
            if (value.indexOf(".") > 0) {
                double d = Double.valueOf(value);
                if (d == 0) {
                    return Boolean.FALSE;
                } else {
                    return Boolean.TRUE;
                }
            }
            if (value.toLowerCase().equals("false") || value.toLowerCase().equals("0"))
                return Boolean.FALSE;
            else
                return Boolean.TRUE;
        } else if (valueClass.isEnum()) {
            value = value.indexOf(".") > 0 ? value.substring(0, value.indexOf(".")) : value;
            return EnumParser.parse(valueClass, value, true);
        }
		/*
		 * else if (valueClass.equals(Flags.class)) { return
		 * EnumParser.parseFlags(valueClass, value, true); }
		 */
        else {
            try {
                return valueClass.cast(value);
            } catch (Exception e) {
                throw new ParseException(String.format("Convert Failed <%2$s>:%1$s", value, valueClass.getName()), 0);
            }
        }
    }

	/*
	 * public static <T> T convertTo(String value) { return (T)convertTo(value,
	 * Class<T>); }
	 */

    // TODO
    // 基于Object的TypeConvert
    public static <T> T convertTo(Object value) {
        return (T) convertTo(value.toString());
    }

    public static void setValue(Field field, Object owner, Object value) throws IllegalArgumentException,
            IllegalAccessException {
        field.set(owner, value);
    }

    public static void setValue(Field field, Object owner, String value, Class valueClass)
            throws IllegalArgumentException, IllegalAccessException, FontFormatException, ParseException {
        Object valueObj = ObjectHelper.convertTo(value, valueClass);
        field.set(owner, valueObj);
    }

    public static String dumpObject(Object obj) {
        return null;
    }

    public static String dumpObject(Object obj, String objectName) {
        if (obj == null) {
            return "<null>";
        } else {
            return null;
        }
    }

    public static String getClassName(Class<?> c, boolean fullName) {
        if (fullName)
            return c.getPackage().getName() + c.getName();
        else
            return c.getName();

    }

    public static Object getFieldValue(Object obj, String fieldName) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field fi;
        Class<?> c = obj.getClass();
        fi = c.getDeclaredField(fieldName);
        if (fi == null)
            return null;
        else
            return fi.get(obj);
    }

    public static int compatibleGetHashCode(int n) {
        return n;
    }

    public static int compatibleGetHashCode(short n) {
        return n;
    }

    public static int compatibleGetHashCodeObject(Object obj) {
        return obj.hashCode();
    }

    public static int compatibleGetHashCode(byte n) {
        return (int) n | ((int) n << 8) | ((int) n << 16) | ((int) n << 24);
    }

    public static int compatibleGetHashCode(long n) {
        return ((int) n) ^ ((int) (n >> 32));
    }

    public static int compatibleGetHashCode(Date time) {
        long internalTicks = time.getTime() * 10000;
        return (((int) internalTicks) ^ ((int) (internalTicks >> 32)));
    }

    public static int compatibleGetHashCode(String s) {
        int num = 0x15051505;
        int num2 = num;

        int np;
        int j = 0;
        int length = s.length();
        for (int i = length; i > 0; i -= 4) {
            char[] ss = s.toCharArray();
            np = (length <= j) ? 0 : ss[j] | ((length > j + 1 ? ss[j + 1] : 0) << 16);
            num = (((num << 5) + num) + (num >> 0x1b)) ^ np;
            if (i <= 2) {
                break;
            }
            j += 2;
            np = (length <= j) ? 0 : ss[j] | ((length > j + 1 ? ss[j + 1] : 0) << 16);
            num2 = (((num2 << 5) + num2) + (num2 >> 0x1b)) ^ np;
            j += 2;
        }
        return (num + (num2 * 0x5d588b65));
    }
}