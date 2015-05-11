package com.alex.jungletiger;

import java.util.concurrent.*;
import java.sql.*;
import io.netty.channel.socket.nio.*;
import io.netty.handler.logging.*;
import io.netty.channel.nio.*;
import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import org.slf4j.*;

public class JTAdmin implements Runnable 
{
    private String dsn;
    private int port;
    private Logger logger;

    public JTAdmin(int port) 
    {
        logger = LoggerFactory.getLogger(JTWorker.class);
        this.port = port;
    }

    @Override
    public void run()
    {
        logger.info(">>>> create admin ...");

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try 
        {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(new JTAdminInitializer());

            Channel ch = b.bind(port).sync().channel();
            ch.closeFuture().sync();
        }
        catch (InterruptedException ie)
        {
            // TODO
        }
        finally 
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
