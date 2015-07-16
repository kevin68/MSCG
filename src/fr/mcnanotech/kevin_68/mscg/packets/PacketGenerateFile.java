package fr.mcnanotech.kevin_68.mscg.packets;

import fr.mcnanotech.kevin_68.mscg.MSCG;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGenerateFile implements IMessage
{
    private boolean excludeAir, sortByType;

    public PacketGenerateFile()
    {

    }

    public PacketGenerateFile(boolean excludeAir, boolean sortByType)
    {
        this.excludeAir = excludeAir;
        this.sortByType = sortByType;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeBoolean(this.excludeAir);
        buffer.writeBoolean(this.sortByType);
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.excludeAir = buffer.readBoolean();
        this.sortByType = buffer.readBoolean();
    }

    public static class Handler implements IMessageHandler<PacketGenerateFile, IMessage>
    {
        @Override
        public IMessage onMessage(PacketGenerateFile message, MessageContext ctx)
        {
            MSCG.generateFile(ctx.getServerHandler().playerEntity, message.excludeAir, message.sortByType);
            return null;
        }
    }
}