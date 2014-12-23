package fr.mcnanotech.kevin_68.mscg.packets;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import fr.mcnanotech.kevin_68.mscg.MSCG;
import fr.minecraftforgefrance.ffmtlibs.network.FFMTPacket;

public class PacketSelection extends FFMTPacket
{
	private BlockPos[] pos;

	public PacketSelection()
	{}

	public PacketSelection(BlockPos[] pos)
	{
		this.pos = pos;
	}

	@Override
	public void writeData(ByteBuf buffer) throws IOException
	{
		if(pos[0] != null)
		{
			if(pos[1] != null)
			{
				buffer.writeInt(0);
				buffer.writeInt(pos[0].getX());
				buffer.writeInt(pos[0].getY());
				buffer.writeInt(pos[0].getZ());
				buffer.writeInt(pos[1].getX());
				buffer.writeInt(pos[1].getY());
				buffer.writeInt(pos[1].getZ());
			}
			else
			{
				buffer.writeInt(1);
				buffer.writeInt(pos[0].getX());
				buffer.writeInt(pos[0].getY());
				buffer.writeInt(pos[0].getZ());
			}
		}
		else if(pos[1] != null)
		{
			buffer.writeInt(2);
			buffer.writeInt(pos[1].getX());
			buffer.writeInt(pos[1].getY());
			buffer.writeInt(pos[1].getZ());
		}
		else
		{
			buffer.writeInt(3);
		}
	}

	@Override
	public void readData(ByteBuf buffer)
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
		}
		pos = new BlockPos[] {p0, p1};
	}

	@Override
	public void handleClientSide(EntityPlayer player)
	{
		MSCG.selections.put(player, pos);
	}

	@Override
	public void handleServerSide(EntityPlayer player)
	{

	}

	@Override
	public int getDiscriminator()
	{
		return 0;
	}
}