package cc.mrbird.febs.common.service.impl;

import cc.mrbird.febs.common.domain.RedisInfo;
import cc.mrbird.febs.common.exception.RedisConnectException;
import cc.mrbird.febs.common.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 工具类，只封装了几个常用的 redis 命令，
 * 可根据实际需要按类似的方式扩展即可。
 *
 * @author MrBird
 */
@Service("redisService")
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    private static String separator = System.getProperty("line.separator");

    @Override
    public List<RedisInfo> getRedisInfo() throws RedisConnectException{
        try{

        }catch (Exception e){
            throw new RedisConnectException(e.getMessage());
        }
        String info = redisTemplate.execute((RedisCallback<String>) redisConnection -> redisConnection.execute("info").toString());
        List<RedisInfo> infoList = new ArrayList<>();
        String[] strs = Objects.requireNonNull(info).split(separator);
        RedisInfo redisInfo;
        if (strs.length > 0) {
            for (String str1 : strs) {
                redisInfo = new RedisInfo();
                String[] str = str1.split(":");
                if (str.length > 1) {
                    String key = str[0];
                    String value = str[1];
                    redisInfo.setKey(key);
                    redisInfo.setValue(value);
                    infoList.add(redisInfo);
                }
            }
        }
        return infoList;
    }

    @Override
    public Map<String, Object> getKeysSize() {
        Long dbSize = redisTemplate.execute((RedisCallback<Long>) redisConnection -> redisConnection.dbSize());
        Map<String, Object> map = new HashMap<>();
        map.put("create_time", System.currentTimeMillis());
        map.put("dbSize", dbSize);
        return map;
    }

    @Override
    public Map<String, Object> getMemoryInfo() {
        String info = redisTemplate.execute((RedisCallback<String>) redisConnection -> redisConnection.execute("info").toString());
        String[] strs = Objects.requireNonNull(info).split(separator);
        Map<String, Object> map = null;
        for (String s : strs) {
            String[] detail = s.split(":");
            if ("used_memory".equals(detail[0])) {
                map = new HashMap<>();
                map.put("used_memory", detail[1].substring(0, detail[1].length() - 1));
                map.put("create_time", System.currentTimeMillis());
                break;
            }
        }
        return map;
    }

    @Override
    public Set<String> getKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void set(String key, String value) {
         redisTemplate.opsForValue().set(key,value);
    }

    @Override
    public void set(String key, String value, Long milliscends) {
        redisTemplate.opsForValue().set(key,value,milliscends, TimeUnit.MILLISECONDS);
    }

    @Override
    public Long del(String... key) {
        return redisTemplate.delete(Arrays.asList(key));
    }

    @Override
    public Boolean exists(String key) {
        return redisTemplate.execute((RedisCallback<Boolean>) redisConnection ->{
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            return redisConnection.exists(serializer.serialize(key));
        });
    }

    @Override
    public Long pttl(String key) {
        return redisTemplate.execute((RedisCallback<Long>) redisConnection ->{
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            return redisConnection.pTtl(serializer.serialize(key));
        });
    }

    @Override
    public Boolean pexpire(String key, Long milliseconds) {
        return redisTemplate.expire(key,milliseconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public Boolean zadd(String key, Double score, String member) {
        return redisTemplate.opsForZSet().add(key,member,score);
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        return redisTemplate.opsForZSet().rangeByScore(key, Double.parseDouble(min),Double.parseDouble(max));
//        return redisTemplate.execute((RedisCallback<Set<String>>) redisConnection ->{
//            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
//            return redisConnection.zRangeByScore(serializer.serialize(key),min,max).stream().map(b->serializer.deserialize(b)).collect(Collectors.toSet());
//        });
    }

    @Override
    public Long zremRangeByScore(String key, String start, String end) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, Double.parseDouble(start),Double.parseDouble(end));
//        return redisTemplate.execute((RedisCallback<Long>) redisConnection ->{
//            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
//            return redisConnection.zRemRangeByScore(serializer.serialize(key), RedisZSetCommands.Range.range().gte(start).lte(end));
//        });
    }

    @Override
    public Long zrem(String key, String... members) {
        return redisTemplate.opsForZSet().remove(key,members);
    }

}
