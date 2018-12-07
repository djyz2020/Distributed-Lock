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
 * 2018.4.16
 * @author haibozhang
 */
public class ZkDistributedLock02 implements Lock{
	public static final String LOCKDATA = "DATA";	 //锁节点数据
	public static final String CHILDNODENAME = "YY"; //子节点名称
	
	/**
	 * 利用zookeeper的同父子节点不可重名的特点来实现分布式锁
	 * 加锁：创建指定名称的节点，如果创建成功（加锁成功），如果节点已经存在，则标识锁被被人占用
	 * 释放锁： 删除指定名称的节点
	 */
	private String LockPath;  	//锁节点
	private ZkClient zkClient; 	//zk客户端
	private ThreadLocal<String> currentPath = new ThreadLocal<String>();    //当前节点
	private ThreadLocal<String> beforePath = new ThreadLocal<String>(); 	//前一节点
	
	public ZkDistributedLock02() {}
	
	public ZkDistributedLock02(String LockPath){
		this.LockPath = LockPath;
		zkClient = new ZkClient("master.bigdata.com:2181");
		if(!zkClient.exists(LockPath)){
			zkClient.createPersistent(LockPath);
		}
		zkClient.setZkSerializer(new Serializer());
	}
	
	@Override
	public void lock() {}

	@Override
	public void lockInterruptibly() throws InterruptedException {}

	@Override
	public boolean tryLock() {
		
		//方法一: 容易造成死锁
		/*try {
			zkClient.createPersistent(LockPath);
		} catch (Exception e) {
			waitForLock();
			tryLock();
		}*/
		
		//方法二: 更可靠
		if(this.currentPath.get() == null){
			currentPath.set(zkClient.createEphemeralSequential(LockPath + "/" + CHILDNODENAME, LOCKDATA));
		}
		List<String> children = this.zkClient.getChildren(LockPath);
		Collections.sort(children);
		if(currentPath.get().equals(LockPath + "/" + children.get(0))){
			System.out.println(LockPath + "/" + children.get(0) + " => 加锁");
			return true;
		}else{
			int currentIndex = children.indexOf(currentPath.get().substring(LockPath.length() + 1));
			beforePath.set(LockPath + "/" + children.get(currentIndex - 1)); 
			//System.out.println("当前节点: " + currentPath.get() + ", 监听的节点" + beforePath.get());
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
				System.out.println("................................................");
			}
			
			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {}
		};
		zkClient.subscribeDataChanges(beforePath.get(), listener); //注册节点变化监听器
		try {
			cdl.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return false;
	}

	@Override
	public void unlock() {
		if(zkClient.exists(currentPath.get())){
			zkClient.delete(currentPath.get());
			System.out.println(currentPath.get() + "解锁");
		}
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
