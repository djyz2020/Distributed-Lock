package redis_distributed_lock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sun.org.apache.regexp.internal.recompile;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class RedisConnManager {
	
	public static JedisCluster jedis;
	public static Jedis jedis0;
	
	public static JedisCluster getJedis(){
		if(jedis == null){
			Set<HostAndPort> nodes = new HashSet<HostAndPort>();
			for(int i = 0; i < 6; i++){
				nodes.add(new HostAndPort("192.168.106.130", 7001 + i));
			}
			jedis = new JedisCluster(nodes);
		}
		return jedis;
	}
	
	public static Jedis getJedis0(){
		if(jedis0 == null){
			jedis0 = new Jedis("192.168.106.130", 6379);
		}
		return jedis0;
	}
	
	public static void main(String[] args) {
		Jedis jedis = new Jedis("192.168.106.130", 6379);
		//stringTest(jedis);
		//hashTest(jedis);
		//listTest(jedis);
		//setTest(jedis);
		//zSetTest(jedis);
	}
	
	//redis String 示例
	public static void stringTest(Jedis jedis){
		jedis.set("20180418", "20180418");
		System.out.println(jedis.get("20180418"));
	}
	
	//redis hash 示例
	public static void hashTest(Jedis jedis){
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "111");
		map.put("2", "222");
		jedis.hmset("hashTest", map);
		System.out.println(jedis.hget("hashTest", "1"));
	}
	
	//redis list 示例
	public static void listTest(Jedis jedis){
		jedis.lpush("haibozhang", "11");
		jedis.lpush("haibozhang", "22");
		jedis.lpush("haibozhang", "33");
		jedis.lpush("haibozhang", "44");
		System.out.println(jedis.lrange("haibozhang", 0, 100));
	}
	
	//redis Set 示例
	public static void setTest(Jedis jedis){
		jedis.sadd("setTest", "redis");
		jedis.sadd("setTest", "mongdb");
		jedis.sadd("setTest", "rabitmq");
		jedis.sadd("setTest", "activemq");
		System.out.println(jedis.smembers("setTest"));
	}
	
	//redis zSet示例
	public static void zSetTest(Jedis jedis){
		jedis.zadd("zsetTest", 1, "redis");
		jedis.zadd("zsetTest", 2, "mongdb");
		jedis.zadd("zsetTest", 5, "rabitmq");
		jedis.zadd("zsetTest", 3, "activemq");
		System.out.println(jedis.zrange("zsetTest", 0, 100));
	}

}
