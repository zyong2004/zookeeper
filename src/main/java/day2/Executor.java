package day2;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import day2.DataMonitor.DataMonitorListener;

public class Executor implements Watcher, Runnable, DataMonitorListener {

	private static Logger logger = Logger.getLogger(Executor.class);
	String znode;

	DataMonitor dm;

	ZooKeeper zk;

	String filename;

	String exec[];

	Process child;

	public Executor(String znode, DataMonitor dm, ZooKeeper zk,
			String filename, String[] exec, Process child) {
		super();
		this.znode = znode;
		this.dm = dm;
		this.zk = zk;
		this.filename = filename;
		this.exec = exec;
		this.child = child;
	}

	public Executor(String hostPort, String znode, String fileName,
			String exec[]) throws IOException {
		this.filename = fileName;
		this.exec = exec;
		zk = new ZooKeeper(hostPort, 5000, this);
		dm = new DataMonitor(zk, znode, null, this);
	}

	public static void main(String[] args) {
		if (args.length < 4) {
			System.err
					.println("USAGE: Executor hostPort znode filename program [args ...]");
			System.exit(2);
		}
		String hostPort = args[0];
		String znode = args[1];
		String filename = args[2];
		String exec[] = new String[args.length - 3];
		System.arraycopy(args, 3, exec, 0, exec.length);
		try {
			new Executor(hostPort, znode, filename, exec).run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void exists(byte[] data) {

		if (data == null) {
			if (child != null) {
				logger.info("Kill process");
				child.destroy();

				try {
					child.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			child = null;
		} else {
			if (child != null) {
				logger.info("Stop child");
				child.destroy();
				try {
					child.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			fos.write(data);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("Starting child");
		try {
			child = Runtime.getRuntime().exec(exec);
			new StreamWriter(child.getInputStream(), System.out);
			new StreamWriter(child.getErrorStream(), System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closing(int rc) {
		synchronized (this) {
			notifyAll();
		}
	}

	public void run() {

		try {
			synchronized (this) {
				while (!dm.dead) {
					wait();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * We do process any events ourselves, we just need to forward them on.
	 * 
	 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.proto.WatcherEvent)
	 */

	public void process(WatchedEvent event) {
		dm.process(event);
	}

	static class StreamWriter extends Thread {
		OutputStream os;

		InputStream is;

		StreamWriter(InputStream is, OutputStream os) {
			this.is = is;
			this.os = os;
			start();
		}

		public void run() {
			byte b[] = new byte[80];
			int rc;
			try {
				while ((rc = is.read(b)) > 0) {
					os.write(b, 0, rc);
				}
			} catch (IOException e) {
			}

		}
	}

}
