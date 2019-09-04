package org.helium.threading;


/**
 * 用于在Executor中执行Callback的接口类型
 * 
 * Created by Coral
 */
public interface FutureCallbackTask extends Runnable
{
	Future<?> getFuture();
}
