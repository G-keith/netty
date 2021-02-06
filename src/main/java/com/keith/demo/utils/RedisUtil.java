package com.keith.demo.utils;

import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author keith
 * @version 1.0
 * @date 2019-04-15
 */
@Component
public class RedisUtil {

    /**
     * 默认过期时长，单位：秒
     */
    public final static long DEFAULT_EXPIRE = 60 * 30;

    /**
     * 不设置过期时长
     */
    public final static long NOT_EXPIRE = -1;


    @Resource
    private ValueOperations<String, Object> valueOperations;

    @Resource
    private HashOperations<String, String, Object> hashOperations;

    @Resource
    private ListOperations<String, Object> listOperations;

    @Resource
    private SetOperations<String, Object> setOperations;

    @Resource
    private ZSetOperations<String, Object> zSetOperations;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;



    /**
     * 指定缓存失效时间
     * @param time(秒) 时间
     * @param key 键
     */
    public void expire(String key, long time){
        try {
            if(time>0){
                redisTemplate.expire(key, time,TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间 返回0代表为永久有效
     */
    public Long getExpire(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key){
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    public void del(String ... key){
        if(key!=null&&key.length>0){
            if(key.length==1){
                redisTemplate.delete(key[0]);
            }else{
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key){
        return key==null?null:valueOperations.get(key);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     */
    public void set(String key, Object value) {
        System.out.println(key+"==="+value);
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间 time要大于0 如果time小于等于0 将设置无限期
     */
    public void setWithExpire(String key,Object value,long time){
        try {
            if(time>0){
                valueOperations.set(key, value, time, TimeUnit.SECONDS);
            }else{
                set(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 普通缓存放入有效期为默认时长
     * @param key 键
     * @param value 值
     */
    public void setWithDefaultExpire(String key,Object value) {
        try {
            valueOperations.set(key, value, DEFAULT_EXPIRE, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 递增
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return 递增后的值
     */
    public Long incr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return valueOperations.increment(key, delta);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return 递减后的值
     */
    public Long decr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return valueOperations.increment(key, -delta);
    }

    //================================Map=================================
    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hGet(String key,String item){
        return hashOperations.get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<String,Object> hmGet(String key){
        return hashOperations.entries(key);
    }

    /**
     * 获取hashKey缓存长度
     * @param key 键
     * @return hashKey缓存长度
     */
    public long hGetSize(String key){
        return hashOperations.size(key);
    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmSet(String key, Map<String,Object> map){
        try {
            hashOperations.putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time(秒) 时间
     * @return true成功 false失败
     */
    public boolean hmSet(String key, Map<String,Object> map, long time){
        try {
            hashOperations.putAll(key, map);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hSet(String key,String item,Object value) {
        try {
            hashOperations.put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time(秒) 时间  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hSet(String key,String item,Object value,long time) {
        try {
            hashOperations.put(key, item, value);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hDel(String key, Object... item){
        hashOperations.delete(key,item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item){
        return hashOperations.hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return 递增后的值
     */
    public double hIncr(String key, String item,double by){
        return hashOperations.increment(key, item, by);
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return 递减后的值
     */
    public double hDecr(String key, String item,double by){
        return hashOperations.increment(key, item,-by);
    }

    //============================set=============================
    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return Set中的所有值
     */
    public Set<Object> sGet(String key){
        try {
            return setOperations.members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public Boolean sHasKey(String key,Object value){
        try {
            return setOperations.isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long sSet(String key, Object...values) {
        try {
            return setOperations.add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 将set数据放入缓存
     * @param key 键
     * @param time(秒) 时间
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long sSetAndTime(String key,long time,Object...values) {
        try {
            Long count = setOperations.add(key, values);
            if(time>0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 获取set缓存的长度
     * @param key 键
     * @return set长度
     */
    public Long sGetSetSize(String key){
        try {
            return setOperations.size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public Long setRemove(String key, Object ...values) {
        try {
            return setOperations.remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束  0 到 -1代表所有值
     * @return list值
     */
    public List<Object> lGet(String key, int start, int end){
        try {
            return listOperations.range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     * @return list长度
     */
    public Long lGetListSize(String key){
        try {
            return listOperations.size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return list值
     */
    public Object lGetIndex(String key,long index){
        try {
            return listOperations.index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return true成功，false失败
     */
    public boolean lSet(String key, Object value) {
        try {
            listOperations.rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间
     * @return true成功，false失败
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            listOperations.rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return true成功，false失败
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            listOperations.rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间
     * @return true成功，false失败
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            listOperations.rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return true成功，false失败
     */
    public boolean lUpdateIndex(String key, long index,Object value) {
        try {
            listOperations.set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public Long lRemove(String key,long count,Object value) {
        try {
            return listOperations.remove(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 模糊查询所有key值
     * @param key 键
     * @return 模糊查询所有的key值
     */
    public Set<String> findKey(String key){
        try {
            return redisTemplate.keys("*"+key+"*");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 设置超时时间
     * @param key key值
     * @param expire 超时时间
     */
    public void setExpire(String key, long expire){
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    /**
     * 设置超时时间（默认时长）
     * @param key key值
     */
    public void setExpire(String key){
        redisTemplate.expire(key, DEFAULT_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 从redis获取值
     * @param key key值
     * @param clazz 对象类型
     * @param expire 超时时间
     * @param <T> 对象类型
     * @return 对象实体
     */
    public <T> T get(String key, Class<T> clazz, long expire) {
        String value =(String) valueOperations.get(key);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    /**
     * 从redis获取对象（永久有效的值）
     * @param key key值
     * @param clazz 对象类型
     * @param <T> 对象类型
     * @return 对象实体
     */
    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    /**
     * 从redis获取值
     * @param key key值
     * @param expire 超时时间
     * @return String
     */
    public String get(String key, long expire) {
        String value =(String)  valueOperations.get(key);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    /**
     * 从redis删除
     * @param key key值
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 对象转json字符串
     * @param object 对象实体
     * @return String
     */
    private String toJson(Object object){
        if(object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String){
            return String.valueOf(object);
        }

        return JSONUtil.toJsonStr(object);
    }

    /**
     * json字符串转对象
     * @param json json字符串
     * @param clazz 对象类
     * @param <T> 对象类型
     * @return 对象实体
     */
    private <T> T fromJson(String json, Class<T> clazz){
        return JSONUtil.toBean(json, clazz);
    }

    /**
     * 获取今天的递增值
     * @param key key值
     * @return int
     */
    public synchronized Integer getTodayIncreaseVal(String key) {
        Integer val;
        if (redisTemplate.hasKey(key)) {
            // 自增
            valueOperations.increment(key);
            val = (Integer) valueOperations.get(key);
        } else {
            val = 1;
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime tomorrow = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
            long expire = now.until(tomorrow, ChronoUnit.SECONDS);
            setWithExpire(key, val, expire);
        }
        return val;
    }
}
