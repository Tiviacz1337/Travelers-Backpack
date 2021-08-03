package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.gui.TravelersBackpackQuarkScreen;
import com.tiviacz.travelersbackpack.client.gui.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackLayer;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackTileEntityRenderer;
import com.tiviacz.travelersbackpack.init.ModContainerTypes;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTileEntityTypes;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class ModClientEventHandler
{
    public static final KeyBinding OPEN_INVENTORY = new KeyBinding(Reference.INVENTORY, GLFW.GLFW_KEY_B, Reference.CATEGORY);
    public static final KeyBinding TOGGLE_TANK = new KeyBinding(Reference.TOGGLE_TANK, GLFW.GLFW_KEY_N, Reference.CATEGORY);
    public static final KeyBinding CYCLE_TOOL = new KeyBinding(Reference.CYCLE_TOOL, GLFW.GLFW_KEY_Z, Reference.CATEGORY);

    public static void registerScreenFactory()
    {
        if(TravelersBackpack.enableQuark())
        {
            ScreenManager.registerFactory(ModContainerTypes.TRAVELERS_BACKPACK_TILE.get(), TravelersBackpackQuarkScreen::new);
            ScreenManager.registerFactory(ModContainerTypes.TRAVELERS_BACKPACK_ITEM.get(), TravelersBackpackQuarkScreen::new);
        }
        else
        {
            ScreenManager.registerFactory(ModContainerTypes.TRAVELERS_BACKPACK_TILE.get(), TravelersBackpackScreen::new);
            ScreenManager.registerFactory(ModContainerTypes.TRAVELERS_BACKPACK_ITEM.get(), TravelersBackpackScreen::new);
        }
    }

    public static void bindTileEntityRenderer()
    {
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.TRAVELERS_BACKPACK.get(), TravelersBackpackTileEntityRenderer::new);
    }

    public static void registerKeybinding()
    {
        ClientRegistry.registerKeyBinding(OPEN_INVENTORY);
        ClientRegistry.registerKeyBinding(TOGGLE_TANK);
        ClientRegistry.registerKeyBinding(CYCLE_TOOL);
    }

    public static void addLayer()
    {
        for(PlayerRenderer renderer : Minecraft.getInstance().getRenderManager().getSkinMap().values())
        {
            renderer.addLayer(new TravelersBackpackLayer(renderer));
        }
    }

    public static void registerItemModelProperty()
    {
        ItemModelsProperties.registerProperty(ModItems.HOSE.get(), new ResourceLocation(TravelersBackpack.MODID,"mode"), (stack, world, entity) -> {
            CompoundNBT compound = stack.getTag();
            if(compound == null) return 0;
            else return compound.getInt("Mode");
        });
    }

   // @Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
  //  public static class Stitcher
  //  {
    /*    @SubscribeEvent
        public static void stitcherEventPre(TextureStitchEvent.Pre event)
        {
            if(!event.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE))
            {
                return;
            }

            for(String name : Reference.BACKPACK_NAMES)
            {
                event.addSprite(new ResourceLocation(TravelersBackpack.MODID, "model/" + name.toLowerCase()));
            }
        } */
    //}
}
