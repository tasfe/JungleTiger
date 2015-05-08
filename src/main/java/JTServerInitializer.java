package com.alex.jungletiger;

import java.util.concurrent.*;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.handler.codec.string.*;
import io.netty.handler.codec.bytes.*;
import io.netty.handler.codec.*;

public class JTServerInitializer extends ChannelInitializer<SocketChannel> 
{
    private ArrayBlockingQueue mq;

    public JTServerInitializer(ArrayBlockingQueue mq)
    {
        this.mq = mq;
    }

    @Override
    public void initChannel(SocketChannel ch) 
    {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
        p.addLast(new ByteArrayDecoder());
        p.addLast(new LengthFieldPrepender(2));
        p.addLast(new ByteArrayEncoder());
        // p.addLast(new LineBasedFrameDecoder(1024));
        // p.addLast(new StringDecoder());
        p.addLast(new JTServerHandler(mq));
    }
}
