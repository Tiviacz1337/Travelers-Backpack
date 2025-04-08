package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.client.screens.buttons.IButton;
import com.tiviacz.travelersbackpack.client.screens.widgets.InventoryScroll;
import com.tiviacz.travelersbackpack.client.screens.widgets.SettingsWidget;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.settings.*;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BackpackSettingsScreen extends AbstractBackpackScreen<BackpackSettingsMenu> implements MenuAccess<BackpackSettingsMenu> {
    public SettingsWidget settingsWidget;
    public UnsortablesWidget unsortablesWidget;
    public MemoryWidget memoryWidget;
    public VisibilityWidget visibilityWidget;
    public SupporterBadgeWidget supporterBadgeWidget; //Supporters Only!! :)
    public List<Integer> lastUnsortableSlots;
    public List<Integer> unsortableSlots = new ArrayList<>();
    public List<Pair<Integer, Pair<ItemStack, Boolean>>> lastMemorySlots;
    public List<Pair<Integer, Pair<ItemStack, Boolean>>> memorySlots = new ArrayList<>();
    public boolean visibility;

    public BackpackSettingsScreen(BackpackSettingsMenu backpackSettingsMenu, Inventory inventory, Component component) {
        super(backpackSettingsMenu, inventory, backpackSettingsMenu.getWrapper().getBackpackScreenTitle());
        //Init called internally
        recalculate();

        //Update position
        updateBackpackSlotsPosition();
        updatePlayerSlotsPosition();

        this.lastUnsortableSlots = new ArrayList<>(wrapper.getUnsortableSlots());
        this.unsortableSlots = new ArrayList<>(this.lastUnsortableSlots);
        this.lastMemorySlots = new ArrayList<>(wrapper.getMemorySlots());
        this.memorySlots = new ArrayList<>(this.lastMemorySlots);
        this.visibility = wrapper.getBackpackStack().getOrDefault(ModDataComponents.IS_VISIBLE, true);

        this.titleLabelX = 8;
    }

    @Override
    public void sendDataToServer() {
        this.unsortablesWidget.sendDataToServer();
        this.memoryWidget.sendDataToServer();
    }

    @Override
    protected void init() {
        super.init();
        initWidgets();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);

        this.isScrollable = false;
        recalculate();
        init();
        getMenu().updateSlots();

        //Update position
        updateBackpackSlotsPosition();
        updatePlayerSlotsPosition();
    }

    @Override
    protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, @Nullable String countString) {
        int i = slot.x;
        int j = slot.y;
        int j1 = slot.x + slot.y * this.imageWidth;
        if(slot.isFake()) {
            guiGraphics.renderFakeItem(itemstack, i, j, j1);
        } else {
            guiGraphics.renderItem(itemstack, i, j, j1);
        }
    }

    public void recalculate() {
        this.clearWidgets();

        this.slotCount = getWrapper().getStorage().getSlots();
        this.visibleSlots = this.slotCount;
        this.slotsHeight = calculateSlotHeight(slotCount > 81);

        this.leftPos = 0;
        this.topPos = 0;

        boolean wideTexture = slotCount > 81;
        this.wider = wideTexture;
        this.visibleRows = (int)Math.ceil((double)this.slotCount / getSlotsInRow());
        int playerInventoryHeight = 96;
        this.imageWidth = wideTexture ? 212 : 176;
        this.imageHeight = TOP_BAR_OFFSET + this.slotsHeight + playerInventoryHeight;

        updateDimensions();

        this.inventoryLabelY = 3 + TOP_BAR_OFFSET + (this.visibleRows * 18);
        this.inventoryLabelX = 8;
        this.titleLabelX = 8;
        this.titleLabelY = 6;

        if(wideTexture) {
            this.inventoryLabelX += 18;
        }

        //Cache first slot Y pos, ignore if not visible
        if(menu.getSlot(0).y >= 0) {
            this.slotYPos = menu.getSlot(0).y;
        }
    }

    @Override
    public void renderScreen(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        //Render Widgets underBg
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderBg(guiGraphics, x, y, mouseX, mouseY));

        boolean wideTexture = slotCount > 81;
        renderInventoryBackground(guiGraphics, x, y, wideTexture ? BackpackScreen.BACKGROUND_11 : BackpackScreen.BACKGROUND_9, imageWidth, this.slotsHeight);

        int slotsXOffset = 7;

        //Render Widgets aboveBg
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderAboveBg(guiGraphics, x, y, mouseX, mouseY, partialTicks));
        renderSlots(guiGraphics, x + slotsXOffset, y + TOP_BAR_OFFSET, slotCount);
        renderLockedBackpackSlot(guiGraphics);
    }

    public void initWidgets() {
        this.settingsWidget = new SettingsWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4), true);
        addRenderableWidget(this.settingsWidget);

        this.unsortablesWidget = new UnsortablesWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4 + 24 + 1));
        addRenderableWidget(this.unsortablesWidget);

        this.memoryWidget = new MemoryWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4 + 24 + 1 + 24 + 1), false);
        addRenderableWidget(this.memoryWidget);

        if(getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID) {
            this.visibilityWidget = new VisibilityWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4 + 24 + 1 + 24 + 1 + 24 + 1));
            addRenderableWidget(this.visibilityWidget);

            if(getWrapper().isOwner(this.getScreenPlayer()) && Supporters.SUPPORTERS_REFERENCE.contains(this.getScreenPlayer().getGameProfile().getName())) {
                this.supporterBadgeWidget = new SupporterBadgeWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4 + 24 + 1 + 24 + 1 + 24 + 1 + 24 + 1));
                addRenderableWidget(this.supporterBadgeWidget);
            }
        }

        if(this.isScrollable) {
            int scrollXPos = leftPos + 7;
            this.scroll = new InventoryScroll(this, Minecraft.getInstance(), 4, this.visibleRows * 18, topPos + TOP_BAR_OFFSET, scrollXPos + getSlotsInRow() * 18);
            if(this.scrollAmount != 0) {
                this.scroll.setScrollDistance(this.scrollAmount);
            }
            addRenderableWidget(this.scroll);
        }
    }

    public void closeTabs(SettingsWidgetBase openedWidget) {
        this.children().stream().filter(widget -> widget instanceof SettingsWidgetBase && widget != openedWidget).forEach(widget -> {
            ((SettingsWidgetBase)widget).tabOpened = false;
        });
    }

    public void updateWidgetsPosition(SettingsWidgetBase openedWidget) {
        this.closeTabs(openedWidget);
        List<SettingsWidgetBase> widgets = (List<SettingsWidgetBase>)this.children().stream().filter(w -> w instanceof SettingsWidgetBase).toList();
        for(int i = 0; i < widgets.size(); i++) {
            SettingsWidgetBase previousWidget = null;
            SettingsWidgetBase currentWidget = widgets.get(i);
            if(i > 0) {
                previousWidget = widgets.get(i - 1);
            }
            if(previousWidget == null) {
                continue;
            }
            int[] previousWidgetPosAndSize = previousWidget.getWidgetSizeAndPos();
            currentWidget.updatePos(previousWidgetPosAndSize[1], previousWidgetPosAndSize[3] + 1);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderTooltip(guiGraphics, mouseX, mouseY));
    }

    @Override
    public void drawUnsortableSlots(GuiGraphics guiGraphics) {
        if(this.unsortablesWidget.isTabOpened()) {
            if(!this.unsortableSlots.isEmpty()) {
                this.unsortableSlots.forEach(i -> guiGraphics.blit(BackpackScreen.ICONS, this.getGuiLeft() + getMenu().getSlot(i).x, this.getGuiTop() + getMenu().getSlot(i).y, 25, 55, 16, 16));
            }
        } else {
            if(!this.lastUnsortableSlots.isEmpty()) {
                this.lastUnsortableSlots.forEach(i -> guiGraphics.blit(BackpackScreen.ICONS, this.getGuiLeft() + getMenu().getSlot(i).x, this.getGuiTop() + getMenu().getSlot(i).y, 25, 55, 16, 16));
            }
        }
    }

    @Override
    public void drawMemorySlots(GuiGraphics guiGraphics) {
        if(this.memoryWidget.isTabOpened()) {
            if(!this.memorySlots.isEmpty()) {
                this.memorySlots.forEach(pair -> {
                    if(pair.getSecond().getSecond()) {
                        guiGraphics.blit(BackpackScreen.ICONS, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y, 25, 73, 16, 16);
                    } else {
                        guiGraphics.blit(BackpackScreen.ICONS, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y, 25, 91, 16, 16);
                    }

                    if(getMenu().getSlot(pair.getFirst()).getItem().isEmpty()) {
                        ItemStack itemstack = pair.getSecond().getFirst();
                        guiGraphics.renderFakeItem(itemstack, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y);
                        guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x + 16, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y + 16, 822083583);
                    }
                });
            }
        } else {
            if(!this.lastMemorySlots.isEmpty()) {
                this.lastMemorySlots.forEach(pair -> {
                    if(getMenu().getSlot(pair.getFirst()).getItem().isEmpty()) {
                        ItemStack itemstack = pair.getSecond().getFirst();
                        guiGraphics.renderFakeItem(itemstack, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y);
                        guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x + 16, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y + 16, 822083583);
                    }
                });
            }
        }
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int button, ClickType type) {
        //Selecting or unselecting unsortable slots by clicking the single slot
        if(selectSlots(slot, button)) {
            return;
        }
        super.slotClicked(slot, slotId, button, type);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        Slot slot = this.getSlotUnderMouse();
        //Selecting or unselecting unsortable and memory slots by dragging mouse cursor
        if(selectSlots(slot, button)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean selectSlots(Slot slot, int button) {
        if(slot != null && slot.index >= 0 && slot.index < wrapper.getStorage().getSlots()) {
            if(this.unsortablesWidget.isTabOpened()) {
                if(button == 0 && !this.unsortableSlots.contains(slot.index)) {
                    this.unsortableSlots.add(slot.index);
                    return true;
                }

                if(button == 1 && this.unsortableSlots.contains(slot.index)) {
                    this.unsortableSlots.remove((Object)slot.index);
                    return true;
                }
            }

            if(this.memoryWidget.isTabOpened()) {
                if(button == 0 && !this.memoryWidget.contains(slot.index, this.memorySlots)) {
                    if(slot.getItem().isEmpty()) {
                        return false;
                    }
                    this.memorySlots.add(Pair.of(slot.index, Pair.of(this.memoryWidget.matchComponents ? slot.getItem() : slot.getItem().getItem().getDefaultInstance(), this.memoryWidget.matchComponents)));
                    return true;
                }

                if(button == 1 && this.memoryWidget.contains(slot.index, this.memorySlots)) {
                    this.memorySlots.removeIf(p -> p.getFirst() == slot.index);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClose() {
        //Send Data to server if closed with ESC
        this.sendDataToServer();
        super.onClose();
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(ModClientEventHandler.OPEN_BACKPACK.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
            LocalPlayer playerEntity = this.getMinecraft().player;
            if(playerEntity != null) {
                this.onClose();
            }
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}