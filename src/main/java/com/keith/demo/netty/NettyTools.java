package com.keith.demo.netty;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author keith
 * @version 1.0
 * @date 2021-02-23
 */
@Slf4j
public class NettyTools {

    /**
     * 响应消息缓存
     */
    private static final Cache<String, BlockingQueue<String>> responseMsgCache = CacheBuilder.newBuilder()
            .maximumSize(50000)
            .expireAfterWrite(1000, TimeUnit.SECONDS)
            .build();


    /**
     * 等待响应消息
     *
     * @param key 消息唯一标识
     * @return ReceiveDdcMsgVo
     */
    public static String waitReceiveMsg(String key) {

        try {
            //设置超时时间
            //poll,轮询，有输入则返回，没有则等待超时时间返回null
            String vo = Objects.requireNonNull(responseMsgCache.getIfPresent(key))
                    .poll(10000, TimeUnit.MILLISECONDS);

            //删除key
            responseMsgCache.invalidate(key);
            if (Optional.ofNullable(vo).isPresent()) {
                return vo;
            } else {
                return "设备未响应";
            }
        } catch (Exception e) {
            log.error("获取数据异常,sn={},msg=null", key);
            return "e";
        }

    }

    /**
     * 初始化响应消息的队列
     *
     * @param key 消息唯一标识
     */
    public static void initReceiveMsg(String key) {
        responseMsgCache.put(key, new LinkedBlockingQueue<>(1));
    }

    /**
     * 设置响应消息
     *
     * @param key 消息唯一标识
     */
    public static void setReceiveMsg(String key, String msg) {
        if (responseMsgCache.getIfPresent(key) != null) {
            Objects.requireNonNull(responseMsgCache.getIfPresent(key)).add(msg);
            return;
        }
        log.warn("sn {}不存在", key);
    }

}
