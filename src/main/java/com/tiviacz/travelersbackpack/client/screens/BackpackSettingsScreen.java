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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    public SupporterBadgeWidget supporterBadgeWidget; //Supporters Only!! :)
    private final BackpackWrapper wrapper;
    public List<Integer> lastUnsortableSlots;
    public List<Integer> unsortableSlots = new ArrayList<>();
    public List<Pair<Integer, Pair<ItemStack, Boolean>>> lastMemorySlots;
    public List<Pair<Integer, Pair<ItemStack, Boolean>>> memorySlots = new ArrayList<>();
    public boolean visibility;

    public InventoryScroll scroll = null;
    public int slotYPos;
    public boolean isScrollable = false;
    public int scrollAmount = 0; //0 - Top
    public static final int HEIGHT_WITHOUT_STORAGE = 118;
    public int slotsHeight;
    public int visibleSlots;
    public int visibleRows;

    public BackpackSettingsScreen(BackpackSettingsMenu backpackSettingsMenu, Inventory inventory, Component component) {
        super(backpackSettingsMenu, inventory, component);
        this.wrapper = backpackSettingsMenu.getWrapper();
        //Init called internally
        recalculate();

        //Update position
        updateBackpackSlotsPosition();
        updatePlayerSlotsPosition();

        this.lastUnsortableSlots = new ArrayList<>(wrapper.getUnsortableSlots());
        this.unsortableSlots = new ArrayList<>(this.lastUnsortableSlots);
        this.lastMemorySlots = new ArrayList<>(wrapper.getMemorySlots());
        this.memorySlots = new ArrayList<>(this.lastMemorySlots);
        this.visibility = wrapper.getBackpackStack().getOrDefault(ModDataComponents.IS_VISIBLE.get(), true);
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
        this.imageHeight = 15 + TOP_BAR_OFFSET + this.slotsHeight + playerInventoryHeight;

        updateDimensions();

        this.inventoryLabelY = 18 + TOP_BAR_OFFSET + (this.visibleRows * 18);
        this.inventoryLabelX = 8;

        if(wideTexture) {
            this.inventoryLabelX += 18;
        }

        //Cache first slot Y pos, ignore if not visible
        if(menu.getSlot(0).y >= 0) {
            this.slotYPos = menu.getSlot(0).y;
        }
    }

    public void updateDimensions() {
        int guiScaledHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        if(guiScaledHeight < imageHeight) {
            int displayableNumberOfRows = Math.min((guiScaledHeight - HEIGHT_WITHOUT_STORAGE) / 18, getRows());
            int newImageHeight = 15 + HEIGHT_WITHOUT_STORAGE + calculateSlotHeight(displayableNumberOfRows);

            this.slotsHeight = calculateSlotHeight(displayableNumberOfRows);
            this.visibleSlots = displayableNumberOfRows * (this.slotCount > 81 ? 11 : 9);
            this.imageHeight = newImageHeight;
            this.visibleRows = displayableNumberOfRows;
            this.isScrollable = true;
        }
    }

    public void renderInventoryBackground(GuiGraphics guiGraphics, int x, int y, ResourceLocation texture, int xSize, int slotsHeight) {
        int halfSlotHeight = slotsHeight / 2;
        guiGraphics.blit(texture, x, y, 0, 0, xSize, TOP_BAR_OFFSET + halfSlotHeight);
        int playerInventoryHeight = 97;
        guiGraphics.blit(texture, x, y + TOP_BAR_OFFSET + halfSlotHeight, 0, 256 - (playerInventoryHeight + halfSlotHeight), xSize, playerInventoryHeight + halfSlotHeight);
    }

    public void renderSlots(GuiGraphics guiGraphics, int x, int y, int slotCount) {
        int lastSlotRow = this.slotCount % getSlotsInRow();
        int visibleRows = this.visibleRows;
        int fullRows = this.isScrollable ? visibleRows : slotCount / getSlotsInRow();

        if(this.isScrollable && this.scrollAmount == getMaxScrollAmount()) {
            if(lastSlotRow > 0) {
                fullRows--;
            }
        }

        //Full Rows
        guiGraphics.blit(BackpackScreen.SLOTS, x, y, 0, 0, getSlotsInRow() * 18, fullRows * 18);

        //Last Row
        if(lastSlotRow > 0) {
            if(this.isScrollable) {
                if(this.scrollAmount == getMaxScrollAmount()) {
                    guiGraphics.blit(BackpackScreen.SLOTS, x, y + fullRows * 18, 0, fullRows * 18, lastSlotRow * 18, 18);
                }
            } else {
                guiGraphics.blit(BackpackScreen.SLOTS, x, y + fullRows * 18, 0, fullRows * 18, lastSlotRow * 18, 18);
            }
        }
    }

    /* public void renderSlots(GuiGraphics guiGraphics, int x, int y, int slotCount, int slotsInRow) {
        int lastSlotRow = slotCount % slotsInRow;
        int fullRows = slotCount / slotsInRow;

        //Full Rows
        guiGraphics.blit(BackpackScreen.SLOTS, x, y, 0, 0, slotsInRow * 18, fullRows * 18);

        //Last Row
        if(lastSlotRow > 0) {
            guiGraphics.blit(BackpackScreen.SLOTS, x, y + fullRows * 18, 0, fullRows * 18, lastSlotRow * 18, 18);
        }
    } */

    public void renderScreen(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        //Render Widgets underBg
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderBg(guiGraphics, x, y, mouseX, mouseY));

        boolean wideTexture = slotCount > 81;
        renderInventoryBackground(guiGraphics, x, y, wideTexture ? BackpackScreen.BACKGROUND_11 : BackpackScreen.BACKGROUND_9, imageWidth, this.slotsHeight);

        int slotsXOffset = 7;

        //Render Widgets aboveBg
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderAboveBg(guiGraphics, x, y, mouseX, mouseY, partialTicks));
        renderSlots(guiGraphics, x + slotsXOffset, y + TOP_BAR_OFFSET, this.slotCount);
    }

    public int calculateSlotHeight(int displayableRows) {
        return displayableRows * 18;
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
        this.settingsWidget = new SettingsWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4 + 15), true);
        addRenderableWidget(this.settingsWidget);

        this.unsortablesWidget = new UnsortablesWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4 + 24 + 1 + 15));
        addRenderableWidget(this.unsortablesWidget);

        this.memoryWidget = new MemoryWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4 + 24 + 1 + 24 + 1 + 15), false);
        addRenderableWidget(this.memoryWidget);

        if(getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID) {
            this.visibilityWidget = new VisibilityWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4 + 24 + 1 + 24 + 1 + 24 + 1 + 15));
            addRenderableWidget(this.visibilityWidget);

            if(getWrapper().isOwner(this.getScreenPlayer()) && Supporters.SUPPORTERS_REFERENCE.contains(this.getScreenPlayer().getGameProfile().getName())) {
                this.supporterBadgeWidget = new SupporterBadgeWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4 + 24 + 1 + 24 + 1 + 24 + 1 + 24 + 1 + 15));
                addRenderableWidget(this.supporterBadgeWidget);
            }
        }

        if(this.isScrollable) {
            int scrollXPos = leftPos + 7; //leftPos + (wider ? 27 : 9) + (tanksVisible ? 22 : (wider ? 0 : 18));
            this.scroll = new InventoryScroll(this, Minecraft.getInstance(), 4, this.visibleRows * 18, topPos + 15 + TOP_BAR_OFFSET, scrollXPos + getSlotsInRow() * 18);
            if(this.scrollAmount != 0) {
                this.scroll.setScrollDistance(this.scrollAmount);
            }
            addRenderableWidget(this.scroll);
        }
    }

    public void setScrollAmount(int scrollAmount) {
        this.scrollAmount = scrollAmount;
    }

    public void initButtons() {
        buttons.clear();
    }

    public int getRows() {
        return (int)Math.ceil((double)this.slotCount / getSlotsInRow());
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

    public void updateBackpackSlotsPosition() {
        int allStorageSlots = this.slotCount;
        if(this.isScrollable) {
            int scrollAmount = this.scrollAmount;
            int hiddenSlotsFirst = scrollAmount * getSlotsInRow();
            int movedSlots = (this.visibleRows * getSlotsInRow()) - hiddenSlotsFirst; //Start from firstYPos
            int revealedSlots = scrollAmount * getSlotsInRow();
            int lastRowSlots = this.slotCount % getSlotsInRow();

            if(scrollAmount == getMaxScrollAmount() && lastRowSlots > 0) {
                revealedSlots -= getSlotsInRow();
                revealedSlots += lastRowSlots;
            }

            for(int i = 0; i < hiddenSlotsFirst; i++) {
                menu.slots.get(i).y = -1000;
            }
            int countSlots = 0;
            for(int i = hiddenSlotsFirst; i < hiddenSlotsFirst + movedSlots; i++) {
                menu.slots.get(i).y = slotYPos + (int)Math.floor((double)countSlots / getSlotsInRow()) * 18;
                countSlots++;
            }
            countSlots = 0;
            int lastY = slotYPos + (this.visibleRows - scrollAmount) * 18;
            for(int i = hiddenSlotsFirst + movedSlots; i < hiddenSlotsFirst + movedSlots + revealedSlots; i++) {
                menu.slots.get(i).y = lastY + (int)Math.floor((double)countSlots / getSlotsInRow()) * 18;
                countSlots++;
            }
            for(int i = hiddenSlotsFirst + movedSlots + revealedSlots; i < allStorageSlots; i++) {
                menu.slots.get(i).y = -1000;
            }
        }
    }

    public void updatePlayerSlotsPosition() {
        if(this.isScrollable) {
            int firstPlayerSlotY = 29 + TOP_BAR_OFFSET + (this.visibleRows * 18);
            //Inventory
            int countSlots = 0;
            for(int i = this.slotCount; i < this.slotCount + 3 * 9; i++) {
                menu.slots.get(i).y = firstPlayerSlotY + (int)Math.floor((double)countSlots / 9) * 18;
                countSlots++;
            }
            //Hotbar
            for(int i = this.slotCount + 3 * 9; i < this.slotCount + 4 * 9; i++) {
                menu.slots.get(i).y = firstPlayerSlotY + (3 * 18) + 4;
            }
        }
    }

    public int getSlotsInRow() {
        return this.wider ? 11 : 9;
    }

    public int getMaxScrollAmount() {
        return (int)Math.ceil((double)this.slotCount / getSlotsInRow()) - (int)Math.ceil((double)this.visibleSlots / getSlotsInRow());
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
        renderScreen(guiGraphics, x, y + 15, mouseX, mouseY, partialTicks);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(this.scroll != null) {
            return this.scroll.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        Slot slot = this.getSlotUnderMouse();
        //Selecting or unselecting unsortable and memory slots by dragging mouse cursor
        if(selectSlots(slot, button)) {
            return true;
        }
        for(GuiEventListener child : children()) {
            if(child.isMouseOver(mouseX, mouseY) && child.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        GuiEventListener focused = getFocused();
        if(focused != null && !focused.isMouseOver(mouseX, mouseY) && (focused instanceof WidgetBase widgetBase)) {
            widgetBase.setFocused(false);
        }
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
