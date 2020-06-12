package com.runcode.docker;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * 接收到docker信息的回调
 * @author RhettPeng
 */
@Slf4j
public class RunCodeResultCallback extends ResultCallbackTemplate<ExecStartResultCallback, Frame> {

    /**
     * websocket客户端的channel
     */
    private ChannelHandlerContext ctx;
    /**
     * 是否是程序执行的最后一条指令
     */
    private boolean isFinal;
    /**
     * 开始执行的时间戳
     */
    private long startTime;

    public RunCodeResultCallback(ChannelHandlerContext ctx,boolean isFinal) {
        this.ctx = ctx;
        this.isFinal = isFinal;
        if(isFinal) {
            startTime = System.currentTimeMillis();
        }
    }

    /**
     * 接收到一条Docker响应后，返回给Websocket客户端
     * @param frame
     */
    @Override
    public void onNext(Frame frame) {
        log.info("收到docker响应");
        if (frame != null) {
            String msg = new String(frame.getPayload());
            switch (frame.getStreamType()) {
                case STDOUT:
                case RAW:
                case STDERR:
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(msg));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 若当前指令执行完成，则发送最后一条消息
     */
    @Override
    public void onComplete() {
        if(isFinal){
            long endTime = System.currentTimeMillis();
            ctx.channel().writeAndFlush(new TextWebSocketFrame("程序运行结束，总耗费时间："+(endTime-startTime)/1000.0+"s\n"));
            log.info("程序运行结束，发送最后一帧");
        }
        super.onComplete();
    }
}
