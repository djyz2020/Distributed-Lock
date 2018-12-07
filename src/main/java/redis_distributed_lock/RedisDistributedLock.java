package redis_distributed_lock;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import junit.framework.Assert;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * redis分布式锁实现
 * @author haibozhang
 * 2018.4.16
 */
public class RedisDistributedLock implements Lock{
	
	private static final String LOCK_SUCCESS = "OK";			//加锁成功
    private static final String SET_IF_NOT_EXIST = "NX"; 		//如果不存在则加锁
    private static final String SET_WITH_EXPIRE_TIME = "PX";    //设置过期时间
    private static LinkedList<CountDownLatch> list = new LinkedList<CountDownLatch>();
    
    private String lockKey;
    private String requestId;
    private int expireTime;
    private JedisCluster jedis;

    public RedisDistributedLock() {}
    
    public RedisDistributedLock(String lockKey, String requestId, int expireTime){
    	this.jedis = RedisConnManager.getJedis();  //获取redis集群客户端
    	this.lockKey = lockKey;
    	this.requestId = requestId;
    	this.expireTime = expireTime;
    }
    
	@Override
	public void lock() {
		
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		
	}

	/**
	 * 	加锁就一行代码：jedis.set(String key, String value, String nxxx, String expx, int time)
	 * 	这个set()方法一共有五个形参：
	 *	1. 第一个为key，我们使用key来当锁，因为key是唯一的。
	 *	2. 第二个为value，我们传的是requestId，很多童鞋可能不明白，有key作为锁不就够了吗，
	 *	为什么还要用到value？原因就是我们在上面讲到可靠性时，分布式锁要满足第四个条件解铃还须系铃人，
	 *	通过给value赋值为requestId，我们就知道这把锁是哪个请求加的了，在解锁的时候就可以有依据。
	 *	requestId可以使用UUID.randomUUID().toString()方法生成。
	 *	3. 第三个为nxxx，这个参数我们填的是NX，意思是SET IF NOT EXIST，
	 *	即当key不存在时，我们进行set操作；若key已经存在，则不做任何操作；
	 *	4. 第四个为expx，这个参数我们传的是PX，意思是我们要给这个key加一个过期的设置，具体时间由第五个参数决定。
	 *	5. 第五个为time，与第四个参数相呼应，代表key的过期时间。
	 *	总的来说，执行上面的set()方法就只会导致两种结果：1. 当前没有锁（key不存在），那么就进行加锁操作，并对锁设置个有效期，
	 *	同时value表示加锁的客户端。2. 已有锁存在，不做任何操作。
	 */
	@Override
	public boolean tryLock() {
		String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }else{
        	waitForLock();
        	return tryLock();
        }
	}
	
	private void waitForLock() {
		CountDownLatch cdl = new CountDownLatch(1);
		synchronized (list) {
			list.add(cdl);
		}
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

	private static final Long RELEASE_SUCCESS = 1L;
	@Override
	public void unlock() {
		String script = "if redis.call('get' , KEYS[1]) == ARGV[1] then return redis.call('del' , KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey),
        		Collections.singletonList(requestId));
        if (RELEASE_SUCCESS.equals(result)) {
           synchronized (list) {
        	   if(list.size() > 0){
        		   CountDownLatch cdl = list.getFirst();
            	   cdl.countDown();
            	   list.remove(cdl);
        	   }
           }
        }
	}

	@Override
	public Condition newCondition() {
		return null;
	}

}


