# Distributed-Lock
分布式锁的实现主要有redis和zookeeper两种方式：
(1) redis的实现属于竞争锁实现，zookeeper的实现属于非竞争锁实现；
(2) redis锁还是有死锁的可能，zookeeper几乎没有死锁的可能。
