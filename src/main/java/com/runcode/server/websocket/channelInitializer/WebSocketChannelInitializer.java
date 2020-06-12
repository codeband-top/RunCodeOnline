package com.runcode.server.websocket.channelInitializer;

import com.runcode.server.websocket.handler.TextWebsocketFrameHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author RhettPeng
 */
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        //向管道加入处理器
        //得到管道
        ChannelPipeline pipeline = ch.pipeline();
        //基于HTTP的编解码器
        pipeline.addLast(new HttpServerCodec());
        //以块方式传输数据
        pipeline.addLast(new ChunkedWriteHandler());
        //HTTP数据在传输过程中是分段，HttpObjectAggregator可以将多个段聚合
        pipeline.addLast(new HttpObjectAggregator(1024));
        //将http协议升级为websocket协议，参数代表请求的uri
        pipeline.addLast(new WebSocketServerProtocolHandler("/runcode"));
        pipeline.addLast(new TextWebsocketFrameHandler());
    }
}
