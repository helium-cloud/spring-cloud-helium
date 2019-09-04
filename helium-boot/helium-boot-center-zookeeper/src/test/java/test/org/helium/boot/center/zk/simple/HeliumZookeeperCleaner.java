package test.org.helium.boot.center.zk.simple;


import org.helium.framework.route.zk.ZkCentralizedService;

/**
 * Created by Coral on 8/12/15.
 */
public class HeliumZookeeperCleaner {
	public static void main(String[] args) throws Exception {
		ZkCentralizedService center = new ZkCentralizedService();
		center.connect("10.10.41.52:7998");
		center.cleanBundles();

		System.out.println(">>> ======== Clean OK ======== <<<");
	}
}
