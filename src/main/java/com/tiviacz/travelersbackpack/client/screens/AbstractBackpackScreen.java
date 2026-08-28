package com.tiviacz.travelersbackpack.client.screens;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.widgets.InventoryScroll;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.AbstractBackpackMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractBackpackScreen<T extends AbstractBackpackMenu> extends AbstractContainerScreen<T> {
    public static final Identifier BACKGROUND_11 = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/background_11.png");
    public static final Identifier BACKGROUND_9 = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/background_9.png");
    public static final Identifier SLOTS = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/slots.png");
    public static final Identifier TANKS = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/tanks.png");
    public static final Identifier ICONS = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/icons.png");
    public static final Identifier TABS = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/tabs.png");
    public static final int TOP_BAR_OFFSET = 17;
    protected int slotCount;
    protected boolean wider = false;
    protected final BackpackWrapper wrapper;

    public InventoryScroll scroll = null;
    public int slotYPos;
    public boolean isScrollable = false;
    public int scrollAmount = 0; //0 - Top
    public static final int HEIGHT_WITHOUT_STORAGE = 114;
    public int slotsHeight;
    public int visibleSlots;
    public int visibleRows;

    protected AbstractBackpackScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.wrapper = pMenu.getWrapper();

        this.titleLabelY = 6;
    }

    public BackpackWrapper getWrapper() {
        return this.wrapper;
    }

    public Player getScreenPlayer() {
        return getMenu().player;
    }

    public Font getScreenFont() {
        return this.font;
    }

    public void updateDimensions() {
        int guiScaledHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        if(guiScaledHeight < imageHeight) {
            int displayableNumberOfRows = Math.min((guiScaledHeight - HEIGHT_WITHOUT_STORAGE) / 18, getRows());
            int newImageHeight = HEIGHT_WITHOUT_STORAGE + calculateSlotHeight(displayableNumberOfRows);

            this.slotsHeight = calculateSlotHeight(displayableNumberOfRows);
            this.visibleSlots = displayableNumberOfRows * (this.slotCount > 81 ? 11 : 9);
            this.imageHeight = newImageHeight;
            this.visibleRows = displayableNumberOfRows;
            this.isScrollable = true;
        }
    }

    public void updateBackpackSlotsPosition() {
        int allStorageSlots = menu.BACKPACK_INV_END;
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
            int firstPlayerSlotY = 15 + TOP_BAR_OFFSET + (this.visibleRows * 18);
            //Inventory
            int countSlots = 0;
            for(int i = menu.PLAYER_INV_START; i < menu.PLAYER_HOT_END - 9; i++) {
                menu.slots.get(i).y = firstPlayerSlotY + (int)Math.floor((double)countSlots / 9) * 18;
                countSlots++;
            }
            //Hotbar
            for(int i = menu.PLAYER_HOT_END - 9; i < menu.PLAYER_HOT_END; i++) {
                menu.slots.get(i).y = firstPlayerSlotY + (3 * 18) + 4;
            }
        }
    }

    public int calculateSlotHeight(int displayableRows) {
        return displayableRows * 18;
    }

    public int calculateSlotHeight(boolean wider) {
        int rowSlots = wider ? 11 : 9;
        int rows = (int)Math.ceil((double)slotCount / rowSlots);
        return rows * 18;
    }

    public int getRows() {
        return (int)Math.ceil((double)this.slotCount / getSlotsInRow());
    }

    public int getSlotsInRow() {
        return this.wider ? 11 : 9;
    }

    public void setScrollAmount(int scrollAmount) {
        this.scrollAmount = scrollAmount;
    }

    public int getMaxScrollAmount() {
        return (int)Math.ceil((double)this.slotCount / getSlotsInRow()) - (int)Math.ceil((double)this.visibleSlots / getSlotsInRow());
    }

    public void renderInventoryBackground(GuiGraphicsExtractor guiGraphics, int x, int y, Identifier texture, int xSize, int slotsHeight) {
        int halfSlotHeight = slotsHeight / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, xSize, TOP_BAR_OFFSET + halfSlotHeight, 256, 256);
        int playerInventoryHeight = 98;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y + TOP_BAR_OFFSET + halfSlotHeight, 0, 256 - (playerInventoryHeight + halfSlotHeight), xSize, playerInventoryHeight + halfSlotHeight, 256, 256);
    }

    public void renderSlots(GuiGraphicsExtractor guiGraphics, int x, int y, int slotCount) {
        int lastSlotRow = this.slotCount % getSlotsInRow();
        int visibleRows = this.visibleRows;
        int fullRows = this.isScrollable ? visibleRows : slotCount / getSlotsInRow();

        if(this.isScrollable && this.scrollAmount == getMaxScrollAmount()) {
            if(lastSlotRow > 0) {
                fullRows--;
            }
        }

        //Full Rows
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SLOTS, x, y, 0, 0, getSlotsInRow() * 18, fullRows * 18, 256, 256);
        if(fullRows > 9) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SLOTS, x, y + (9 * 18), 0, 0, getSlotsInRow() * 18, (fullRows - 9) * 18, 256, 256);
        }

        //Last Row
        if(lastSlotRow > 0) {
            if(this.isScrollable) {
                if(this.scrollAmount == getMaxScrollAmount()) {
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SLOTS, x, y + fullRows * 18, 0, fullRows * 18, lastSlotRow * 18, 18, 256, 256);
                }
            } else {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SLOTS, x, y + fullRows * 18, 0, 0, lastSlotRow * 18, 18, 256, 256);
            }
        }
    }

    public void renderLockedBackpackSlot(GuiGraphicsExtractor guiGraphics) {
        if(menu.disabledSlotIndex > 0 && menu.disabledSlotIndex < menu.slots.size()) {
            if(menu.getSlot(menu.disabledSlotIndex) instanceof DisabledSlot slot) {
                int x = leftPos + slot.x;
                int y = topPos + slot.y;
                guiGraphics.fill(RenderPipelines.GUI, x, y, x + 16, y + 16, (0xFF << 24) | (0x68 << 16) | (0x68 << 8) | 0x68);
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackground(guiGraphics, mouseX, mouseY, a);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        renderScreen(guiGraphics, x, y, mouseX, mouseY, a);
        drawUnsortableSlots(guiGraphics);
        drawMemorySlots(guiGraphics);
    }

    public abstract void renderScreen(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks);

    public abstract void drawUnsortableSlots(GuiGraphicsExtractor guiGraphics);

    public abstract void drawMemorySlots(GuiGraphicsExtractor guiGraphics);

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if(super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                return true;
            }
        }
        if(this.scroll != null && !(TravelersBackpack.mouseTweaksLoaded && this.hoveredSlot != null)) {
            return this.scroll.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        for(GuiEventListener child : children()) {
            if(child.isMouseOver(event.x(), event.y()) && child.mouseDragged(event, dragX, dragY)) {
                return true;
            }
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean b1) {
        GuiEventListener focused = getFocused();
        if(focused != null && !focused.isMouseOver(event.x(), event.y()) && (focused instanceof WidgetBase widgetBase)) {
            widgetBase.setFocused(false);
        }
        return super.mouseClicked(event, b1);
    }

    public void sendDataToServer() {

    }

    public void playUIClickSound() {
        menu.getPlayerInventory().player.level().playSound(menu.getPlayerInventory().player, menu.getPlayerInventory().player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.MASTER, 0.25F, 1.0F);
    }
}