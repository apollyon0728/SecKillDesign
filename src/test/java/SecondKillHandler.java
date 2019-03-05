import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

@Slf4j
public class SecondKillHandler implements Runnable {
    private Jedis jedis;
    private String userName;
    private static final String goodsKey = "iphone_stock"; // 监视的key 当前秒杀商品的数量

    public SecondKillHandler(String userName, Jedis jedis) {
        this.userName = userName;
        this.jedis = jedis;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // watch 监视一个key，当事务执行之前这个key发生了改变，事务会被打断
//                jedis.watch(goodsKey);
                int currentGoodsCount = Integer.parseInt(jedis.get(goodsKey)); // 当前剩余商品数量
                log.info("当前 {} 剩余商品数量，{}",goodsKey, currentGoodsCount);
                if (currentGoodsCount <= 0) {
                    log.info("商品已抢完，" + userName + "---> 抢购失败 XXX");
                    break;
                }
                Transaction tran = jedis.multi(); // 开启事务
                tran.incrBy(goodsKey, -1); // 商品数量-1
                List<Object> exec = tran.exec(); // 执行事务
                if (CollectionUtils.isEmpty(exec)) {
                    log.info(userName + "---> 抢购失败，继续抢购");
                    Thread.sleep(1);
                } else {
                    exec.forEach(success -> {
                        String successStr = userName + "===========================> 抢购到第【" + ((100 - currentGoodsCount) + 1) + "】份商品，该商品剩余：" + success.toString();
                        log.info(successStr);
                        jedis.set("goodsResult:" + userName, successStr); // 业务代码，处理抢购成功
                    });
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jedis != null) {
//                    jedis.unwatch();
                }
            }
        }
    }
}
