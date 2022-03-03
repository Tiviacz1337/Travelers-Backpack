package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.gui.OverlayScreen;
import com.tiviacz.travelersbackpack.client.gui.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class ModClientEventHandler
{
    public static final KeyMapping OPEN_INVENTORY = new KeyMapping(Reference.INVENTORY, GLFW.GLFW_KEY_B, Reference.CATEGORY);
    public static final KeyMapping TOGGLE_TANK = new KeyMapping(Reference.TOGGLE_TANK, GLFW.GLFW_KEY_N, Reference.CATEGORY);
    public static final KeyMapping CYCLE_TOOL = new KeyMapping(Reference.CYCLE_TOOL, GLFW.GLFW_KEY_Z, Reference.CATEGORY);

    public static void registerScreenFactory()
    {
      /*  if(TravelersBackpack.enableQuark())
        {
            ScreenManager.registerFactory(ModContainerTypes.TRAVELERS_BACKPACK_TILE.get(), TravelersBackpackQuarkScreen::new);
            ScreenManager.registerFactory(ModContainerTypes.TRAVELERS_BACKPACK_ITEM.get(), TravelersBackpackQuarkScreen::new);
        } */
       // else
      //  {
            MenuScreens.register(ModMenuTypes.TRAVELERS_BACKPACK_TILE.get(), TravelersBackpackScreen::new);
            MenuScreens.register(ModMenuTypes.TRAVELERS_BACKPACK_ITEM.get(), TravelersBackpackScreen::new);
      //  }
    }

    public static void bindTileEntityRenderer()
    {
        BlockEntityRenderers.register(ModBlockEntityTypes.TRAVELERS_BACKPACK.get(), TravelersBackpackBlockEntityRenderer::new);
    }

    public static void registerKeybinding()
    {
        ClientRegistry.registerKeyBinding(OPEN_INVENTORY);
        ClientRegistry.registerKeyBinding(TOGGLE_TANK);
        ClientRegistry.registerKeyBinding(CYCLE_TOOL);
    }

    public static void registerOverlay()
    {
        OverlayRegistry.registerOverlayBelow(ForgeIngameGui.HOTBAR_ELEMENT, "Traveler's Backpack HUD", (gui, poseStack, partialTicks, screenWidth, screenHeight) ->
        {
            Minecraft mc = Minecraft.getInstance();

            if(!mc.options.hideGui && CapabilityUtils.isWearingBackpack(mc.player) && mc.gameMode.getPlayerMode() != GameType.SPECTATOR)
            {
                OverlayScreen.renderOverlay(gui, mc, poseStack);
            }
        });
    }

   /* public static void addLayer()
    {
        if(Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(Minecraft.getInstance().player) instanceof PlayerRenderer renderer)
        {
            renderer.addLayer(new TravelersBackpackLayer(renderer));
        } */
      //  for(PlayerRenderer renderer : Minecraft.getInstance().getEntityRenderDispatcher().getRenderer())
      //  {

      //  }
       // for(EntityRenderer<? extends Player> renderer : Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap().values())
       // {
          //  renderer.render(new TravelersBackpackLayer(renderer));
       // }
   // }

    public static void registerItemModelProperty()
    {
        ItemProperties.register(ModItems.HOSE.get(), new ResourceLocation(TravelersBackpack.MODID,"mode"), (stack, world, entity, p_174638_) -> {
            CompoundTag compound = stack.getTag();
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
