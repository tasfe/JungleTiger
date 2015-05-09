package com.alex.jungletiger;

import java.util.concurrent.*;
import java.io.*;
import io.netty.channel.socket.nio.*;
import io.netty.handler.logging.*;
import io.netty.channel.nio.*;
import io.netty.bootstrap.*;
import io.netty.channel.*;
import org.slf4j.*;
import org.json.*;

public final class JTServer 
{
    private static JSONObject json;

    public static void main(String[] args) throws Exception 
    {
        // load config file
        String filename = args.length > 0 ? args[0] : "./etc/jt.config";
        loadJsonConfig(filename);

        JTServer jtserver = new JTServer();
        jtserver.run();
    }

    private void run() throws Exception
    {
        Logger logger = LoggerFactory.getLogger(JTServer.class);
        logger.info("MySQL Server run ...");

        // create message queue
        int size = json.getJSONObject("jt_server").getInt("queue_size");
        ArrayBlockingQueue mq = new ArrayBlockingQueue(size, true);

        // create worker thread pool
        int workers = json.getJSONObject("jt_server").getInt("workers");
        String dsn = json.getJSONObject("meta_server").getString("dsn");
        ExecutorService executor = Executors.newFixedThreadPool(workers);
        for (int i = 0; i < workers; i++) 
        {
            Runnable worker = new JTWorker(dsn, mq);
            executor.execute(worker);
        }

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try 
        {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(new JTServerInitializer(mq));

            Channel ch = b.bind(json.getJSONObject("jt_server").getInt("port")).sync().channel();
            ch.closeFuture().sync();
        } 
        finally 
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

        executor.shutdown();
        while (!executor.isTerminated()) 
        {}
        System.out.println("Finished all threads");
    }

    private static void loadJsonConfig(String filename) throws Exception 
    {
        json = new JSONObject(new JSONTokener(new FileReader(new File(filename))));  
    }
}
