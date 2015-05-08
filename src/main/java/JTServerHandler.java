package com.alex.jungletiger;

import java.util.concurrent.*;
import java.io.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import org.msgpack.annotation.*;
import org.msgpack.*;

public class JTServerHandler extends ChannelInboundHandlerAdapter 
{
    private ArrayBlockingQueue mq;

    public JTServerHandler(ArrayBlockingQueue mq)
    {
        this.mq = mq;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception 
    {
        // System.out.println("active:" + ctx.channel().remoteAddress().toString());
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) 
    {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException
    {
        try 
        {
            MessagePack msgpack = new MessagePack();
            JTInternalMessage message = new JTInternalMessage();
            message.requestMessage = msgpack.read((byte[]) msg, JTRequestTMessage.class);
            message.ctx = ctx;
            mq.put(message);
        }
        catch (InterruptedException ie)
        {
            ByteBuf rep = Unpooled.copiedBuffer(ie.getMessage().getBytes());
            ctx.write(rep);
        }
        /*
        System.out.println("get");
        ByteBuf rep = Unpooled.copiedBuffer("ok".getBytes());
        ctx.write(rep);
        */
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
    {
        cause.printStackTrace();
        ctx.close();
    }
}
