package org.helium.perfmon.observation;

/**
 * 监控条目追踪器，追踪所有监控条目的添加和移除事件
 * <p>
 * 当该追踪器在 {@link ObserverManager} 中注册之后，所有在追踪器注册之前就已经添加的监控条目会立刻触发一次追踪器的添加事件，从而保证可以追踪到所有的监控条目，避免注册顺序的依赖
 * <p>
 * Created by Coral on 2015/8/11.
 */
public interface ObservableTracker {

    /**
     * 监控条目行为枚举
     */
    enum Action {

        /**
         * 添加监控条目
         */
        ADD,

        /**
         * 移除监控条目
         */
        REMOVE
    }

    /**
     * 监控条目行为处理方法
     *
     * @param action     监控条目行为
     * @param observable 监控条目对象
     */
    void action(Action action, Observable observable);
}
