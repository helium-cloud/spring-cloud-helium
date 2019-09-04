package org.helium.redis.sentinel;

/**
 * Created with IntelliJ IDEA.
 * User: lihongbo
 * Date: 13-5-27
 * Time: 下午2:42
 * To change this template use File | Settings | File Templates.
 */
public class UnSupportedException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnSupportedException(String p0) {
        super(p0);
    }
}
