package com.keith.demo;

import com.keith.demo.entity.GatewayChannel;
import com.keith.demo.mapper.gatewaychannel.GatewayChannelMapper;
import com.keith.demo.netty.GatewayServerHandler;
import com.keith.demo.script.JavaScriptUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.script.ScriptEngine;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * @author keith
 * @version 1.0
 * @date 2021-02-05
 */
@RestController
@RequestMapping("/netty/test")
public class TestController {

    @Autowired
    private GatewayChannelMapper gatewayChannelMapper;

    @PostMapping("/sendToClient")
    public void sendToClient(String gatewayCode) {
        ChannelGroup channelGroup = GatewayServerHandler.channelGroup;
        ConcurrentMap<String, ChannelId> channelMap = GatewayServerHandler.channelMap;
        GatewayChannel gatewayChannel = gatewayChannelMapper.findGatewayChannel(gatewayCode);
        if (Optional.ofNullable(gatewayChannel).map(GatewayChannel::getChannelId).isPresent()) {
            ChannelId channelId = channelMap.get(gatewayChannel.getChannelId());
            if (channelId != null) {
                ByteBuf byteBuf = Unpooled.copiedBuffer("发送指令", CharsetUtil.UTF_8);
                channelGroup.find(channelId).writeAndFlush(byteBuf);
            }
        }
    }

    @PostMapping("/script")
    public void script(String key, String path) {
        Object execute = JavaScriptUtils.execute(key, path, "fun1", "Peter Parker");
        System.out.println(execute);
    }
}
