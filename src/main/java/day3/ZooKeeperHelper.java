package day3;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.apache.log4j.Logger;


/**
 * {@link http://blog.csdn.net/jacktan/article/details/6112806}
 * @author zhangyong
 *
 */
public class ZooKeeperHelper {

	
	private static Logger logger = Logger.getLogger(ZooKeeperHelper.class);
	private static String hosts;
	private static ExecutorService pool = Executors.newCachedThreadPool();
	
	private static final String GROUP_NAME ="/SESSIONS";

}
