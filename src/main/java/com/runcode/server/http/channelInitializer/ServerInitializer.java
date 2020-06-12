package com.runcode.server.http.channelInitializer;

import com.google.protobuf.Api;
import com.runcode.server.http.handler.ApiServerHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * HTTP服务器通道初始化
 * @author RhettPeng
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        /*HTTP 服务的解码器*/
        p.addLast(new HttpServerCodec());
        /*HTTP 消息的合并处理*/
        p.addLast(new HttpObjectAggregator(2048));
        p.addLast(new ApiServerHandler());
    }
}
