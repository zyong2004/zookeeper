package day1;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class ZkClient {

	private String groupNode = "sgroup";
	private String subNode = "sub";
	private static int TIME_OUT = 5000;
	private static Logger logger = Logger.getLogger(ZkServer.class);
	private ZooKeeper zk;
	private static String connectString = "localhost:2181,localhost:2182,localhost:2183";

	public void connectZookeeper(String address) throws IOException {
		zk = new ZooKeeper(connectString, TIME_OUT, new zkServerWatcher());
	}

	public void deleteChildNode(String address) {

		try {
			Stat stat = zk.exists(address, true);
			if (null != stat) {
				zk.delete(address, stat.getAversion());
			}
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void createNode(String address) {
		try {
			String createPath = zk.create("/" + groupNode + "/" + subNode,
					address.getBytes("utf-8"), Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.info("create: " + createPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class zkServerWatcher implements Watcher {

		public void process(WatchedEvent event) {
			logger.info("zkserver==");
		}
	}

	/**
	 * server的工作逻辑写在这个方法中 此处不做任何处理, 只让server sleep
	 */

	public void handle() throws InterruptedException {
		Thread.sleep(Long.MAX_VALUE);
	}

	public static void main(String[] args) throws Exception {
		ZkClient client = new ZkClient();
		client.connectZookeeper("test212334");
		
		client.deleteChildNode("/sgroup/11");
		client.handle();
	}

}
