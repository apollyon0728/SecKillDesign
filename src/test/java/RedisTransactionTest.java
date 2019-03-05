import com.scut.seckill.SecKillApp;
import com.scut.seckill.cache.RedisCacheHandle;
import com.scut.seckill.cache.RedisLoaderListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Redis 事务测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecKillApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class RedisTransactionTest {
    private static final String goodsKey = "iphone_stock"; // 监视的key 当前秒杀商品的数量

    @Autowired
    private RedisCacheHandle redisCacheHandle;


//    @Test
    public void redisTransactionTest() {
        Jedis jedis = redisCacheHandle.getJedis();
        jedis.watch(goodsKey);
        int iphoneCount = Integer.parseInt(jedis.get(goodsKey)); // 当前剩余商品数量
        if (iphoneCount <= 0) {
            log.info("商品已抢完，" + iphoneCount + "---> 抢购失败");
        }
        Transaction tran = jedis.multi();
        tran.incrBy(goodsKey, -1);
        List<Object> exec = tran.exec(); // 执行事务
        if (CollectionUtils.isEmpty(exec)) {
            log.info(goodsKey + "---> 抢购失败");
        } else {
            log.info(goodsKey + "---> 抢购成功");
        }
        jedis.unwatch();
    }

    /**
     * 1000 个线程抢购100个商品
     *
     *  {@link RedisLoaderListener} @PostConstruct 方法在启动时
     *  每次从数据库读取数据，重新初始化Redis的值
     */
    @Test
    public void threadSecondKillTest() throws InterruptedException, IOException {
        /** 初始化商品 */
//        initGoods();

        ExecutorService executorService  = Executors.newFixedThreadPool(20);
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i <= 1000; i++) {
            Jedis jedis = redisCacheHandle.getJedis();
            executorService.execute(new SecondKillHandler(Thread.currentThread().getName() + i, jedis));
            countDownLatch.countDown();
        }
        executorService.shutdown();
        log.info("countDownLatch.getCount() >>>>>> " + String.valueOf(countDownLatch.getCount()));
        countDownLatch.await();
        long time = System.currentTimeMillis() - startTime;
        System.out.println("共耗时：" + time + "毫秒");
        // JedisPoolUtil.close();
    }

    /** 初始化商品数量 */
    private void initGoods() {
        Jedis jedis = redisCacheHandle.getJedis();
        jedis.set(goodsKey, "100"); // 设置100个商品
    }


}
