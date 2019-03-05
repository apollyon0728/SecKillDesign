import com.scut.seckill.SecKillApp;
import com.scut.seckill.cache.RedisCacheHandle;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import tool.RedisTool;

import java.util.UUID;

/**
 * Redis 分布式锁测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecKillApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class RedisDistributedLockTest {
    private static final String LOCK_KEY = "TEST_LOCK_KEY";
    private static final String LOCK_KEY_VALUE = "TEST_LOCK_KEY_VALUE";
    private static final String UUID_VALUE = UUID.randomUUID().toString();

    @Autowired
    private RedisCacheHandle redisCacheHandle;


    @Test
    public void redisLockTest(){
        Jedis jedis = redisCacheHandle.getJedis();
        if (jedis.exists(LOCK_KEY)) {
            log.info(">>>>>>>>>> redisLockTest key exists : {}", jedis.exists(LOCK_KEY));
        } else {
            boolean isSuccess = RedisTool.tryGetDistributedLock(jedis, LOCK_KEY, UUID_VALUE, 10);
            log.info(">>>>>>>>>> redisLockTest 加锁是否成功， isSuccess : {}", isSuccess);
            log.info(">>>>>>>>>> redisLockTest 加锁后是否存在该KEY， exist : {}", jedis.exists(LOCK_KEY));
        }
    }

    @Test
    public void redisUnLockTest(){
        Jedis jedis = redisCacheHandle.getJedis();
        if (jedis.exists(LOCK_KEY)) {
            log.info(">>>>>>>>>> redisUnLockTest key exists : {}", jedis.exists(LOCK_KEY));
        } else {
            log.info(">>>>>>>>>> redisUnLockTest key not exists : {}", jedis.exists(LOCK_KEY));
            return;
        }
        boolean isSuccess = RedisTool.releaseDistributedLock(jedis, LOCK_KEY, UUID_VALUE);
        log.info(">>>>>>>>>> redisUnLockTest isSuccess : {}", isSuccess);
    }

    @Test
    public void redisAboutLockTest(){
        redisLockTest();
//        redisUnLockTest();
    }


}
