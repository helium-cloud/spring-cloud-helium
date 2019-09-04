package org.helium.framework.spi.task;

import org.helium.framework.task.TaskQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TaskQueueMemory implements TaskQueue{
	private Queue<TaskArgs> queue = new ConcurrentLinkedDeque<TaskArgs>();;

	private int limit = 128;

	public TaskQueueMemory() {
	}

	public TaskQueueMemory(int limit) {
		this.limit = limit;
	}

	@Override
	public void put(int partition, TaskArgs taskArgs) {
		queue.add(taskArgs);
	}

	@Override
	public List<TaskArgs> poolList(int partition) {
		List<TaskArgs> list = new ArrayList<>();
		for (int i = 0; i < getLimit(); i++ ){
			TaskArgs taskArgs = queue.poll();
			if (taskArgs != null){
				list.add(taskArgs);
			} else {
				break;
			}
		}
		return list;
	}

	@Override
	public TaskArgs pool(int partition) {
		return queue.poll();
	}

	@Override
	public void delete(int partition, TaskArgs taskArgs) {

	}

	@Override
	public void delete(int partition, List<TaskArgs> list) {

	}

	@Override
	public boolean isEmpty(int partition) {
		return queue.isEmpty();
	}

	private int getLimit(){
		return limit;
	}
}
