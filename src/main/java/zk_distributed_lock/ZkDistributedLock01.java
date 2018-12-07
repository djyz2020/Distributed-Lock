package zk_distributed_lock;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * zk竞争锁
 * @author haibozhang
 * 2018.4.16
 */
public class ZkDistributedLock01 implements Lock{
	public static final String LOCKDATA = "DATA";	//锁节点数据
	
	/**
	 * 利用zookeeper的同父子节点不可重名的特点来实现分布式锁
	 * 加锁：创建指定名称的节点，如果创建成功（加锁成功），如果节点已经存在，则标识锁被被人占用
	 * 释放锁： 删除指定名称的节点
	 */
	private String LockPath;  	//锁节点
	private ZkClient zkClient; 	//zk客户端
	
	public ZkDistributedLock01() {}
	
	public ZkDistributedLock01(String LockPath){
		this.LockPath = LockPath;
		zkClient = new ZkClient("master.bigdata.com:2181");
		zkClient.setZkSerializer(new Serializer());
	}
	
	@Override
	public void lock() {}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean tryLock() {
		
		try {
			zkClient.createPersistent(LockPath);
			return true;
		} catch (Exception e) {
			waitForLock();
			return tryLock();
		}
	}
	
	private void waitForLock() {
		CountDownLatch cdl = new CountDownLatch(1);
		IZkDataListener listener = new IZkDataListener() {
			
			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				System.out.println(dataPath + " =>节点被删除");
				cdl.countDown();
			}
			
			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {}
		};
		zkClient.subscribeDataChanges(LockPath, listener); //注册节点变化监听器
		try {
			cdl.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unlock() {
		if(zkClient.exists(LockPath)){
			zkClient.delete(LockPath);
		}
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
