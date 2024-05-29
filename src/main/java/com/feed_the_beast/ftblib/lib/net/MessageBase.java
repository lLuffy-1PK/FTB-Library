package com.feed_the_beast.ftblib.lib.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class MessageBase implements IMessage {
    MessageBase() {
    }

    public abstract NetworkWrapper getWrapper();

    @Override
    public final void toBytes(ByteBuf buf) {
        writeData(new DataOut(buf));
    }

    @Override
    public final void fromBytes(ByteBuf buf) {
        readData(new DataIn(buf));
    }

    public void writeData(DataOut data) {
    }

    public void readData(DataIn data) {
    }
}