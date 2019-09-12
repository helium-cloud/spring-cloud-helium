package org.helium.cloud.task;

import org.helium.cloud.common.utils.SpringContextUtil;
import org.helium.cloud.task.api.TaskBeanContext;
import org.helium.perfmon.PerformanceCounterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 负责服务的路由转发
 */
public class TaskRouter {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskRouter.class);
	private static final Marker MARKER = MarkerFactory.getMarker("TASK");

	private Map<String, TaskBeanContext> tasks = new HashMap<>();
	private Map<String, TaskRouteEntry> entrys = new HashMap<>();


	private void addTask(TaskBeanContext task) {
		synchronized (this) {
			tasks.put(task.getEventId(), task);
			buildEntrys();
		}
	}

	private void removeTask(TaskBeanContext task) {
		synchronized (this) {
			if (tasks.remove(task.getEventId()) != null) {
				buildEntrys();
			}
		}
	}

	private void updateTask(TaskBeanContext task) {
		synchronized (this) {
			tasks.put(task.getEventId(), task);
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
	 * @param eventId
	 * @return
	 */
	public TaskRouteEntry getEntry(String eventId) {
		synchronized (this) {
			TaskRouteEntry re = entrys.get(eventId);
			if (re == null) {
				re = new TaskRouteEntry(eventId);
				entrys.put(eventId, re);
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
		private org.helium.cloud.task.TaskCounter counter;

		private TaskRouteEntry(String eventName) {
			this.eventName = eventName;
			this.counter = PerformanceCounterFactory.getCounters(TaskCounter.class, eventName);
		}

		public String getEventName() {
			return eventName;
		}

		public void consume(Object args) {
			try {
				TaskInstance taskInstance = SpringContextUtil.getBean(eventName, TaskInstance.class);
				taskInstance.consume(args);
				counter.getProduce().increase();
			} catch (Exception ex) {
				LOGGER.error("TaskRouteEntry.consume failed" + eventName + " {}", ex);
			}

		}
	}
}
