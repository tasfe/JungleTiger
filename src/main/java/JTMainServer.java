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

public final class JTMainServer 
{
    private static JSONObject json;
    private Logger logger = LoggerFactory.getLogger(JTMainServer.class);

    public static void main(String[] args) throws Exception 
    {
        // load config file
        String filename = args.length > 0 ? args[0] : "./etc/jt.config";
        loadJsonConfig(filename);

        JTMainServer server = new JTMainServer();
        server.run();
    }

    private void run() throws Exception
    {
        logger.info(">>>> jungletiger run ...");

        ExecutorService p1 = Executors.newSingleThreadExecutor();
        Runnable t1 = new JTAdmin(json.getJSONObject("jt_admin").getInt("port"));
        p1.execute(t1);

        ExecutorService p2 = Executors.newSingleThreadExecutor();
        Runnable t2 = new JTServer(json);
        p2.execute(t2);
    }

    private static void loadJsonConfig(String filename) throws Exception 
    {
        json = new JSONObject(new JSONTokener(new FileReader(new File(filename))));  
    }
}
