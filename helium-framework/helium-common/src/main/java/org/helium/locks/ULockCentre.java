package org.helium.locks;

/**
 * Created by lvmingwei on 16-6-1.
 */
public interface ULockCentre {

    void reg(String lockId, ULockEvent event) throws Exception;

    void unreg(String lockId) throws Exception;

    boolean isFirst(String lockId) throws Exception;

}