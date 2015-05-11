package com.alex.jungletiger;

import java.util.concurrent.*;
import io.netty.handler.codec.string.*;
import io.netty.handler.codec.bytes.*;
import io.netty.handler.codec.*;
import io.netty.channel.socket.*;
import io.netty.channel.*;
import io.netty.util.concurrent.*;


public class JTAdminInitializer extends ChannelInitializer<SocketChannel> 
{
    public JTAdminInitializer()
    {
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
        p.addLast(new JTAdminHandler());
    }
}
