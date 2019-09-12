package org.helium.framework.task;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.framework.BeanContextService;
import org.helium.framework.BeanIdentity;
import org.helium.framework.servlet.ServletRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Coral on 9/12/15.
 */
public class TaskRouter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServletRouter.class);
	private static final Marker MARKER = MarkerFactory.getMarker("TASK");

	private Map<BeanIdentity, TaskBeanContext> tasks = new HashMap<>();
	private Map<String, TaskRouteEntry> entrys;

	public TaskRouter(BeanContextService contextService) {
		entrys = new HashMap<>();
		contextService.syncBeans(
				(sender, modification) -> {
					switch (modification.getAction()) {
						case INSERT:
							LOGGER.info(MARKER, "INSERT Task={}", modification.getBeanContext().getId());
							addTask((TaskBeanContext) modification.getBeanContext());
							break;
						case UPDATE:
							LOGGER.info(MARKER, "UPDATE Task={}", modification.getBeanContext().getId());
							updateTask((TaskBeanContext) modification.getBeanContext());
							break;
						case DELETE:
							LOGGER.info(MARKER, "REMOVE Task={}", modification.getBeanContext().getId());
							removeTask((TaskBeanContext) modification.getBeanContext());
							break;
					}
				},
				bc -> bc instanceof TaskBeanContext
		);
	}


	private void addTask(TaskBeanContext task) {
		synchronized (this) {
			tasks.put(task.getId(), task);
			buildEntrys();
		}
	}

	private void removeTask(TaskBeanContext task) {
		synchronized (this) {
			if (tasks.remove(task.getId()) != null) {
				buildEntrys();
			}
		}
	}

	private void updateTask(TaskBeanContext task) {
		synchronized (this) {
			tasks.put(task.getId(), task);
		}
	}

	/**
	 * 每次buildEntrys都需要重新梳理所有的tasks, 更新到entrys中去
	 */
	private void buildEntrys() {
		Map<String, TaskRouteEntry> map = new HashMap<>();
		synchronized (this) {
			//
			// 按照eventName将tasks重组临时的map中
			for (TaskBeanContext tc: tasks.values()) {
				String eventName = tc.getEventName();
				TaskRouteEntry entry = map.get(tc.getEventName());
				if (entry == null) {
					entry = new TaskRouteEntry(eventName);
					getEntry(eventName);
				}
				map.put(tc.getEventName(), entry);
				entry.list.contexts.add(tc);
			}

			//
			// 更新entrys中的内容，同时将entrys替换为临时map
			entrys.forEach((k, v) -> {
				TaskRouteEntry newEntry = map.get(k);
				if (newEntry == null) {
					v.list.contexts =  new ArrayList<>();   // 将Entry置空
				} else {
					v.list.contexts = newEntry.list.contexts;
				}
			});
		}
	}

	/**
	 * 获取一个Entry，提供给Setter服务调用，需要提前占坑(TaskRouteEntry)
	 * @param eventName
	 * @return
	 */
	public TaskRouteEntry getEntry(String eventName) {
		synchronized (this) {
			TaskRouteEntry re = entrys.get(eventName);
			if (re == null) {
				re = new TaskRouteEntry(eventName);
				entrys.put(eventName, re);
			}
			return re;
		}
	}

	static class TaskContextList {
		List<TaskBeanContext> contexts = new ArrayList<>();
	}

	public static class TaskRouteEntry {
		TaskContextList list = new TaskContextList();

		private String eventName;
		private TaskCounter counter;

		private TaskRouteEntry(String eventName) {
			this.eventName = eventName;
			this.counter = PerformanceCounterFactory.getCounters(TaskCounter.class, eventName);
		}

		public String getEventName() {
			return eventName;
		}

		public void consume(Object args) {
			for (TaskBeanContext task: list.contexts) {
				try {
					counter.getProduce().increase();
					task.consume(args);
				} catch (Exception ex) {
					LOGGER.error("TaskRouteEntry.consume failed" + eventName + " {}", ex);
				}
			}
		}
	}
}
