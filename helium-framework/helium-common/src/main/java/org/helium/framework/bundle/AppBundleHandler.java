package org.helium.framework.bundle;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanContextService;
import org.helium.framework.utils.StateController;
import org.helium.util.ErrorList;

import java.util.List;

/**
 * Created by Coral on 10/5/15.
 */
public interface AppBundleHandler extends BundleHandler {
	boolean isExport();
	/**
	 * 获取StateController
	 * @return
	 */
	StateController<BundleState> getStateController();

	/**
	 * 获取Bundle中的BeanContext
	 * @return
	 */
	List<BeanContext> getBeans();

	@Override
	default BundleState getState() {
		return getStateController().getState();
	}

	@Override
	default Throwable getLastError() {
		return getStateController().getLastError();
	}

	@Override
	default boolean isAppBundle() {
		return true;
	}

	/**
	 * 分析Bundle中的内容，对于AppBundle来说，相当于将其中的Bean读取出来，然后加载
	 * @return
	 */
	@Override
	default boolean resolve() {
		return getStateController().doAction(BundleAction.RESOLVE, () -> {
			checkErrors(doResolve(), "resolve");
		});
	}

	/**
	 * 将Bundle中的内容注册到contextService当中
	 * @return
	 */
	default boolean register(BeanContextService contextService) {
		return getStateController().doAction(BundleAction.REGISTER, () -> {
			checkErrors(doRegister(contextService), "register");
		});
	}

	/**
	 * 进行Bean的装配, 只有app类型的Bundle需要进行assemble
	 * 1. XML或类型解析不报错
	 * 2. 不缺配置
	 * 3. 依赖完整
	 * @param contextService
	 */
	default boolean assemble(BeanContextService contextService) {
		return getStateController().doAction(BundleAction.ASSEMBLE, () -> {
			checkErrors(doAssemble(contextService), "assemble");
		});
	}

	/**
	 * 进行Bean的装配, 只有app类型的Bundle需要进行upadte
	 * 1. XML或类型解析不报错
	 * 2. 不缺配置
	 * 3. 依赖完整
	 * @param contextService
	 */
	default boolean update(BeanContextService contextService) {
		if (doUpdate(contextService) != null){
			return false;
		}
		return true;
	}

	/**
	 * 启动Bundle及内部包含的Beans
	 */
	@Override
	default boolean start() {
		return getStateController().doAction(BundleAction.START, () -> {
			checkErrors(doStart(), "start");
		});
	}

	/**
	 * 停止Bundle及内部包含的Beans
	 * @return
	 */
	@Override
	default boolean stop() {
		return getStateController().doAction(BundleAction.STOP, () -> {
			checkErrors(doStop(), "stop");
		});
	}

	/**
	 * 移除一个Bundle
	 * @throws Exception
	 */
	@Override
	default boolean uninstall() {
		return getStateController().doAction(BundleAction.UNINSTALL, () -> {
			checkErrors(doUninstall(), "uninstall");
		});
	}

	/**
	 * 检查错误, 默认不进行处理
	 * @param errors
	 * @param message
	 */
	default void checkErrors(ErrorList errors, String message) {
	}

	/**
	 * 返回checkErrors函数中保留下来的错误信息
	 * @return
	 */
	ErrorList getLastErrors();

	/**
	 * 执行Resolve动作，分析bundle.xml并将加载Bean
	 * @return
	 */
	ErrorList doResolve();

	/**
	 * 执行Register动作，将beans注册到BeanContextService当中
	 * @param contextService
	 * @return
	 */
	ErrorList doRegister(BeanContextService contextService);

	/**
	 * 执行装配动作
	 * @param contextService
	 * @return
	 */
	ErrorList doAssemble(BeanContextService contextService);

	/**
	 * 更新动作
	 * @param contextService
	 * @return
	 */
	ErrorList doUpdate(BeanContextService contextService);

	/**
	 * 启动所有的Beans
	 * @return
	 */
	ErrorList doStart();

	/**
	 * 停止所有的Beans
	 * @return
	 */
	ErrorList doStop();

	/**
	 * 卸载整个Bundle
	 * @return
	 */
	ErrorList doUninstall();
}