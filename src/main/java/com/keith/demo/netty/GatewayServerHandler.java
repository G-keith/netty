package com.keith.demo.netty;

import com.keith.demo.entity.GatewayChannel;
import com.keith.demo.mapper.gatewaychannel.GatewayChannelMapper;
import com.keith.demo.utils.RedisUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * handlerAdded -> channelRegistered
 * -> channelActive -> channelRead -> channelReadComplete
 * -> channelInactive -> channelUnRegistered -> handlerRemoved
 * handlerAdded: 新建立的连接会按照初始化策略，把handler添加到该channel的pipeline里面，也就是channel.pipeline.addLast(new LifeCycleInBoundHandler)执行完成后的回调；
 * channelRegistered: 当该连接分配到具体的worker线程后，该回调会被调用。
 * channelActive：channel的准备工作已经完成，所有的pipeline添加完成，并分配到具体的线上上，说明该channel准备就绪，可以使用了。
 * channelRead：客户端向服务端发来数据，每次都会回调此方法，表示有数据可读；
 * channelReadComplete：服务端每次读完一次完整的数据之后，回调该方法，表示数据读取完毕；
 * channelInactive：当连接断开时，该回调会被调用，说明这时候底层的TCP连接已经被断开了。
 * channelUnREgistered: 对应channelRegistered，当连接关闭后，释放绑定的workder线程；
 * handlerRemoved： 对应handlerAdded，将handler从该channel的pipeline移除后的回调方法。
 *
 * @author keith
 * @version 1.0
 * @date 2021-01-25
 */
@Component
public class GatewayServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Autowired
    private GatewayChannelMapper gatewayChannelMapper;

    @Autowired
    private RedisUtil redisUtil;

    public static GatewayServerHandler gatewayServerHandler;

    @PostConstruct
    public void init() {
        gatewayServerHandler = this;
    }

    /**
     * 定义一个channel组，管理所有的channel
     * GlobalEventExecutor 全局事件执行器，是一个单例
     */
    public static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static final ConcurrentMap<String, ChannelId> channelMap = new ConcurrentHashMap();

    /**
     * 连接建立，第一个被执行
     *
     * @param ctx 上下文对象
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        if (!channelGroup.contains(channel)) {
            //将该客户端加入聊天信息推送给其他客户端
            channelGroup.add(channel);
        }
        channelMap.put(channel.id().asLongText(), channel.id());
    }

    /**
     * 表示channel处于活动状态
     *
     * @param ctx 上下文对象
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress() + "上线了");
    }

    /**
     * 表示channel处于非活动状态
     *
     * @param ctx 上下文对象
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress() + "离线了");
        channelMap.remove(ctx.channel().id().asLongText());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        //todo 需要考虑拆包粘包问题
        Channel channel = ctx.channel();
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        String content = new String(bytes, CharsetUtil.UTF_8).trim();
        if (content.startsWith("g")) {
            System.out.println("content:" + content);
            //插入通道和网关关系
            GatewayChannel gatewayChannel = gatewayServerHandler.gatewayChannelMapper.findGatewayChannel(content);
            if (gatewayChannel != null) {
                gatewayServerHandler.gatewayChannelMapper.updateGatewayCode(channel.id().asLongText(), content);
            } else {
                gatewayServerHandler.gatewayChannelMapper.insertGatewayChannel(channel.id().asLongText(), content);
            }
        } else {
            //根据指令执行脚本
            //todo 对线程或者队列去处理消息，防止阻塞
            ctx.channel().eventLoop().execute(() -> {
                System.out.println(content);
            });
        }

    }

    /**
     * 表示channel断开连接
     *
     * @param ctx 上下文对象
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        //断开连接
        gatewayServerHandler.gatewayChannelMapper.deleteGatewayChannel(channel.id().asLongText());
        channelMap.remove(channel.id().asLongText());
        channelGroup.remove(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause.getMessage());
        //发生异常，关闭通道
        Channel channel = ctx.channel();
        gatewayServerHandler.gatewayChannelMapper.deleteGatewayChannel(channel.id().asLongText());
        channelMap.remove(channel.id().asLongText());
        ctx.close();
    }
}
