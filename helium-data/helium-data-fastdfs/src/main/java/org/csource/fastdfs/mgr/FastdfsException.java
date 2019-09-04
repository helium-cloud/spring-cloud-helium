package org.csource.fastdfs.mgr;

public class FastdfsException extends RuntimeException{
	
	private static final long serialVersionUID = 2951820399620063916L;

	public FastdfsException(String message)
    {
        super(message);
    }

    public FastdfsException(Throwable e)
    {
        super(e);
    }

    public FastdfsException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
