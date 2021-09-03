package com.nmmoc7.better_structure;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryHandler {
    static Block BLOCK = new BlockBetterStructure()
            .setRegistryName(new ResourceLocation(BetterStructure.MOD_ID, "better_structure"));

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(BLOCK);
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(BlockBetterStructure.ITEM.get()
                .setRegistryName(new ResourceLocation(BetterStructure.MOD_ID, "better_structure")));
    }

    static TileEntityType<TileEntityBetterStructure> TILE =
            new TileEntityType<>(TileEntityBetterStructure::new, new HashSet<>(Collections.singletonList(BLOCK)), null);

    @SubscribeEvent
    public static void onTileEntityRegister(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TILE
                .setRegistryName(new ResourceLocation(BetterStructure.MOD_ID, "better_structure_tile")));
    }
}
