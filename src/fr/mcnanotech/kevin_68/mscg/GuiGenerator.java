package fr.mcnanotech.kevin_68.mscg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import fr.mcnanotech.kevin_68.mscg.packets.PacketGenerateFile;

public class GuiGenerator extends GuiScreen
{
	public GuiGenerator(EntityPlayer player)
	{

	}

	@Override
	public void initGui()
	{
		super.initGui();
		this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 2, "Generate"));
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		switch(guibutton.id)
		{
		case 0:
		{
			MSCG.packetHandler.sendToServer(new PacketGenerateFile());
			// MSCG.generateFile(Minecraft.getMinecraft().thePlayer);
			Minecraft.getMinecraft().displayGuiScreen(null);
			Minecraft.getMinecraft().setIngameFocus();
		}
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}
