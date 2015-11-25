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
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import fr.mcnanotech.kevin_68.mscg.packets.PacketGenerateFile;
import fr.mcnanotech.kevin_68.mscg.packets.PacketSelection;

@Mod(modid = MSCG.MODID, name = "Minecraft Structure Code Generator", version = "@VERSION@", dependencies = "required-after:ffmtlibs")
public class MSCG
{
    public static final String MODID = "mscg";

    @Instance(MODID)
    public static MSCG modInstance;

    public static SimpleNetworkWrapper network;

    public static Item selector, generator;

    public static Map<EntityPlayer, BlockPos[]> selections = new HashMap<EntityPlayer, BlockPos[]>();
    public static Map<Block, String> blocksName = new HashMap<Block, String>();

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
    public void init(FMLInitializationEvent event)
    {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        network.registerMessage(PacketGenerateFile.Handler.class, PacketGenerateFile.class, 0, Side.SERVER);
        network.registerMessage(PacketSelection.Handler.class, PacketSelection.class, 1, Side.CLIENT);
    }

    @EventHandler
    public void postInitMSCG(FMLPostInitializationEvent event)
    {
        if(event.getSide() == Side.CLIENT)
        {
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(selector, 0, new ModelResourceLocation(MODID + ":mscg_selector", "inventory"));
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(generator, 0, new ModelResourceLocation(MODID + ":mscg_generator", "inventory"));
        }

        Set<ResourceLocation> keys = Block.blockRegistry.getKeys();
        for(ResourceLocation o : keys)
        {
            Object ob = Block.blockRegistry.getObject(o);
            String name = ((ResourceLocation)o).getResourceDomain() + ":" + ((ResourceLocation)o).getResourcePath();
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
                            event.entityPlayer.addChatMessage(new ChatComponentText("Pos 1 selected"));
                        }
                    }
                    else if(event.action == Action.RIGHT_CLICK_BLOCK)
                    {
                        if(event.pos != null)
                        {
                            p[1] = event.pos;
                            event.entityPlayer.addChatMessage(new ChatComponentText("Pos 2 selected"));
                        }
                    }
                    selections.put(event.entityPlayer, p);
                    network.sendTo(new PacketSelection(p), (EntityPlayerMP)event.entityPlayer);
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

    public static void generateFile(EntityPlayer player, boolean excludeAir, boolean sortByType)
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
            File file = new File(folder, +c.get(Calendar.DAY_OF_MONTH) + "d" + (c.get(Calendar.MONTH) + 1) + "m" + c.get(Calendar.YEAR) + "y_" + c.get(Calendar.HOUR_OF_DAY) + "h" + c.get(Calendar.MINUTE) + "m" + c.get(Calendar.SECOND) + "s.java");
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
                List<BlockCode> blockslist = new ArrayList<BlockCode>();
                Map<String, Map<String, List<BlockCode>>> sortedList = new HashMap<String, Map<String, List<BlockCode>>>();
                int i = 0;
                for(BlockPos pos : l)
                {
                    System.out.print("block" + i + " " + (i % 20 == 0 ? "\n" : ""));
                    if(sortByType)
                    {
                        BlockCode bc = new BlockCode(i, player.worldObj, pos, l.get(0));
                        System.out.println(bc.getBlockName() != "iron_block");
                        if(!bc.getBlockName().equals("iron_block"))
                        {
                            if(sortedList.containsKey(bc.getBlockName()))
                            {
                                Map<String, List<BlockCode>> map = sortedList.get(bc.getBlockName());
                                if(map.containsKey(bc.getProps()))
                                {
                                    List<BlockCode> tempList = map.get(bc.getProps());
                                    tempList.add(bc);
                                    map.put(bc.getProps(), tempList);
                                    sortedList.put(bc.getBlockName(), map);
                                }
                                else
                                {
                                    List<BlockCode> tempList = new ArrayList<BlockCode>();
                                    tempList.add(bc);
                                    map.put(bc.getProps(), tempList);
                                    sortedList.put(bc.getBlockName(), map);
                                }
                            }
                            else
                            {
                                Map<String, List<BlockCode>> tempMap = new HashMap<String, List<BlockCode>>();
                                List<BlockCode> tempList = new ArrayList<BlockCode>();
                                tempList.add(bc);
                                tempMap.put(bc.getProps(), tempList);
                                sortedList.put(bc.getBlockName(), tempMap);
                            }
                            String imp = bc.getImport();
                            if(!imports.contains(imp))
                            {
                                imports.add(imp);
                            }
                            i++;
                        }
                    }
                    else
                    {
                        BlockCode bc = new BlockCode(i, player.worldObj, pos, l.get(0));
                        blockslist.add(bc);
                        String imp = bc.getImport();
                        if(!imports.contains(imp))
                        {
                            imports.add(imp);
                        }
                        i++;
                    }
                }

                if(sortByType)
                {
                    for(String str : sortedList.keySet())
                    {
                        for(String str2 : sortedList.get(str).keySet())
                        {
                            blockslist.addAll(sortedList.get(str).get(str2));
                        }
                    }
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("//Warning this class may be too long for eclipse and your computer, if it is you can cut it.\n");
                bw.write("package name.your.package.here;\n");
                bw.write("\n");
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
                bw.write("    public boolean generate(World world, BlockPos pos)\n");
                bw.write("    {\n");
                int m = 0;
                for(BlockCode bc : blockslist)
                {
                    System.out.print("writegen" + m + " " + (m % 20 == 0 ? "\n" : ""));
                    if(excludeAir)
                    {
                        if(bc.getBlockName() != "air")
                        {
                            bw.write(bc.getCodeLine());
                        }
                    }
                    else
                    {
                        bw.write(bc.getCodeLine());
                    }
                    m++;
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
}