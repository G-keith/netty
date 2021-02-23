package com.keith.demo.controller;

import com.keith.demo.entity.GatewayChannel;
import com.keith.demo.mapper.gatewaychannel.GatewayChannelMapper;
import com.keith.demo.netty.GatewayServerHandler;
import com.keith.demo.netty.NettyTools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;

import io.netty.util.CharsetUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * @author keith
 * @version 1.0
 * @date 2021-02-23
 */
@Slf4j
@RestController
@RequestMapping("/netty/send")
public class SendClientController {

    @Autowired
    private GatewayChannelMapper gatewayChannelMapper;

    @GetMapping()
    public String addTimeJob(String name) {
        ChannelGroup channelGroup = GatewayServerHandler.channelGroup;
        ConcurrentMap<String, ChannelId> channelMap = GatewayServerHandler.channelMap;
        GatewayChannel gatewayChannel = gatewayChannelMapper.findGatewayChannel(name);
        if (Optional.ofNullable(gatewayChannel).isPresent()) {
            System.out.println("执行下发指令操作");
            ChannelId channelId = channelMap.get(gatewayChannel.getChannelId());
            if (channelId != null) {
                //异步发送，获取返回值
                ByteBuf byteBuf = Unpooled.copiedBuffer("发送指令", CharsetUtil.UTF_8);
                channelGroup.find(channelId).writeAndFlush(byteBuf);
                //等待并获取服务端响应
                NettyTools.initReceiveMsg(gatewayChannel.getChannelId());
                return NettyTools.waitReceiveMsg(gatewayChannel.getChannelId());
            } else {
                return "网关离线当中~~~";
            }
        } else {
            log.warn("网关离线当中~~~");
            return "网关离线当中~~~";
        }
    }
}
