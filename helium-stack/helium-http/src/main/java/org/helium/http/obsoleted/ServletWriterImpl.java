//package org.helium.http.servlet.extension.spi;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.Writer;
//
///**
// * @author coral
// * @version 创建时间：2014年9月24日 类说明
// */
//class ServletWriterImpl extends PrintWriter {
//
//	private static final char[] LINE_SEP = System.getProperty("line.separator").toCharArray();
//	protected Writer ob;
//	protected boolean error = false;
//
//	public ServletWriterImpl(Writer ob) {
//		super(ob);
//		this.ob = ob;
//	}
//
//	@Override
//	protected Object clone() throws CloneNotSupportedException {
//		throw new CloneNotSupportedException();
//	}
//
//	void clear() {
//		ob = null;
//	}
//
//	void recycle() {
//		error = false;
//	}
//
//	@Override
//	public void flush() {
//		if (error) {
//			return;
//		}
//		try {
//			ob.flush();
//		} catch (IOException e) {
//			error = true;
//		}
//
//	}
//
//	@Override
//	public void close() {
//		try {
//			ob.close();
//		} catch (IOException ignored) {
//		}
//		error = false;
//
//	}
//
//	@Override
//	public boolean checkError() {
//		flush();
//		return error;
//	}
//
//	@Override
//	public void write(int c) {
//		if (error) {
//			return;
//		}
//
//		try {
//			ob.write(c);
//		} catch (IOException e) {
//			error = true;
//		}
//
//	}
//
//	@Override
//	public void write(char buf[], int off, int len) {
//		if (error) {
//			return;
//		}
//
//		try {
//			ob.write(buf, off, len);
//		} catch (IOException e) {
//			error = true;
//		}
//
//	}
//
//	@Override
//	public void write(char buf[]) {
//		write(buf, 0, buf.length);
//	}
//
//	@Override
//	public void write(String s, int off, int len) {
//		if (error) {
//			return;
//		}
//
//		try {
//			ob.write(s, off, len);
//		} catch (IOException e) {
//			error = true;
//		}
//
//	}
//
//	@Override
//	public void write(String s) {
//		write(s, 0, s.length());
//	}
//
//	@Override
//	public void print(boolean b) {
//		if (b) {
//			write("true");
//		} else {
//			write("false");
//		}
//	}
//
//	@Override
//	public void print(char c) {
//		write(c);
//	}
//
//	@Override
//	public void print(int i) {
//		write(String.valueOf(i));
//	}
//
//	@Override
//	public void print(long l) {
//		write(String.valueOf(l));
//	}
//
//	@Override
//	public void print(float f) {
//		write(String.valueOf(f));
//	}
//
//	@Override
//	public void print(double d) {
//		write(String.valueOf(d));
//	}
//
//	@Override
//	public void print(char s[]) {
//		write(s);
//	}
//
//	@Override
//	public void print(String s) {
//		if (s == null) {
//			s = "null";
//		}
//		write(s);
//	}
//
//	@Override
//	public void print(Object obj) {
//		write(String.valueOf(obj));
//	}
//
//	@Override
//	public void println() {
//		write(LINE_SEP);
//	}
//
//	@Override
//	public void println(boolean b) {
//		print(b);
//		println();
//	}
//
//	@Override
//	public void println(char c) {
//		print(c);
//		println();
//	}
//
//	@Override
//	public void println(int i) {
//		print(i);
//		println();
//	}
//
//	@Override
//	public void println(long l) {
//		print(l);
//		println();
//	}
//
//	@Override
//	public void println(float f) {
//		print(f);
//		println();
//	}
//
//	@Override
//	public void println(double d) {
//		print(d);
//		println();
//	}
//
//	@Override
//	public void println(char c[]) {
//		print(c);
//		println();
//	}
//
//	@Override
//	public void println(String s) {
//		print(s);
//		println();
//	}
//
//	@Override
//	public void println(Object o) {
//		print(o);
//		println();
//	}
//
//}
