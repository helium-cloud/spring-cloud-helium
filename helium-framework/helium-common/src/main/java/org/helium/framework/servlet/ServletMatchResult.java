package org.helium.framework.servlet;

import org.helium.framework.route.ServerRouter;

/**
 *
 * Created by Coral on 8/8/15.
 */
public class ServletMatchResult {
	//
	// 过滤器
	public interface Filter<E extends ServletMappings> {
		/**
		 * 是否需要执行过滤, 有些情况下在match之前就知道是否需要实际的执行一遍了
		 * @param mappings
		 * @return
		 */
		boolean needMatch(E mappings);
		/**
		 * 第一遍过滤
		 * @param mr
		 * @return
		 */
		boolean applyFirst(ServletMatchResult mr);

		/**
		 * 第二遍过滤, 因为很重要所以要过滤两遍
		 * @param mr
		 * @return
		 */
		boolean applyLast(ServletMatchResult mr);
	}

	public static final int DEFAULT_PRIORITY = Integer.MAX_VALUE;
	public static final ServletMatchResult UNMATCHED = new ServletMatchResult(false);

	public static ServletMatchResult matched() {
		return new ServletMatchResult(true);
	}

	public static ServletMatchResult unmatched() {
		return UNMATCHED;
	}

	public static ServletMatchResult matchedWith(int priority) {
		return new ServletMatchResult(true, priority);
	}

	private boolean isMatch;
	private int priority;
	private boolean isExperiment;
	private Object attachment;

	private ServerRouter router;

	public ServletMatchResult(boolean isMatch) {
		this(isMatch, DEFAULT_PRIORITY);
	}

	public ServletMatchResult(boolean isMatch, int priority) {
		this.isMatch = isMatch;
		this.priority = priority;
	}

	public boolean isMatch() {
		return isMatch;
	}

	/**
	 * 值越低，优先级越高
	 * @return
	 */
	public int getPriority() {
		return priority;
	}

	public ServerRouter getRouter() {
		return router;
	}

	public void setRouter(ServerRouter router) {
		this.router = router;
	}

	public Object getAttachment() {
		return attachment;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	public boolean isExperiment() {
		return isExperiment;
	}

	public void setIsExperiment(boolean isExperiment) {
		this.isExperiment = isExperiment;
	}

	/**
	 * 全通过滤器
	 */
	public static final Filter ALL_FILTER = new Filter<ServletMappings>() {
		@Override
		public boolean needMatch(ServletMappings mappings) {
			return true;
		}

		@Override
		public boolean applyFirst(ServletMatchResult mr) {
			return true;
		}

		@Override
		public boolean applyLast(ServletMatchResult mr) {
			return true;
		}
	};
}
// TODO: 当有了闲工夫，并且match的条件确实太多，可以考虑增加一种用于构建快速索引的数据结构, 能够快速的处理Match请求