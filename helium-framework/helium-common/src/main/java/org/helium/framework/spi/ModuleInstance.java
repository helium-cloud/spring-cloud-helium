package org.helium.framework.spi;

import org.helium.framework.BeanContext;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.servlet.ServletDescriptor;
import org.helium.framework.servlet.ServletMappings;
import org.helium.framework.servlet.ServletStack;
import org.helium.framework.servlet.StackManager;

import java.util.List;

/**
 * Created by Coral on 7/28/15.
 */
public class ModuleInstance extends BeanInstance {
	private ServletDescriptor descriptor;
	private ServletStack[] stacks;

	/**
	 * 构造函数
	 *
	 * @param configuration
	 */
	public ModuleInstance(BeanConfiguration configuration, BeanContextProvider cp) {
		super(configuration, cp);
	}

	@Override
	protected void doResolve() {
		StackManager servletService = BeanContext.getContextService().getService(StackManager.class);
//		ServletMappingsNode node = getConfiguration().getServletMappings();
//		if (node == null) {
//			throw new IllegalArgumentException("Module must have <servletMappings/> node");
//		}
		ServletDescriptor descriptor = servletService.getModuleDescriptor(getBean());
		if (descriptor == null) {
			throw new IllegalArgumentException("unknown module or servlet" + this.toString());
		}
		this.putAttachment(ServletDescriptor.class, descriptor);

		if (getConfiguration().getServletMappings() != null) {
			ServletMappings mappings = descriptor.parseMappings(getConfiguration().getServletMappings());
			this.putAttachment(ServletMappings.class, mappings);
		}

		//
		// 初始化Stacks
		List<String> ss = getConfiguration().getParentNode().getStacks();
		stacks = new ServletStack[ss.size()];
		for (int i = 0; i < ss.size(); i++) {
			ServletStack stack = servletService.getStack(ss.get(i));
			if (stack == null) {
				throw new IllegalArgumentException("unknown stack:" + ss.get(i));
			}
			stacks[i] = stack;
		}
	}

	@Override
	protected void doStart() {
		for (ServletStack stack: stacks) {
			stack.registerModule(this);
		}
	}

	@Override
	protected void doStop() {
		for (ServletStack stack : stacks) {
			stack.unregisterModule(this);
		}
	}
}
