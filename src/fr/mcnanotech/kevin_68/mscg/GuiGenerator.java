package fr.mcnanotech.kevin_68.mscg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import fr.mcnanotech.kevin_68.mscg.packets.PacketGenerateFile;
import fr.minecraftforgefrance.ffmtlibs.client.gui.GuiBooleanButton;

public class GuiGenerator extends GuiScreen
{
    private GuiBooleanButton excludeAir;
    private GuiBooleanButton sorting;

    public GuiGenerator(EntityPlayer player)
    {

    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 - 30, "Generate"));
        this.buttonList.add(this.excludeAir = new GuiBooleanButton(1, this.width / 2 - 100, this.height / 2, 200, 20, "", false));
        this.excludeAir.setTexts("Will exclude air", "Will include air");
        this.buttonList.add(this.sorting = new GuiBooleanButton(2, this.width / 2 - 100, this.height / 2 + 30, 200, 20, "", false));
        this.sorting.setTexts("Sort blocks by type", "Sort blocks by x/y/z");
    }

    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
        switch(guibutton.id)
        {
        case 0:
        {
            MSCG.network.sendToServer(new PacketGenerateFile(this.excludeAir.isActive(), this.sorting.isActive()));
            Minecraft.getMinecraft().displayGuiScreen(null);
            Minecraft.getMinecraft().setIngameFocus();
            break;
        }
        case 1:
        {
            this.excludeAir.toggle();
            break;
        }
        case 2:
        {
            this.sorting.toggle();
            break;
        }
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
