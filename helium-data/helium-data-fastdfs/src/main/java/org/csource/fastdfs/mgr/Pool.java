package org.csource.fastdfs.mgr;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

public class Pool<E> {
	
	private final GenericObjectPool internalPool;
	
	public Pool(org.apache.commons.pool.impl.GenericObjectPool.Config poolConfig, PoolableObjectFactory factory) {
		this.internalPool=new GenericObjectPool(factory, poolConfig);
	}

	public E getResource()
    {
        try
        {
            return (E) internalPool.borrowObject();
        }
        catch(Exception e)
        {
            throw new FastdfsException("Could not get a resource from the pool", e);
        }
    }

    public void returnResource(E resource)
    {
        try
        {
            internalPool.returnObject(resource);
        }
        catch(Exception e)
        {
            throw new FastdfsException("Could not return the resource to the pool", e);
        }
    }

    public void returnBrokenResource(E resource)
    {
        try
        {
            internalPool.invalidateObject(resource);
        }
        catch(Exception e)
        {
            throw new FastdfsException("Could not return the resource to the pool", e);
        }
    }

    public void destroy()
    {
        try
        {
            internalPool.close();
        }
        catch(Exception e)
        {
            throw new FastdfsException("Could not destroy the pool", e);
        }
    }

}
