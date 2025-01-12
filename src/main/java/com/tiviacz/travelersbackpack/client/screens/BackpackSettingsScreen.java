package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.client.screens.buttons.IButton;
import com.tiviacz.travelersbackpack.client.screens.widgets.SettingsWidget;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.settings.MemoryWidget;
import com.tiviacz.travelersbackpack.client.screens.widgets.settings.SettingsWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.settings.UnsortablesWidget;
import com.tiviacz.travelersbackpack.client.screens.widgets.settings.VisibilityWidget;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
public class BackpackSettingsScreen extends AbstractContainerScreen<BackpackSettingsMenu> implements MenuAccess<BackpackSettingsMenu>, IBackpackScreen {
    public static final int TOP_BAR_OFFSET = 7;
    public int slotCount;
    boolean wider = false;
    public List<IButton> buttons = new ArrayList<>();
    public SettingsWidget settingsWidget;
    public UnsortablesWidget unsortablesWidget;
    public MemoryWidget memoryWidget;
    public VisibilityWidget visibilityWidget;
    private final BackpackWrapper wrapper;
    public List<Integer> lastUnsortableSlots;
    public List<Integer> unsortableSlots = new ArrayList<>();
    public List<Pair<Integer, Pair<ItemStack, Boolean>>> lastMemorySlots;
    public List<Pair<Integer, Pair<ItemStack, Boolean>>> memorySlots = new ArrayList<>();
    public boolean visibility;

    public BackpackSettingsScreen(BackpackSettingsMenu backpackSettingsMenu, Inventory inventory, Component component) {
        super(backpackSettingsMenu, inventory, component);
        this.wrapper = backpackSettingsMenu.getWrapper();
        recalculate();

        this.lastUnsortableSlots = new ArrayList<>(wrapper.getUnsortableSlots());
        this.unsortableSlots = new ArrayList<>(this.lastUnsortableSlots);
        this.lastMemorySlots = new ArrayList<>(wrapper.getMemorySlots());
        this.memorySlots = new ArrayList<>(this.lastMemorySlots);
        this.visibility = wrapper.getBackpackStack().getOrDefault(ModDataComponents.IS_VISIBLE, true);
        //GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().getWindow(), this.leftPos + this.imageWidth - 3, this.topPos + 4);
    }

    @Override
    public BackpackWrapper getWrapper() {
        return this.wrapper;
    }

    @Override
    public Player getScreenPlayer() {
        return getMenu().player;
    }

    @Override
    public void sendDataToServer() {
        this.unsortablesWidget.sendDataToServer();
        this.memoryWidget.sendDataToServer();
    }

    @Override
    protected void init() {
        super.init();
        initButtons();
        initWidgets();
    }

    @Override
    protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, @Nullable String countString) {
        GuiGraphics p_281607_ = guiGraphics;
        Slot p_282613_ = slot;
        String s = countString;
        int i = slot.x;
        int j = slot.y;
        int j1 = p_282613_.x + p_282613_.y * this.imageWidth;
        if(p_282613_.isFake()) {
            p_281607_.renderFakeItem(itemstack, i, j, j1);
        } else {
            p_281607_.renderItem(itemstack, i, j, j1);
        }

        //p_281607_.renderItemDecorations(this.font, itemstack, i, j, s);
    }

    public void recalculate() {
        this.clearWidgets();

        this.slotCount = getWrapper().getStorage().getSlots();

        this.leftPos = 0;
        this.topPos = 0;

        boolean wideTexture = slotCount > 81;
        wider = wideTexture;
        int playerInventoryHeight = 96;
        this.imageWidth = wideTexture ? 212 : 176;
        this.imageHeight = TOP_BAR_OFFSET + calculateSlotHeight(wideTexture) + playerInventoryHeight;

        this.inventoryLabelY = this.imageHeight - 93;
        this.inventoryLabelX = 8;

        if(wideTexture) {
            this.inventoryLabelX += 18;
        }
    }

    public void renderInventoryBackground(GuiGraphics guiGraphics, int x, int y, ResourceLocation texture, int xSize, int slotsHeight) {
        int halfSlotHeight = slotsHeight / 2;
        guiGraphics.blit(texture, x, y, 0, 0, xSize, TOP_BAR_OFFSET + halfSlotHeight);
        int playerInventoryHeight = 97;
        guiGraphics.blit(texture, x, y + TOP_BAR_OFFSET + halfSlotHeight, 0, 256 - (playerInventoryHeight + halfSlotHeight), xSize, playerInventoryHeight + halfSlotHeight);
    }

    public void renderSlots(GuiGraphics guiGraphics, int x, int y, int slotCount, int slotsInRow) {
        int lastSlotRow = slotCount % slotsInRow;
        int fullRows = slotCount / slotsInRow;

        //Full Rows
        guiGraphics.blit(BackpackScreen.SLOTS, x, y, 0, 0, slotsInRow * 18, fullRows * 18);

        //Last Row
        if(lastSlotRow > 0) {
            guiGraphics.blit(BackpackScreen.SLOTS, x, y + fullRows * 18, 0, fullRows * 18, lastSlotRow * 18, 18);
        }
    }

    public void renderScreen(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        //Render Widgets underBg
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderBg(guiGraphics, x, y, mouseX, mouseY));

        boolean wideTexture = slotCount > 81;
        renderInventoryBackground(guiGraphics, x, y, wideTexture ? BackpackScreen.BACKGROUND_11 : BackpackScreen.BACKGROUND_9, imageWidth, calculateSlotHeight(wideTexture));

        int slotsXOffset = 7;

        //Render Widgets aboveBg
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderAboveBg(guiGraphics, x, y, mouseX, mouseY, partialTicks));
        renderSlots(guiGraphics, x + slotsXOffset, y + TOP_BAR_OFFSET, slotCount, wideTexture ? 11 : 9);
    }

    public int calculateSlotHeight(boolean wider) {
        int rowSlots = wider ? 11 : 9;
        int rows = (int)Math.ceil((double)slotCount / rowSlots);
        return rows * 18;
    }

    @Override
    public Font getFont() {
        return this.font;
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
        }
    }

    public void initButtons() {
        buttons.clear();
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
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.buttons.forEach(button -> button.render(guiGraphics, mouseX, mouseY, partialTicks));
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        this.buttons.forEach(button -> button.renderTooltip(guiGraphics, mouseX, mouseY));
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderTooltip(guiGraphics, mouseX, mouseY));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        renderScreen(guiGraphics, x, y, mouseX, mouseY, partialTicks);
        drawUnsortableSlots(guiGraphics);
        drawMemorySlots(guiGraphics);
    }

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
                    //ItemStack itemstack = pair.getSecond().getFirst();
                    //guiGraphics.renderFakeItem(itemstack, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y);
                    //guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x + 16, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y + 16, 822083583);
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
                    //ItemStack itemstack = pair.getSecond().getFirst();
                    //guiGraphics.renderFakeItem(itemstack, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y);
                    //guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x + 16, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y + 16, 822083583);
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
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        Slot slot = this.getSlotUnderMouse();
        //Selecting or unselecting unsortable and memory slots by dragging mouse cursor
        if(selectSlots(slot, pButton)) {
            return true;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void playUIClickSound() {
        menu.getPlayerInventory().player.level().playSound(menu.getPlayerInventory().player, menu.getPlayerInventory().player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.MASTER, 0.25F, 1.0F);
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
