package com.alex.jungletiger;

import java.util.concurrent.*;
import java.sql.*;
import java.io.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import org.msgpack.annotation.*;
import org.msgpack.*;
import org.slf4j.*;

public class JTAdminHandler extends ChannelInboundHandlerAdapter 
{
    private Logger logger;
    private Connection conn;
    private Statement stmt;

    public JTAdminHandler()
    {
        logger = LoggerFactory.getLogger(JTAdminHandler.class);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception 
    {
        logger.info(ctx.channel().remoteAddress().toString() + " is connected");
        String dsn = JTMainServer.json.getJSONObject("jt_admin")
                                      .getJSONObject("meta_server")
                                      .getString("dsn");
        try
        {
            logger.info("connect admin database: " + dsn + "...");

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(dsn);
            stmt = conn.createStatement();
        }
        catch (SQLException se)
        {
            logger.error("mysql err: " + se.getMessage());
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }
        finally
        {
            if(stmt != null)
            {
                try {stmt.close();} catch(SQLException e) {e.printStackTrace();}   
            }
            if(conn != null)
            {
                try {conn.close();} catch(SQLException e) {e.printStackTrace();}
            }
        }
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