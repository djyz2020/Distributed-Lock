package redis_distributed_lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import redis.clients.jedis.Jedis;

public class RedisDistributedLockTest {
	
	public static int count = 100;
	public static int num = 0;
	//public static AtomicInteger num = new AtomicInteger(0);
	//public static Lock lock = new ReentrantLock();
	private static String lockKey = "test01";
	private static String requestId = "127.0.0.1";
	private static int expireTime = 1000;
	
	public static void main(String[] args) {
		Lock lock = new RedisDistributedLock(lockKey, requestId, expireTime);
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
