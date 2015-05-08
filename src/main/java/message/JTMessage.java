package com.alex.jungletiger;

import org.msgpack.annotation.*;
import io.netty.channel.*;

@Message
class JTRequestTMessage
{
    public int type;
    public String command;
}

class JTInternalMessage
{
    public JTRequestTMessage requestMessage;
    public ChannelHandlerContext ctx;
}