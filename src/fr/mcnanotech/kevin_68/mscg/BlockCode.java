package fr.mcnanotech.kevin_68.mscg;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;

public class BlockCode
{
    private World world;
    private BlockPos pos, start;
    private int id;

    public BlockCode(int id, World w, BlockPos p, BlockPos start)
    {
        this.world = w;
        this.pos = p;
        this.start = start;
        this.id = id;
    }

    public String getBlockName()
    {
        String str = "";
        if(!this.world.isRemote)
        {
            IBlockState b = this.world.getBlockState(this.pos);
            if(this.world.getWorldType() != WorldType.DEBUG_WORLD)
            {
                b = b.getBlock().getActualState(b, this.world, this.pos);
            }

            if(this.world.isAirBlock(this.pos))
            {
                return "air";
            }

            str = MSCG.blocksName.get(b.getBlock()).split(":")[1];
        }
        return str;
    }

    public int getAddX()
    {
        return this.pos.getX() - this.start.getX();
    }

    public int getAddY()
    {
        return this.pos.getY() - this.start.getY();
    }

    public int getAddZ()
    {
        return this.pos.getZ() - this.start.getZ();
    }

    public String getProps()
    {
        IBlockState b = this.world.getBlockState(this.pos);
        if(this.world.getWorldType() != WorldType.DEBUG_WORLD)
        {
            b = b.getBlock().getActualState(b, this.world, this.pos);
        }

        if(this.world.isAirBlock(this.pos))
        {
            return "";
        }

        String prop = "";
        for(int i = 0; i < b.getPropertyNames().toArray().length; i++)
        {
            Object o = b.getPropertyNames().toArray()[i];
            Object o1 = b.getValue((IProperty)o);

            String clazz = o1.getClass().toString().substring(6);
            String[] part = clazz.split("\\.");
            String parentchild = part[part.length - 1].replace("$", ".");

            String blockClass = b.getBlock().getClass().toString().substring(6);
            String[] blockPart = blockClass.split("\\.");
            String bClass = blockPart[blockPart.length - 1];

            String s = o1.toString();
            // result.add(blockClass);
            prop += ".withProperty(" + bClass + "." + ((IProperty)o).getName().toUpperCase() + ", " + (s == "false" || s == "true" ? s : parentchild + "." + s.toUpperCase()) + ")";
        }
        return prop;
    }

    public boolean isVanilla()
    {
        IBlockState b = this.world.getBlockState(this.pos);
        if(this.world.getWorldType() != WorldType.DEBUG_WORLD)
        {
            b = b.getBlock().getActualState(b, this.world, this.pos);
        }

        if(this.world.isAirBlock(this.pos))
        {
            return true;
        }

        return MSCG.blocksName.get(b.getBlock()).split(":")[0].equalsIgnoreCase("minecraft");
    }

    public String getCodeLine()
    {
        if(this.world.isAirBlock(this.pos))
        {
            return "        world.setBlockToAir(pos.add(" + getAddX() + ", " + getAddY() + ", " + getAddZ() + "));\n";
        }
        String block = (isVanilla() ? "Blocks." : "/* /!\\ */YourClass.") + getBlockName() + (isVanilla() ? "" : "/*Mod Block!!!!!!!! /!\\ */") + ".getDefaultState()";
        return "        world.setBlockState(pos.add(" + getAddX() + ", " + getAddY() + ", " + getAddZ() + "), " + block + getProps() + ");\n";
    }

    public String getImport()
    {
        IBlockState b = this.world.getBlockState(this.pos);
        if(this.world.getWorldType() != WorldType.DEBUG_WORLD)
        {
            b = b.getBlock().getActualState(b, this.world, this.pos);
        }
        return b.getBlock().getClass().toString().substring(6);
    }

    public int getId()
    {
        return this.id;
    }

    public boolean sameAs(BlockCode b)
    {
        return this.getAddX() == b.getAddX() && this.getAddY() == b.getAddY() && this.getAddZ() == b.getAddZ();
    }
}