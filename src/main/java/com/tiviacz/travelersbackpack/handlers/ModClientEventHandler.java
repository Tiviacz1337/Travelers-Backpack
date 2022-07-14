package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.gui.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ModClientEventHandler
{
    public static void registerScreenFactory()
    {
        MenuScreens.register(ModMenuTypes.TRAVELERS_BACKPACK_TILE.get(), TravelersBackpackScreen::new);
        MenuScreens.register(ModMenuTypes.TRAVELERS_BACKPACK_ITEM.get(), TravelersBackpackScreen::new);
    }

    public static void bindTileEntityRenderer()
    {
        BlockEntityRenderers.register(ModBlockEntityTypes.TRAVELERS_BACKPACK.get(), TravelersBackpackBlockEntityRenderer::new);
    }

    public static void registerItemModelProperty()
    {
        ItemProperties.register(ModItems.HOSE.get(), new ResourceLocation(TravelersBackpack.MODID,"mode"), (stack, world, entity, p_174638_) -> {
            CompoundTag compound = stack.getTag();
            if(compound == null) return 0;
            else return compound.getInt("Mode");
        });
    }
}
