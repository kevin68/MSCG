package fr.mcnanotech.kevin_68.mscg.packets;

import fr.mcnanotech.kevin_68.mscg.MSCG;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSelection implements IMessage
{
    private BlockPos[] pos;

    public PacketSelection()
    {}

    public PacketSelection(BlockPos[] pos)
    {
        this.pos = pos;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        if(this.pos != null)
        {
            if(this.pos[0] != null)
            {
                if(this.pos[1] != null)
                {
                    buffer.writeInt(0);
                    buffer.writeInt(this.pos[0].getX());
                    buffer.writeInt(this.pos[0].getY());
                    buffer.writeInt(this.pos[0].getZ());
                    buffer.writeInt(this.pos[1].getX());
                    buffer.writeInt(this.pos[1].getY());
                    buffer.writeInt(this.pos[1].getZ());
                }
                else
                {
                    buffer.writeInt(1);
                    buffer.writeInt(this.pos[0].getX());
                    buffer.writeInt(this.pos[0].getY());
                    buffer.writeInt(this.pos[0].getZ());
                }
            }
            else if(this.pos[1] != null)
            {
                buffer.writeInt(2);
                buffer.writeInt(this.pos[1].getX());
                buffer.writeInt(this.pos[1].getY());
                buffer.writeInt(this.pos[1].getZ());
            }
            else
            {
                buffer.writeInt(3);
            }
        }
        else
        {
            buffer.writeInt(4);
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        int val = buffer.readInt();
        BlockPos p0 = null;
        BlockPos p1 = null;
        switch(val)
        {
        case 0:
            p0 = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            p1 = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            break;
        case 1:
            p0 = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            break;
        case 2:
            p1 = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            break;
        case 3:
            break;
        case 4:
            break;
        }
        this.pos = new BlockPos[] {p0, p1};
    }

    public static class Handler implements IMessageHandler<PacketSelection, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSelection message, MessageContext ctx)
        {
            MSCG.selections.put(Minecraft.getMinecraft().thePlayer, message.pos);
            return null;
        }
    }
}