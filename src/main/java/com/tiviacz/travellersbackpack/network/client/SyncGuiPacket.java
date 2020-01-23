package com.tiviacz.travellersbackpack.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncGuiPacket implements IMessage
{
    public int id, value;

    public SyncGuiPacket() {}

    public SyncGuiPacket(int fieldId, int value)
    {
        id = fieldId;
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        value = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeInt(value);
    }

    public static class Handler implements IMessageHandler<SyncGuiPacket, IMessage>
    {
        @Override
        public IMessage onMessage(final SyncGuiPacket message, MessageContext ctx)
        {
            final IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    Minecraft.getMinecraft().player.openContainer.updateProgressBar(message.id, message.value);
                }
            });
            return null;
        }
    }
}