package com.keith.demo.task;

import com.keith.demo.netty.GatewayServerHandler;
import com.keith.demo.entity.GatewayChannel;
import com.keith.demo.mapper.gatewaychannel.GatewayChannelMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.CharsetUtil;
import lombok.Data;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * @author keith
 * @version 1.0
 * @date 2021-02-06
 */
@Data
public class GatewayJob extends QuartzJobBean {

    private String name;

    @Autowired
    private GatewayChannelMapper gatewayChannelMapper;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        ChannelGroup channelGroup = GatewayServerHandler.channelGroup;
        ConcurrentMap<String, ChannelId> channelMap = GatewayServerHandler.channelMap;
        System.out.println("execute timeJob at " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")) +
                ": hello " + this.name);
        GatewayChannel gatewayChannel = gatewayChannelMapper.findGatewayChannel(name);
        if (Optional.ofNullable(gatewayChannel).isPresent()) {
            System.out.println("执行下发指令操作");
            ChannelId channelId = channelMap.get(gatewayChannel.getChannelId());
            if (channelId != null) {
                ByteBuf byteBuf = Unpooled.copiedBuffer("发送指令", CharsetUtil.UTF_8);
                channelGroup.find(channelId).writeAndFlush(byteBuf);
            }
        }
    }

}