package fr.mcnanotech.kevin_68.mscg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemGenerator extends Item
{
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
	{
		if(!player.isSneaking())
		{
			player.openGui(MSCG.modInstance, 1, world, 0, 0, 0);
		}

		return itemStack;
	}
}