package zk_distributed_lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * zk非竞争锁
 * @author haibozhang
 * 2018.4.16
 */
public class ZkDistributedLockTest {
	
	public static int count = 100;
	public static int num = 0;
	//public static AtomicInteger num = new AtomicInteger(0);
	//public static Lock lock = new ReentrantLock(); //可重入锁
	//public static Lock lock = new ZkDistributedLock01("/zkDistributedLock01");   //竞争锁
	public static Lock lock = new ZkDistributedLock02("/zkDistributedLock");	   //非竞争锁
	
	public static void main(String[] args) {
		CountDownLatch cdl = new CountDownLatch(1);
		
		while(count > 0){
			count--;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						cdl.await();
						Thread.sleep(10);
						//codeGenerator(num.incrementAndGet());
						if(lock.tryLock()){
							codeGenerator(num++);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock.unlock();
					}
				}
			}).start();
		}
		cdl.countDown();
	}
	
	public static void codeGenerator(int num){
		System.out.println("生产数字: ====> " + num);
	}

}
