package org.helium.redis.cluster.lambda;

/**
 * Created with IntelliJ IDEA.
 * User: xium
 * Date: 13-5-17
 * Time: 下午3:19
 * To change this template use File | Settings | File Templates.
 */
public interface LambdaActionBool<E> {
    boolean run(E item);
}
