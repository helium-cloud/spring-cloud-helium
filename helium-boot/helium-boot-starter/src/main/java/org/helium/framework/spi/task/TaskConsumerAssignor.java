package org.helium.framework.spi.task;

import com.feinno.superpojo.util.StringUtils;
import org.helium.framework.route.ServerEndpoint;
import org.helium.framework.route.center.CentralizedService;

import org.helium.framework.spi.Bootstrap;
import org.helium.framework.task.PartitionBean;
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
		synchronized (partition){
			String partitionStr = null;
			try {
				partition = 6;
				partitionStr = System.getProperty("PARTITION");
				if (!StringUtils.isNullOrEmpty(partitionStr)){
					partition = Integer.valueOf(partition);
				}
				partitionStr = System.getenv("PARTITION");
				if (!StringUtils.isNullOrEmpty(partitionStr)){
					partition = Integer.valueOf(partition);
				}
			} catch (Throwable e){
				LOGGER.error("getPartition:{}", partitionStr, e);
			}
		}

		return partition;

	}

	//获取分区名称
	public static List<PartitionBean> getAssignor() {
		CentralizedService service = Bootstrap.INSTANCE.getCentralizedService();
		ServerEndpoint serverEndpoint = Bootstrap.INSTANCE.getCentralizedService().getServerEndpoint();
		List<ServerEndpoint> serverEndpointList = service.getServerEndpointList();
		if (serverEndpointList == null || serverEndpointList.size() == 0){
			serverEndpointList = service.getGrayServerEndpointList();
		}
		if (serverEndpointList == null || serverEndpointList.size() == 0){
			serverEndpointList = new ArrayList<>();
			serverEndpointList.add(serverEndpoint);
		}
		return getAssignor(getPartition(), serverEndpointList, serverEndpoint, serverEndpoint.getId());
	}


	//获取分区名称
	public static List<PartitionBean> getAssignor(int partitionSize, List<ServerEndpoint> list, ServerEndpoint endpoint, String pre) {
		//计算分区数量
		List<PartitionBean> assignorList = new ArrayList<>();
		if (list == null || list.size() == 0){
			return assignorList;
		}
		int consumerSize = list.size();
		int n = partitionSize / consumerSize;
		int m = partitionSize % consumerSize;
		int partitionAssignor = n;
		int index = 0;
		for (ServerEndpoint serverEndpointItem : list) {
			if (serverEndpointItem.equals(endpoint)){
				break;
			}
			index++;
		}
		if (index < m){
			partitionAssignor = partitionAssignor + 1;
		}

		//获取分区名称
		for (int i = index; i < partitionSize; i++) {
			if (i - index > partitionAssignor){
				break;
			}
			PartitionBean partitionBean = new PartitionBean();
			partitionBean.setName(pre + i);
			partitionBean.setIndex(i);
			assignorList.add(partitionBean);
		}
		return assignorList;
	}
}
