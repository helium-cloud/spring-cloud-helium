package org.helium.redis.cluster.lambda;

/**
 * Created with IntelliJ IDEA.
 * User: xium
 * Date: 13-5-17
 * Time: 下午3:24
 * To change this template use File | Settings | File Templates.
 */
public interface LambdaActionObject<E,N> {
    N run(E item);
}
