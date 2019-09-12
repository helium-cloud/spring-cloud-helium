package org.helium.cloud.task.manager;

import com.feinno.superpojo.util.StringUtils;
import org.helium.cloud.task.entity.TaskBeans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TaskConsumerAssignor {
	private static  final Logger LOGGER = LoggerFactory.getLogger(TaskConsumerAssignor.class);

	public static Integer partition = 0;
	public static int getPartition() {
		if (partition > 0){
			return partition;
		}
		return partition;

	}

	//获取分区名称
	public static List<TaskBeans.PartitionBean> getAssignor() {

		List<String> serverEndpointList = new ArrayList<>();
		return getAssignor(getPartition(), serverEndpointList, "", "");
	}


	//获取分区名称
	public static List<TaskBeans.PartitionBean> getAssignor(int partitionSize, List list, String endpoint, String pre) {
		//计算分区数量
		List<TaskBeans.PartitionBean> assignorList = new ArrayList<>();
		if (list == null || list.size() == 0){
			return assignorList;
		}
		int consumerSize = list.size();
		int n = partitionSize / consumerSize;
		int m = partitionSize % consumerSize;
		int partitionAssignor = n;
		int index = 0;
//		for (ServerEndpoint serverEndpointItem : list) {
//			if (serverEndpointItem.equals(endpoint)){
//				break;
//			}
//			index++;
//		}
		if (index < m){
			partitionAssignor = partitionAssignor + 1;
		}

		//获取分区名称
		for (int i = index; i < partitionSize; i++) {
			if (i - index > partitionAssignor){
				break;
			}
			TaskBeans.PartitionBean partitionBean = new TaskBeans.PartitionBean();
			partitionBean.setName(pre + i);
			partitionBean.setIndex(i);
			assignorList.add(partitionBean);
		}
		return assignorList;
	}
}
