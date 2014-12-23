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
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
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
	private static Map<Block, String> blocksName = new HashMap<Block, String>();

	@EventHandler
	public void preInitMSCG(FMLPreInitializationEvent event)
	{
		selector = new Item().setUnlocalizedName("mscg_selector").setCreativeTab(CreativeTabs.tabTools);
		GameRegistry.registerItem(selector, "mscg_selector");

		generator = new ItemGenerator().setUnlocalizedName("mscg_generator").setCreativeTab(CreativeTabs.tabTools);
		GameRegistry.registerItem(generator, "mscg_generator");
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

		NetworkRegistry.INSTANCE.registerGuiHandler(modInstance, new GuiHandler());
	}

	@EventHandler
	public void postInitMSCG(FMLPostInitializationEvent event)
	{
		if(event.getSide() == Side.CLIENT)
		{
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(selector, 0, new ModelResourceLocation(MODID + ":mscg_selector", "inventory"));
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(generator, 0, new ModelResourceLocation(MODID + ":mscg_generator", "inventory"));
		}

		Set keys = Block.blockRegistry.getKeys();
		for(Object o : keys)
		{
			Object ob = Block.blockRegistry.getObject(o);
			String name = ((ResourceLocation)o).getResourceDomain() + ":" + ((ResourceLocation)o).getResourcePath();
			System.out.println(name);
			blocksName.put((Block)ob, name);
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
						if(event.pos != null)
						{
							p[0] = event.pos;
							event.entityPlayer.addChatMessage(new ChatComponentTranslation("message.pos1.set", event.pos.getX(), event.pos.getY(), event.pos.getZ()));
						}
					}
					else if(event.action == Action.RIGHT_CLICK_BLOCK)
					{
						if(event.pos != null)
						{
							p[1] = event.pos;
							event.entityPlayer.addChatMessage(new ChatComponentTranslation("message.pos2.set", event.pos.getX(), event.pos.getY(), event.pos.getZ()));
						}
					}
					selections.put(event.entityPlayer, p);
					packetHandler.sendTo(new PacketSelection(p), (EntityPlayerMP)event.entityPlayer);
				}
			}
		}
	}

	@SubscribeEvent
	public void breakBlockEvent(BlockEvent.BreakEvent event)
	{
		if(event.getPlayer().getCurrentEquippedItem() != null)
		{
			if(event.getPlayer().getCurrentEquippedItem().getItem() == selector)
			{
				event.setCanceled(true);
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
		if(l == null || l.isEmpty())
		{
			player.addChatMessage(new ChatComponentTranslation("message.create.failed", "NullPointerException"));
		}
		else
		{
			Calendar c = Calendar.getInstance();
			File folder = new File(FMLCommonHandler.instance().getSavesDirectory().getParentFile(), "mscg");
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

				List<String> imports = new ArrayList<String>();
				List<String> blocksGen = new ArrayList<String>();

				for(BlockPos pos : l)
				{
					List<String> str = getBlockCode(player.worldObj, pos, l.get(0));
					if(str.size() > 1)
					{
						List<String> st = str.subList(str.size() - 2, str.size() - 1);
						for(String s : st)
						{
							if(!imports.contains(s))
							{
								imports.add(s);
							}
						}
					}
					blocksGen.add(str.get(str.size() - 1));
				}

				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("//Warning this class may be too long for eclipse and your computer, if it is you can cut it.\n");
				bw.write("package name.your.package.here;\n");
				bw.write("\n");
				bw.write("import java.util.Random;\n");
				bw.write("import net.minecraft.init.Blocks;\n");
				bw.write("import net.minecraft.util.BlockPos;\n");
				bw.write("import net.minecraft.world.World\n");
				bw.write("import net.minecraft.world.gen.feature.WorldGenerator;\n");
				for(String s : imports)
				{
					bw.write("import " + s + ";\n");
				}
				bw.write("\n");
				bw.write("public Class NameYourClass extends WorldGenerator\n");
				bw.write("{\n");
				bw.write("    @Override\n");
				bw.write("    public boolean generate(World world, Random rand, BlockPos pos)\n");
				bw.write("    {\n");
				for(String s : blocksGen)
				{
					bw.write(s);
				}
				bw.write("        return true;\n");
				bw.write("    }\n");
				bw.write("}");
				bw.close();
				player.addChatMessage(new ChatComponentTranslation("message.create.success", file.getName()));
			}
			catch(IOException e)
			{
				player.addChatMessage(new ChatComponentTranslation("message.create.failed", e.getLocalizedMessage()));
				e.printStackTrace();
			}
		}
	}

	public static List<String> getBlockCode(World world, BlockPos p, BlockPos start)
	{
		List<String> result = new ArrayList<String>();
		if(!world.isRemote)
		{
			IBlockState b = world.getBlockState(p);
			if(world.getWorldType() != WorldType.DEBUG_WORLD)
			{
				b = b.getBlock().getActualState(b, world, p);
			}
			int x = p.getX() - start.getX();
			int y = p.getY() - start.getY();
			int z = p.getZ() - start.getZ();
			if(world.isAirBlock(p))
			{
				result.add("        world.setBlockToAir(pos.add(" + x + ", " + y + ", " + z + "));\n");
				return result;
			}
			String prop = "";
			for(int i = 0; i < b.getPropertyNames().toArray().length; i++)
			{
				Object o = b.getPropertyNames().toArray()[i];
				Object o0 = b.getProperties().get(o);
				Object o1 = b.getValue((IProperty)o);

				String clazz = o1.getClass().toString().substring(6);
				String[] part = clazz.split("\\.");
				String parentchild = part[part.length - 1].replace("$", ".");

				String blockClass = b.getBlock().getClass().toString().substring(6);
				String[] blockPart = blockClass.split("\\.");
				String bClass = blockPart[blockPart.length - 1];

				String s = o1.toString();
				result.add(blockClass);
				prop += ".withProperty(" + bClass + "." + ((IProperty)o).getName().toUpperCase() + ", " + (s == "false" || s == "true" ? s : parentchild + "." + s.toUpperCase()) + ")";
			}

			String name = blocksName.get(b.getBlock()).split(":")[1];
			Boolean mc = blocksName.get(b.getBlock()).split(":")[0].equalsIgnoreCase("minecraft");
			String block = (mc ? "Blocks." : "/* /!\\ */YourClass.") + name + (mc ? "" : "/*Mod Block!!!!!!!! /!\\ */") + ".getDefaultState()";
			result.add("        world.setBlockState(pos.add(" + x + ", " + y + ", " + z + "), " + block + prop + ");\n");
		}
		if(result.size() < 1)
		{
			result.add("");
		}
		return result;
	}
}
