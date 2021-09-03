package com.nmmoc7.better_structure;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.StructureTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RendererRegistryHandler {
    @SubscribeEvent
    public static void onTileEntityRendererRegister(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(RegistryHandler.TILE, BetterStructureTileEntityRenderer::new);
    }
}
