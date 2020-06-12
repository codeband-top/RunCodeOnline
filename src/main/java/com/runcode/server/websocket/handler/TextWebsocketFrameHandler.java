package com.runcode.server.websocket.handler;

import com.runcode.docker.DockerJavaClient;
import com.runcode.entities.CodeLang;
import com.runcode.entities.CodeWrapperDTO;
import com.runcode.utils.CodeWrapperUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * Websocket帧处理器
 * @author RhettPeng
 */
@Slf4j
public class TextWebsocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    private DockerJavaClient dockerJavaClient = new DockerJavaClient();

    /**
     * 读取Websocket客户端发来的信息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        try{
            CodeWrapperDTO codeWrapper = CodeWrapperUtil.fromJson(msg.text());
            log.info("收到客户端信息："+msg.text());
            dockerJavaClient.exec(CodeLang.valueOf(codeWrapper.getLangType().toUpperCase()),codeWrapper.getContent(),ctx);
        }catch (Exception e){
            log.warn("执行过程中出现异常：");
            e.printStackTrace();
            ctx.channel().writeAndFlush(new TextWebSocketFrame("发生意外，运行出错！"));
        }
    }


    /**
     * 建立连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("与客户端建立连接");
    }

    /**
     * 连接关闭
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("与客户端断开连接");
    }


    /**
     * 捕获异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常发生"+cause.getMessage());
        ctx.close();//关闭连接
    }
}
