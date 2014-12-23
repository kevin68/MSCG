package fr.mcnanotech.kevin_68.mscg.packets;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import fr.mcnanotech.kevin_68.mscg.MSCG;
import fr.minecraftforgefrance.ffmtlibs.network.FFMTPacket;

public class PacketGenerateFile extends FFMTPacket
{
	public PacketGenerateFile()
	{

	}

	@Override
	public void writeData(ByteBuf buffer) throws IOException
	{

	}

	@Override
	public void readData(ByteBuf buffer)
	{

	}

	@Override
	public void handleClientSide(EntityPlayer player)
	{

	}

	@Override
	public void handleServerSide(EntityPlayer player)
	{
		MSCG.generateFile(player);
	}

	@Override
	public int getDiscriminator()
	{
		return 1;
	}

}