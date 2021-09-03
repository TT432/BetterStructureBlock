package com.nmmoc7.better_structure;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;

public class BlockBetterStructure extends StructureBlock {
    public static Lazy<Item> ITEM;

    public BlockBetterStructure() {
        super(AbstractBlock.Properties.create(Material.IRON, MaterialColor.LIGHT_GRAY)
                .setRequiresTool()
                .hardnessAndResistance(-1.0F, 3600000.0F)
                .noDrops());

        ITEM = Lazy.of(() -> new BlockItem(this, new Item.Properties().group(ItemGroup.DECORATIONS).rarity(Rarity.EPIC)));
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
        return new TileEntityBetterStructure();
    }
}
