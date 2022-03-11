package com.lhjitem.seckilldemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillDemoApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisScript<Boolean> redisScript;

    @Test
    public void lockTest01() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //占位，setIfAbsent就是有这个k就设置失败，没有就成功
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1");
        //如果占位成功进行正常操作
        if(isLock){
            valueOperations.set("name", "xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println(name);

            //操作结束删除锁
            redisTemplate.delete("k1");
        }else {
            System.out.println("有线程在使用请稍候");
        }
    }

    /**
     * 如果此时在释放锁之前发生了异常，锁还在被占用，那么以后的线程想要访问这个资源都会失败，造成死锁的状态
     * 解决方法就是设置锁自动的失效时间，一般设置的比该线程操作时间多一点，但是注意这个线程可能因为网络等原因
     * 进行了耗时操作，最后完成时间甚至比锁失效时间还长，这就是导致时间上的错位，等耗时操作完成后可能会把下一个
     * 线程的锁进行删除，这时可以将锁的value值也进行比较，对value取随机数，每次删除锁之前比较value值，
     * 一致则是自己的在进行删除，但这些操作合起来是不具备原子性的，又会引发多线程问题，那么此时引入lua脚本
     */
    @Test
    public void lockTest02() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //占位，setIfAbsent就是有这个k就设置失败，没有就成功
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1",5, TimeUnit.SECONDS);
        //如果占位成功进行正常操作
        if(isLock){
            valueOperations.set("name", "xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println(name);
            Integer.parseInt("xxxxx");
            //操作结束删除锁
            redisTemplate.delete("k1");
        }else {
            System.out.println("有线程在使用请稍候");
        }
    }


    /**
     * 这个测试就是测试lua脚本
     */
    @Test
    public void lockTest03() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        //占位，setIfAbsent就是有这个k就设置失败，没有就成功
        Boolean isLock = valueOperations.setIfAbsent("k1", value,5, TimeUnit.SECONDS);
        //如果占位成功进行正常操作
        if(isLock){
            valueOperations.set("name", "xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println(name);
            System.out.println(valueOperations.get("k1"));
            Boolean result = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"), value);
            System.out.println(result);
            //操作结束删除锁
//            redisTemplate.delete("k1");
        }else {
            System.out.println("有线程在使用请稍候");
        }
    }
}
