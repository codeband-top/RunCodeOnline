package com.runcode.server.http.handler;

import cn.hutool.json.JSONObject;
import com.runcode.controller.CodeSegmentController;
import com.runcode.entities.Result;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;


/**
 * @author RhettPeng
 */
public class ApiServerHandler extends ChannelInboundHandlerAdapter {

    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof FullHttpRequest) {
            //客户端的请求对象
            FullHttpRequest req = (FullHttpRequest) msg;

            //获取客户端的URL
            String uri = req.uri();

            //根据不同的请求API做不同的处理(路由分发)
            if(req.uri().startsWith("/codeSegment"))
            {
                Result result = new CodeSegmentController().handle(req);
                responseResult(ctx,req,result);
            }else {
                responseResult(ctx,req,Result.ERROR().msg("请求路径出错！"));
            }

        }
    }

    /**
     * 响应HTTP的请求
     * @param ctx
     * @param req
     * @param result
     */
    private void responseResult(ChannelHandlerContext ctx, FullHttpRequest req , Result result)
    {
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        String json = result.toJson();
        byte[] jsonByteByte = result.toJson().getBytes();

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(jsonByteByte));
        response.headers().set(CONTENT_TYPE, "application/json;charset=utf-8");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set("Access-Control-Allow-Origin", "*");
        response.headers().set("Access-Control-Allow-Headers", "*");

        /* HTTP/1.1 持久化相关 */
        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.write(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 获取请求的内容
     * @param request
     * @return
     */
    private String parseJsonRequest(FullHttpRequest request) {
        ByteBuf jsonBuf = request.content();
        String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
        return jsonStr;
    }
}
