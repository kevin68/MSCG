package fr.mcnanotech.kevin_68.mscg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

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
			MSCG.generateFile(Minecraft.getMinecraft().thePlayer);
		}
		}
	}
}
