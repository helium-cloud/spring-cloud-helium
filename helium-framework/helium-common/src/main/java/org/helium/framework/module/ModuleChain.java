package org.helium.framework.module;

import org.helium.util.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 非线程安全
 * Created by Coral on 7/6/15.
 */
public class ModuleChain {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModuleChain.class);

	private Module module;
	private ModuleChain next;

	public ModuleChain() {
	}

	public ModuleChain(Module firstModule) {
		this.module = firstModule;
	}

	/**
	 * copy on write 模式
	 * @return
	 */
	public ModuleChain clone() {
		ModuleChain result = new ModuleChain();
		// 很偷懒的写法，算法复杂度提高了，但是对性能并没有什么影响
		getModules().forEach(v -> result.addModule(v));
		return result;
	}

	/**
	 * 添加在链表尾部
	 * @param value
	 */
	public void addModule(Module value) {
		if (this.module == null) {
			this.module = value;
		} else {
			ModuleChain header = this;
			while (header.next != null) {
				header = header.next;
			}
			header.next = new ModuleChain(value);
		}
	}

	/**
	 * 移除一个Module, 从链表中移除一个对象
	 * @param value
	 */
	public boolean removeModule(Module value) {
		List<Module> list = getModules();
		boolean removed = list.removeIf(v -> v.equals(value));
		clearModules();
		list.forEach(v -> addModule(v));
		return removed;
	}

	/**
	 * 清除所有Modules
	 */
	public void clearModules() {
		module = null;
		next = null;
	}

	/**
	 * 获取当前链表上的所有Modules
	 * @return
	 */
	public List<Module> getModules() {
		List<Module> modules = new ArrayList<>();
		if (module == null) {
			return modules;
		}

		ModuleChain header = this;
		while (header != null) {
			modules.add(header.module);
			header = header.next;
		}
		return modules;
	}

	/**
	 * 处理Module调用链
	 * @param ctx
	 * @param callback
	 */
	public void processModuleChain(ModuleContext ctx, Action<ModuleState> callback) {
		ModuleChain header;
		if (module == null) {
			header = null;
		} else {
			header = this;
		}
		innerProcess(header, ctx, callback);
	}

	private static void innerProcess(ModuleChain header, ModuleContext ctx, Action<ModuleState> callback) {
		LOGGER.info("processModuleChain module={}", header == null ? null : header.module.getClass());
		if (header == null) {
			callback.run(ModuleState.newCompleted());
			return;
		}

		//
		// 如果不Match则自动进行执行下一跳
		if (!header.module.isMatch(ctx)) {
			innerProcess(header.next, ctx, callback);
			return;
		}

		ModuleState state;
		try {
			state = header.module.processModule(ctx);
		} catch (Exception ex) {
			LOGGER.error("module.processModule failed: {}", ex);
			callback.run(ModuleState.newTerminated(ex));
			return;
		}

		state.setListener(new Action<ModuleState.Result>() {
			@Override
			public void run(ModuleState.Result result) {
				if (result.isTerminated) {
					if (result.error != null) {
						LOGGER.error("tag.processModule failed: {}", result.error);
					}
					callback.run(ModuleState.newTerminated(result.error));
				} else {
					ModuleChain.innerProcess(header.next, ctx, callback);
				}
			}
		});
	}
}
