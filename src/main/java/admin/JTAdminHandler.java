package com.alex.jungletiger;

import java.util.concurrent.*;
import java.io.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import org.msgpack.annotation.*;
import org.msgpack.*;

public class JTAdminHandler extends ChannelInboundHandlerAdapter 
{
    public JTAdminHandler()
    {
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
        MessagePack msgpack = new MessagePack();
        JTRequestTMessage message = new JTRequestTMessage();
        message = msgpack.read((byte[]) msg, JTRequestTMessage.class);
        System.out.println("command: " + message.command);
        ByteBuf rep = Unpooled.copiedBuffer("ok".getBytes());
        ctx.write(rep);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
    {
        cause.printStackTrace();
        ctx.close();
    }
}