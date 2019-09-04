package com.feinno.superpojo;

import java.util.Arrays;
import java.util.List;

import com.feinno.superpojo.util.JavaEval;
import com.feinno.superpojo.util.SuperPojoChecker;
import com.feinno.superpojo.util.TwoTuple;

public class AntBuilder {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String dest = null;
		String pkgName = null;
		System.out.println("Args : " + args == null ? null : Arrays.toString(args));
		if (args != null && args.length > 0) {
			dest = args[0].trim();
			dest = dest.length() == 0 ? null : dest;
		} else {
			System.err.println("Args is empty. Save path is null.");
		}
		if (args != null && args.length > 1) {
			pkgName = args[1];
		} else {
			pkgName = "";
		}
		JavaEval.setSaveClassPath(dest);
		List<TwoTuple<String, Exception>> failed = SuperPojoChecker.check(pkgName);
		if (failed.size() > 0) {
			System.out.println("SuperPojo build failed.\n" + failed);
		}
	}
}
