package fr.mcnanotech.kevin_68.mscg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import fr.mcnanotech.kevin_68.mscg.packets.PacketSelection;
import fr.minecraftforgefrance.ffmtlibs.network.PacketManager;

@Mod(modid = MSCG.MODID, name = "Minecraft Structure Code Generator", version = "@VERSION@", dependencies = "required-after:ffmtlibs")
public class MSCG
{
	public static final String MODID = "mscg";

	@Instance(MODID)
	public static MSCG modInstance;

	public static final PacketManager packetHandler = new PacketManager("fr.mcnanotech.kevin_68.mscg.packets", MODID, "MinecraftStructureCodeGenerator");

	public static Item selector, generator;

	public static Map<EntityPlayer, BlockPos[]> selections = new HashMap<EntityPlayer, BlockPos[]>();

	@EventHandler
	public void preInitMSCG(FMLPreInitializationEvent event)
	{
		selector = new Item().setUnlocalizedName("mscg.selector").setCreativeTab(CreativeTabs.tabTools);
		GameRegistry.registerItem(selector, "mscg.selector");

		generator = new ItemGenerator().setUnlocalizedName("mscg.generator").setCreativeTab(CreativeTabs.tabTools);
		GameRegistry.registerItem(generator, "mscg.generator");
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

		NetworkRegistry.INSTANCE.registerGuiHandler(modInstance, new GuiHandler());
	}

	@EventHandler
	public void postInitMSCG(FMLPostInitializationEvent event)
	{
		if(event.getSide() == Side.CLIENT)
		{
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(selector, 0, new ModelResourceLocation(MODID + ":selector", "inventory"));
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(generator, 0, new ModelResourceLocation(MODID + ":generator", "inventory"));
		}
	}

	@SubscribeEvent
	public void onInteractEvent(PlayerInteractEvent event)
	{
		if(!event.entityPlayer.worldObj.isRemote)
		{
			if(event.entityPlayer.getCurrentEquippedItem() != null)
			{
				if(event.entityPlayer.getCurrentEquippedItem().getItem() == selector)
				{
					if(selections.isEmpty())
					{
						selections.put(event.entityPlayer, new BlockPos[2]);
					}

					BlockPos[] p = selections.get(event.entityPlayer);
					if(event.action == Action.LEFT_CLICK_BLOCK)
					{
						p[0] = event.pos;
					}
					else if(event.action == Action.RIGHT_CLICK_BLOCK)
					{
						p[1] = event.pos;
					}
					selections.put(event.entityPlayer, p);
					packetHandler.sendTo(new PacketSelection(p), (EntityPlayerMP)event.entityPlayer);
				}
			}
		}
	}

	public static List<BlockPos> getBlockList(EntityPlayer player)
	{
		if(selections.containsKey(player))
		{
			BlockPos start = selections.get(player)[0];
			BlockPos end = selections.get(player)[1];

			if(start != null && end != null)
			{
				List<BlockPos> list = new ArrayList<BlockPos>();
				int startX = start.getX() < end.getX() ? start.getX() : end.getX();
				int endX = start.getX() < end.getX() ? end.getX() : start.getX();

				int startY = start.getY() < end.getY() ? start.getY() : end.getY();
				int endY = start.getY() < end.getY() ? end.getY() : start.getY();

				int startZ = start.getZ() < end.getZ() ? start.getZ() : end.getZ();
				int endZ = start.getZ() < end.getZ() ? end.getZ() : start.getZ();

				for(int x = startX; x <= endX; x++)
				{
					for(int y = startY; y <= endY; y++)
					{
						for(int z = startZ; z <= endZ; z++)
						{
							list.add(new BlockPos(x, y, z));
						}
					}
				}

				return list;
			}
		}
		return null;
	}

	public static void generateFile(EntityPlayer player)
	{
		List<BlockPos> l = getBlockList(player);

		Calendar c = Calendar.getInstance();
		File folder = new File(Minecraft.getMinecraft().mcDataDir.getParentFile(), "mscg");
		File file = new File(folder, +c.get(Calendar.DAY_OF_MONTH) + "d" + c.get(Calendar.MONTH) + "m" + c.get(Calendar.YEAR) + "y_" + c.get(Calendar.HOUR_OF_DAY) + "h" + c.get(Calendar.MINUTE) + "m" + c.get(Calendar.SECOND) + "s.java");
		try
		{
			if(!folder.exists())
			{
				folder.mkdirs();
			}
			if(!file.exists())
			{
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("//Warning this class may be too long for eclipse and your computer, if it is you can cut it.\n");
			bw.write("package name.your.package.here;\n");
			bw.write("\n");
			bw.write("import net.minecraft.world.World\n");
			bw.write("\n");
			bw.write("public Class NameYourClass\n");
			bw.write("{\n");
			bw.write("    public static void generate(World world, BlockPos pos)\n");
			bw.write("    {\n");
			// Blocks
			bw.write("    }\n");
			bw.write("}");
			bw.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}