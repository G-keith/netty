package com.keith.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author keith
 * @version 1.0
 * @date 2021-01-25
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 定义一个channel组，管理所有的channel
     * GlobalEventExecutor 全局事件执行器，是一个单例
     */
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

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
            channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "加入聊天");
            channelGroup.add(channel);
        }
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
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        Channel channel = ctx.channel();
        //遍历channelGroup，根据不同情况，回送不同消息
        channelGroup.forEach(ch -> {
            if (channel != ch) {
                //不是当前channel
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + "说：" + msg);
            } else {
                ch.writeAndFlush("发送成功");
            }
        });
    }

    /**
     * 表示channel断开连接
     *
     * @param ctx 上下文对象
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        //将该客户端离开聊天信息推送给其他客户端
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "离开了");
        //自动从channelGroup移除掉 channelGroup.remove(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //发生异常，关闭通道
        ctx.close();
    }
}
