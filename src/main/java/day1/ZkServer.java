package day1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZkServer {
	private static Logger logger = Logger.getLogger(ZkServer.class);
	private String groupNode = "sgroup";
	private ZooKeeper zk;
	private Stat stat = new Stat();

	private static String connectString = "localhost:2181,localhost:2182,localhost:2183";
	private volatile List<String> serverList;

	public void connectZookeeper() throws IOException {
		zk = new ZooKeeper(connectString, 5000, new ClientWatch());
	}

	class ClientWatch implements Watcher {

		public void process(WatchedEvent event) {
			logger.info("clientWatch---"+event.getPath()+"|"+event.getState());
			if (event.getType() == EventType.NodeChildrenChanged
					&& ("/" + groupNode).equals(event.getPath())) {
					try {
						updateServerList();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			 try {
				updateServerList();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
	}
	/**
	 * 更新server列表
	 * @throws Exception
	 */
	public void updateServerList() throws Exception {
		List<String> newServerlist = new ArrayList<String>();
		/**
		 * 获取并监听groupnode的子节点变化
		 * watch的参数为true，表示监听子节点变化事件
		 * 每次都需要重新注册监听个，因为一次注册，只能监听一次事件，如果还想继续保持监听，必须重新注册
		 */
		List<String> sublist = zk.getChildren("/" + groupNode, true);
		for (String string : sublist) {
			byte[] data = zk
					.getData("/" + groupNode + "/" + string, true, stat);
			newServerlist.add(new String(data, "UTF-8"));
		}
		//替换server列表
		serverList = newServerlist;
		
		System.out.println("server list updateed:"+serverList);
	}
	
	public void handle() throws InterruptedException{
		Thread.sleep(Long.MAX_VALUE);
	}
	
	
	public static void main(String[] args) {
		ZkServer cl = new ZkServer();
		try {
			cl.connectZookeeper();
			cl.handle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
