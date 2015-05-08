package com.alex.jungletiger;

import java.util.concurrent.*;
import io.netty.buffer.*;
import org.slf4j.*;

class JTWorker implements Runnable 
{
    private String name;
    private Logger logger;
    private ArrayBlockingQueue mq;

    public JTWorker(String name, ArrayBlockingQueue mq) 
    {
        logger = LoggerFactory.getLogger(JTWorker.class);
        this.name = name;
        this.mq = mq;
    }

    @Override
    public void run()
    {
        logger.info(this.name + " is running.");
        try
        {
            while (true)
            {
                JTInternalMessage message = (JTInternalMessage)mq.take();
                logger.info("get message: " + message.requestMessage.command);
                ByteBuf rep = Unpooled.copiedBuffer("ok".getBytes());
                message.ctx.writeAndFlush(rep);
            }
        }
        catch (InterruptedException ie)
        {
            logger.error("get message failed: " + ie.getMessage());
        } 
    }
}