/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2010-12-29
 * 
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package com.feinno.superpojo.type;


import com.feinno.superpojo.util.EnumParser;

/**
 * 
 * <b>描述: </b>实现类似于.Net中[Flags]注解功能的Flags Enum,实现标记位的功能<br>
 * 标记位可以通过一个标记，标识出几种信息，例如Java虚拟机规范中以一个byte就能够标识出了一个类的访问权限及基础属性(access_flags)
 * <p>
 * <b>功能: </b>通过一个标记，标识出几种信息，实现类似于.Net中[Flags]注解功能的Flags Enum
 * <p>
 * <b>用法: </b>
 * 
 * <pre>
 *  首先有一个类，继承自{@link EnumInteger}
 *  class TestEI implements EnumInteger {
 *  		private int value;
 *  
 *  		public TestEI(int value) {
 *  			this.value = value;
 *  		}
 *  
 *  		public int intValue() {
 *  			return value;
 *  		}
 *  	}
 *  
 * 具体使用示例
 * Flags TestFlags1 = new Flags(1);
 * EnumInteger testEI1 = new TestEI(0);
 * Flags TestFlags2 = new Flags(new TestEI(0));
 * EnumInteger testEI2 = new TestEI(1);
 * 
 * Assert.assertEquals(1, TestFlags1.or(testEI1).intValue());
 * Assert.assertEquals(1, TestFlags1.or(TestFlags2).intValue());
 * 		
 * Assert.assertEquals(0, TestFlags1.and(testEI1).intValue());
 * Assert.assertEquals(0, TestFlags1.and(TestFlags2).intValue());
 * 		
 * Assert.assertEquals(false, TestFlags1.has(testEI1));
 * Assert.assertEquals(true, TestFlags1.has(testEI2));
 * 		
 * Assert.assertEquals(0, TestFlags1.extract(testEI1));
 * </pre>
 * <p>
 * 
 * @author 高磊 gaolei@feinno.com
 * @see EnumInteger
 * @see EnumParser
 * @param <E>
 */
public class Flags<E extends EnumInteger>
{
	public Flags(int value)
	{
		this.value = value;
	}
	
	public Flags(E value)
	{
		this.value = value.intValue();
	}

	public Flags<E> or(E rval)
	{
		this.value = this.value | rval.intValue();
		return this;
	}
	
	public Flags<E> or(Flags<E> rval)
	{
		this.value = this.value | rval.value;
		return this;
	}

	public Flags<E> and(Flags<E> rval)
	{
		this.value = this.value & rval.value;
		return this;
	}

	public Flags<E> and(E rval)
	{
		this.value = this.value & rval.intValue();
		return this;
	}
	
	public Flags<E> xor(E rval)
	{
		this.value = this.value ^ rval.intValue();
		return this; 
	}

	public boolean has(E rval)
	{
		return (value & rval.intValue()) > 0;
	}
	
	public void setFlag(E mask, boolean bool)
	{
		value = (value ^ (value & mask.intValue())) | (bool ? mask.intValue() : 0);   
	}
	
	public void setFlags(E mask, int value)
	{
		int order = getMaskOrder(mask.intValue());
		this.value = (this.value ^ (this.value & mask.intValue())) | (value << order);
	}
	
	public boolean getFlag(E mask)
	{
		return (value & mask.intValue()) > 0;
	}
	
	public int getFlags(E mask)
	{
		int order = getMaskOrder(mask.intValue());
		return (value & mask.intValue()) >> order;
	}
	
	public int extract(E mask)
	{
		return (value & mask.intValue()) >> getMaskOrder(mask);
	}

	public static <E extends EnumInteger> int getMaskOrder(E e)
	{
		int mask = e.intValue();
		int n = 0;
		while (mask > 0) {
			if ((mask & 1) > 0)
				return n;
			n++;
			mask = mask >> 1;
		}
		return n;
	}

	public static <E extends EnumInteger> Flags<E> valueOf(int value)
	{
		return new Flags<E>(value);
	}

	public static <E extends EnumInteger> Flags<E> of(E first, E... last)
	{
		int a = first.intValue();
		for (E e : last) {
			a |= e.intValue();
		}
		return new Flags<E>(a);
	}

	private int value;

	@Deprecated
	public int value()
	{
		return value;
	}
	
	public int intValue()
	{
		return value;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof Flags))
			return false;
		Flags<E> target = (Flags<E>) obj;
		return (this.value == target.value);
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}	
	
	public static int getMaskOrder(int mask)
	{
		int n = 0;
		while (mask > 0) {
			if ((mask & 1) > 0)
				return n;
			n++;
			mask = mask >> 1;
		}
		throw new RuntimeException("Holyshit!!!");
	}
	
	/**
	 * 传入Flag使用的枚举类，输出flag对应的内容
	 * @param clazz
	 * @return
	 */
	public String toString(Class<?> clazz)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Flags : ");
		try{
				if(clazz.isEnum())
				{		
					Object[] objs = clazz.getEnumConstants();
					for(Object obj : objs)
					{
						if(obj instanceof EnumInteger)
						{
							int intValue = ((EnumInteger)obj).intValue();
							if((this.value & intValue) == intValue)
							{
								sb.append(" | "+obj.toString());
							}
						}
					}
					
				}
			}
			catch(Exception exc)
			{
				sb.append(" error -> intCode : " + this.value);
			}
		return sb.toString();
	 }
	
	@Override
	public String toString() {
		return String.valueOf(this.value);
	}
}
