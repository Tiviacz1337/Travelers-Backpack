package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.platform.Window;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.handlers.NeoForgeClientEventHandler;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ToolsScreen extends Screen {
    private static final double REF_W = 1920.0;
    private static final double REF_H = 1080.0;
    private long openStartMs = -1;

    public ToolsScreen() {
        super(Component.translatable("screen.travelersbackpack.tools_overlay"));
    }

    private float getOpenProgress() {
        long now = System.currentTimeMillis();
        if(openStartMs < 0) openStartMs = now;

        float durMs = 180.0F;
        return Mth.clamp((now - openStartMs) / durMs, 0.0f, 1.0f);
    }

    @Override
    protected void init() {
        super.init();
        Window mainWindow = Minecraft.getInstance().getWindow();

        int sw = mainWindow.getScreenWidth();
        int sh = mainWindow.getScreenHeight();

        int cx = sw / 2;
        int cy = sh / 2;

        int offXpx = TravelersBackpackConfig.CLIENT.toolsOverlay.offsetX.get(); // px@1920
        int offYpx = TravelersBackpackConfig.CLIENT.toolsOverlay.offsetY.get(); // px@1080

        double px = offXpx / REF_W;
        double py = offYpx / REF_H;

        int scaledWidth = (int)Math.round(cx + px * sw);
        int scaledHeight = (int)Math.round(cy + py * sh);
        GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().getWindow(), scaledWidth, scaledHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Window mainWindow = mc.getWindow();

        int sw = mainWindow.getGuiScaledWidth();
        int sh = mainWindow.getGuiScaledHeight();

        int cx = sw / 2;
        int cy = sh / 2;

        int offXpx = TravelersBackpackConfig.CLIENT.toolsOverlay.offsetX.get(); // px@1920
        int offYpx = TravelersBackpackConfig.CLIENT.toolsOverlay.offsetY.get(); // px@1080

        double px = offXpx / REF_W;
        double py = offYpx / REF_H;

        int scaledWidth = (int)Math.round(cx + px * sw);
        int scaledHeight = (int)Math.round(cy + py * sh);

        float progress = getOpenProgress();

        ItemStack backpack = CapabilityUtils.getWearingBackpack(player);
        ItemStack heldItem = player.getMainHandItem();

        //Hose Menu
        if(heldItem.getItem() instanceof HoseItem) {
            int hoveredResult = RadialToolsOverlay.renderRadial(graphics, backpack, heldItem, hoseMenu, false, scaledWidth, scaledHeight, mouseX, mouseY, partialTick, progress);

            if(!NeoForgeClientEventHandler.isKeyDown(ModClientEventHandler.SWAP_TOOL)) {
                selectHoseAction(mc.player, hoveredResult);
                onClose();
            }
            return;
        }

        NonNullList<ItemStack> tools = NbtHelper.getOrDefault(backpack, ModDataHelper.TOOLS_CONTAINER, NonNullList.withSize(NbtHelper.getOrDefault(backpack, ModDataHelper.TOOL_SLOTS, Tiers.LEATHER.getToolSlots()), ItemStack.EMPTY));
        int nonEmptyCount = getNonEmptyTools(tools).size();

        boolean canAdd = ToolSlotItemHandler.isValid(player.getMainHandItem()) && nonEmptyCount < tools.size();
        int hoveredResult = RadialToolsOverlay.renderRadial(graphics, backpack, heldItem, tools, canAdd, scaledWidth, scaledHeight, mouseX, mouseY, partialTick, progress);

        if(!NeoForgeClientEventHandler.isKeyDown(ModClientEventHandler.SWAP_TOOL)) {
            if(hoveredResult != -1) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.SWAP_TOOL, hoveredResult);
            }
            onClose();
        }
    }

    public void selectHoseAction(Player player, int hoveredResult) {
        if(hoveredResult == 0 || hoveredResult == 1 || hoveredResult == 4) {
            int mode = 1;
            if(hoveredResult == 0) mode = 1;
            if(hoveredResult == 1) mode = 3;
            if(hoveredResult == 4) mode = 2;
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SWITCH_HOSE_MODE, mode);
            player.displayClientMessage(getNextModeMessage(0, mode), true);
        }
        if(hoveredResult == 2 || hoveredResult == 3) {
            int tank = hoveredResult == 2 ? 2 : 1;
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SWITCH_HOSE_TANK, tank);
            player.displayClientMessage(getNextModeMessage(1, tank), true);
        }
    }

    public static Component getNextModeMessage(int changedMode, int data) {
        if(changedMode == 0) {
            if(data == HoseItem.SPILL_MODE) {
                return Component.translatable("item.travelersbackpack.hose.spill");
            } else if(data == HoseItem.DRINK_MODE) {
                return Component.translatable("item.travelersbackpack.hose.drink");
            }
            return Component.translatable("item.travelersbackpack.hose.suck");
        } else {
            if(data == 1) {
                return Component.translatable("item.travelersbackpack.hose.tank_left");
            } else {
                return Component.translatable("item.travelersbackpack.hose.tank_right");
            }
        }
    }

    public static NonNullList<ItemStack> getNonEmptyTools(NonNullList<ItemStack> inventory) {
        NonNullList<ItemStack> tools = NonNullList.create();
        for(ItemStack itemStack : inventory) {
            if(!itemStack.isEmpty()) {
                tools.add(itemStack);
            }
        }
        return tools;
    }

    public static final NonNullList<ItemStack> hoseMenu = createHoseMenu();

    public static NonNullList<ItemStack> createHoseMenu() {
        NonNullList<ItemStack> stacks = NonNullList.createWithCapacity(5);
        ItemStack suckHose = new ItemStack(ModItems.HOSE.get());
        NbtHelper.set(suckHose, ModDataHelper.HOSE_MODES, List.of(1, 0));
        ItemStack spitHose = new ItemStack(ModItems.HOSE.get());
        NbtHelper.set(spitHose, ModDataHelper.HOSE_MODES, List.of(2, 0));
        ItemStack drinkHose = new ItemStack(ModItems.HOSE.get());
        NbtHelper.set(drinkHose, ModDataHelper.HOSE_MODES, List.of(3, 0));
        stacks.add(suckHose);
        stacks.add(drinkHose);
        stacks.add(new ItemStack(ModItems.BACKPACK_TANK.get()));
        stacks.add(new ItemStack(ModItems.BACKPACK_TANK.get()));
        stacks.add(spitHose);
        return stacks;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}