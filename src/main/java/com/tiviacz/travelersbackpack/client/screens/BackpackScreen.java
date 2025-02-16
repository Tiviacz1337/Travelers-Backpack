package com.tiviacz.travelersbackpack.client.screens;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.buttons.*;
import com.tiviacz.travelersbackpack.client.screens.widgets.*;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.sorter.ContainerSorter;
import com.tiviacz.travelersbackpack.inventory.upgrades.IUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.item.upgrades.TanksUpgradeItem;
import com.tiviacz.travelersbackpack.network.ServerboundSorterPacket;
import com.tiviacz.travelersbackpack.util.BackpackDeathHelper;
import com.tiviacz.travelersbackpack.util.FluidTypeHelper;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class BackpackScreen extends AbstractContainerScreen<BackpackBaseMenu> implements MenuAccess<BackpackBaseMenu>, IBackpackScreen {
    public static final ResourceLocation BACKGROUND_11 = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/background_11.png");
    public static final ResourceLocation BACKGROUND_9 = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/background_9.png");
    public static final ResourceLocation SLOTS = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/slots.png");
    public static final ResourceLocation TANKS = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/tanks.png");
    public static final ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/icons.png");
    public static final ResourceLocation TABS = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/tabs.png");
    public static final int TOP_BAR_OFFSET = 17;
    public int slotCount;
    public boolean tanksVisible;
    public int upgradeSlotCount;
    public List<UpgradeSlot> upgradeSlots = new ArrayList<>();
    boolean upgradesInitialized = false;
    boolean wider = false;
    public List<IButton> buttons = new ArrayList<>();
    public SortingButtons sortingButtons;
    public ToolSlotsWidget toolSlotsWidget;
    public SettingsWidget settingsWidget;
    private final BackpackWrapper wrapper;
    public int warningTicks = 0;
    public boolean showAllButtons = false;
    public InventoryScroll scroll = null;

    public int slotYPos;
    public boolean isScrollable = false;
    public int scrollAmount = 0; //0 - Top
    public static final int HEIGHT_WITHOUT_STORAGE = 114;
    public int slotsHeight;
    public int visibleSlots;
    public int visibleRows;

    public BackpackScreen(BackpackBaseMenu backpackMenu, Inventory inventory, Component component) {
        super(backpackMenu, inventory, backpackMenu.getWrapper().getBackpackScreenTitle());
        this.wrapper = backpackMenu.getWrapper();
        //Init getting called internally
        recalculate();

        //Update position
        updateBackpackSlotsPosition();
        updatePlayerSlotsPosition();

        this.titleLabelX = 8 + (tanksVisible ? 22 : 0);
        this.titleLabelY = 6;
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
    public Slot getHoveredSlot() {
        return this.hoveredSlot;
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

        //Reload screen
        updateScreen(true);
        getMenu().updateSlots();

        //Update position
        updateBackpackSlotsPosition();
        updatePlayerSlotsPosition();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if(this.warningTicks > 0) {
            this.warningTicks--;
        }
    }

    public int getGuiLeft() {
        return leftPos;
    }

    public int getGuiTop() {
        return topPos;
    }

    public void recalculate() {
        this.clearWidgets();
        upgradeSlots.clear();

        this.slotCount = getWrapper().getStorage().getSlots();
        this.visibleSlots = this.slotCount;
        this.slotsHeight = calculateSlotHeight(slotCount > 81);
        this.tanksVisible = getWrapper().tanksVisible();
        this.upgradeSlotCount = getWrapper().getUpgrades().getSlots();

        this.leftPos = 0;
        this.topPos = 0;

        boolean wideTexture = slotCount > 81;
        this.wider = wideTexture;
        this.visibleRows = (int)Math.ceil((double)this.slotCount / getSlotsInRow());
        int playerInventoryHeight = 96;
        this.imageWidth = wideTexture ? (tanksVisible ? 256 : 212) : (tanksVisible ? 220 : 176);
        this.imageHeight = TOP_BAR_OFFSET + this.slotsHeight + playerInventoryHeight;

        updateDimensions();

        this.inventoryLabelY = 3 + TOP_BAR_OFFSET + (this.visibleRows * 18);
        this.inventoryLabelX = 8;
        this.titleLabelX = 8 + (tanksVisible ? 22 : 0);
        this.titleLabelY = 6;

        if(tanksVisible) {
            this.inventoryLabelX += 22;
        }

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
            int newImageHeight = HEIGHT_WITHOUT_STORAGE + calculateSlotHeight(displayableNumberOfRows);

            this.slotsHeight = calculateSlotHeight(displayableNumberOfRows);
            this.visibleSlots = displayableNumberOfRows * (this.slotCount > 81 ? 11 : 9);
            this.imageHeight = newImageHeight;
            this.visibleRows = displayableNumberOfRows;
            this.isScrollable = true;
        }
    }

    public int getRows() {
        return (int)Math.ceil((double)this.slotCount / getSlotsInRow());
    }

    public int getMiddleBar() {
        return TOP_BAR_OFFSET + calculateSlotHeight(this.visibleRows) + 1;
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

    public int getWidthAdditions() {
        int addition = 0;
        if(tanksVisible) addition += 22;
        if(wider) addition += 18;
        return addition;
    }

    public void updateScreen(boolean duplicatedCall) {
        this.isScrollable = false;
        recalculate();
        init();

        //Update position
        if(!duplicatedCall) {
            updateBackpackSlotsPosition();
            updatePlayerSlotsPosition();
        }
    }

    public void renderInventoryBackground(GuiGraphics guiGraphics, int x, int y, ResourceLocation texture, int xSize, int slotsHeight) {
        int halfSlotHeight = slotsHeight / 2;
        guiGraphics.blit(RenderType::guiTextured, texture, x, y, 0, 0, xSize, TOP_BAR_OFFSET + halfSlotHeight, 256, 256);
        int playerInventoryHeight = 98;
        guiGraphics.blit(RenderType::guiTextured, texture, x, y + TOP_BAR_OFFSET + halfSlotHeight, 0, 256 - (playerInventoryHeight + halfSlotHeight), xSize, playerInventoryHeight + halfSlotHeight, 256, 256);
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
        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 0, 0, getSlotsInRow() * 18, fullRows * 18, 256, 256);

        //Last Row
        if(lastSlotRow > 0) {
            if(this.isScrollable) {
                if(this.scrollAmount == getMaxScrollAmount()) {
                    guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y + fullRows * 18, 0, fullRows * 18, lastSlotRow * 18, 18, 256, 256);
                }
            } else {
                guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y + fullRows * 18, 0, fullRows * 18, lastSlotRow * 18, 18, 256, 256);
            }
        }
    }

    public void renderScreen(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {

        //Render widgets below inventory
        renderUpgradeSlots(guiGraphics, x, y);

        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderBg(guiGraphics, x, y, mouseX, mouseY));

        boolean wideTexture = slotCount > 81;
        int inventoryXOffset = tanksVisible ? 22 : 0;
        renderInventoryBackground(guiGraphics, x + inventoryXOffset, y, wideTexture ? BACKGROUND_11 : BACKGROUND_9, imageWidth, this.slotsHeight);

        int slotsXOffset = 7;

        if(tanksVisible) {
            slotsXOffset = 29;

            int halfTankHeight = this.slotsHeight / 2;
            int tanksHeight = 90;
            int uOffset = 56;
            int posOffset = 193;
            if(wideTexture) {
                uOffset = 0;
                posOffset = 229;
            }

            //Left Tank
            guiGraphics.blit(RenderType::guiTextured, TANKS, x, y, 0, 0, 27, TOP_BAR_OFFSET - 9 + halfTankHeight, 256, 256);
            guiGraphics.blit(RenderType::guiTextured, TANKS, x, y + TOP_BAR_OFFSET - 9 + halfTankHeight, uOffset, 256 - (tanksHeight + halfTankHeight + TOP_BAR_OFFSET), 27, tanksHeight + halfTankHeight + TOP_BAR_OFFSET - 9, 256, 256);

            //Right Tank
            guiGraphics.blit(RenderType::guiTextured, TANKS, x + posOffset, y, uOffset + 28, 0, 27, TOP_BAR_OFFSET - 9 + halfTankHeight, 256, 256);
            guiGraphics.blit(RenderType::guiTextured, TANKS, x + posOffset, y + TOP_BAR_OFFSET - 9 + halfTankHeight, uOffset + 28, 256 - (tanksHeight + halfTankHeight + TOP_BAR_OFFSET), 27, tanksHeight + halfTankHeight + TOP_BAR_OFFSET - 9, 256, 256);
        }

        //Render Upgrades
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

    public void renderUpgradeSlots(GuiGraphics guiGraphics, int x, int y) {
        for(UpgradeSlot slot : upgradeSlots) {
            slot.render(guiGraphics, x, y);
        }
    }

    public void initializeUpgradeSlots() {
        for(int i = 0; i < upgradeSlotCount; i++) {
            int x = menu.upgradeSlot.get(i).x - 4;
            int y = menu.upgradeSlot.get(i).y - 4;
            upgradeSlots.add(new UpgradeSlot(getWrapper().getUpgrades(), new Point(getGuiLeft() + x, getGuiTop() + y), i, x, y, menu.upgradeSlot.get(i).isHidden));
        }

        upgradesInitialized = true;
    }

    @Override
    public Font getFont() {
        return this.font;
    }

    public void initWidgets() {
        if(this.isScrollable) {
            int scrollXPos = leftPos + 7 + (tanksVisible ? 22 : 0);
            this.scroll = new InventoryScroll(this, Minecraft.getInstance(), 4, this.visibleRows * 18, topPos + TOP_BAR_OFFSET, scrollXPos + getSlotsInRow() * 18);
            if(this.scrollAmount != 0) {
                this.scroll.setScrollDistance(this.scrollAmount);
            }
            addRenderableWidget(this.scroll);
        }

        this.settingsWidget = new SettingsWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4), false);
        addRenderableWidget(this.settingsWidget);

        int xPos = leftPos + (wider ? 36 : 0) + (tanksVisible ? 22 : 0) + 120;
        this.sortingButtons = new SortingButtons(this, new Point(xPos, topPos - 10 + 12), 50, 13);
        addRenderableWidget(this.sortingButtons);

        int xPosTools = (wider ? 36 : 0) + (tanksVisible ? 22 : 0);
        this.toolSlotsWidget = new ToolSlotsWidget(this, new Point(this.leftPos + xPosTools + 110, topPos - 10 + 15), xPosTools);
        addRenderableWidget(this.toolSlotsWidget);

        UpgradeManager manager = getWrapper().getUpgradeManager();

        for(Optional<? extends IUpgrade> upgrade : manager.mappedUpgrades.values()) {
            upgrade.ifPresent(loadedUpgrade -> {
                int x;
                int y;

                x = menu.upgradeSlot.get(getWrapper().getUpgradeManager().slotMappedUpgrades.get(upgrade)).x - 4;
                y = menu.upgradeSlot.get(getWrapper().getUpgradeManager().slotMappedUpgrades.get(upgrade)).y - 4;

                addRenderableWidget(loadedUpgrade.createWidget(this, x, y));
            });
        }

        initializeUpgradeSlots();
    }

    public void initButtons() {
        buttons.clear();
        int xOffset = 0;
        if(getWrapper().getScreenID() == Reference.ITEM_SCREEN_ID) {
            if(!TravelersBackpack.enableIntegration()) {
                buttons.add(new EquipButton(this));
                xOffset += 12;
            }
        }
        if(getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID && getWrapper().isOwner(getMenu().player)) {
            buttons.add(new MoreButton(this));

            if(!TravelersBackpack.enableIntegration()) {
                buttons.add(new UnequipButton(this));
                xOffset += 12;
            }

            if(TravelersBackpackConfig.getConfig().backpackSettings.quickSleepingBag) {
                buttons.add(new SleepingBagButton(this, true, xOffset));
                xOffset += 12;
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, getWrapper().getBackpackStack())) {
                buttons.add(new AbilitySliderButton(this, false, xOffset));
            }
        }
        if(getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
            buttons.add(new MoreButton(this));
            buttons.add(new SleepingBagButton(this, false, 0));

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, getWrapper().getBackpackStack())) {
                buttons.add(new AbilitySliderButton(this, true, 12));
            }
        }
    }

    public void setScrollAmount(int scrollAmount) {
        this.scrollAmount = scrollAmount;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        this.buttons.forEach(button -> {
            if(showAllButtons || button instanceof MoreButton || button instanceof EquipButton) {
                button.render(guiGraphics, mouseX, mouseY, partialTicks);
            }
        });

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        this.buttons.forEach(button -> {
            if(showAllButtons || button instanceof MoreButton || button instanceof EquipButton) {
                button.renderTooltip(guiGraphics, mouseX, mouseY);
            }
        });
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderTooltip(guiGraphics, mouseX, mouseY));

        if(warningTicks > 0) {
            if(!(menu.getCarried().getItem() instanceof TanksUpgradeItem)) {
                warningTicks = 0;
            }

            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("screen.travelersbackpack.cant_apply_upgrade"));
            tooltip.add(Component.translatable("screen.travelersbackpack.too_much_fluid"));
            FluidVariantWrapper leftFluidStack = TanksUpgradeItem.getLeftFluidStack(menu.getCarried());
            FluidVariantWrapper rightFluidStack = TanksUpgradeItem.getRightFluidStack(menu.getCarried());

            if(!leftFluidStack.isEmpty() && leftFluidStack.getAmount() > getWrapper().getBackpackTankCapacity()) {
                tooltip.add(crateFluidWarning(leftFluidStack, getWrapper().getBackpackTankCapacity()));
            }

            if(!rightFluidStack.isEmpty() && rightFluidStack.getAmount() > getWrapper().getBackpackTankCapacity()) {
                tooltip.add(crateFluidWarning(rightFluidStack, getWrapper().getBackpackTankCapacity()));
            }
            guiGraphics.renderTooltip(getFont(), tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    public Component crateFluidWarning(FluidVariantWrapper fluidVariantWrapper, long backpackCapacity) {
        return Component.literal(FluidTypeHelper.getFluidVariantName(fluidVariantWrapper.fluidVariant()).getString() + " " + fluidVariantWrapper.amount() + "/" + backpackCapacity + "mB").withStyle(ChatFormatting.RED);
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
        if(!getWrapper().getUnsortableSlots().isEmpty()) {
            getWrapper().getUnsortableSlots().forEach(i -> guiGraphics.blit(RenderType::guiTextured, ICONS, this.getGuiLeft() + getMenu().getSlot(i).x, this.getGuiTop() + getMenu().getSlot(i).y, 25, 55, 16, 16, 256, 256));
        }
    }

    public void drawMemorySlots(GuiGraphics guiGraphics) {
        if(!getWrapper().getMemorySlots().isEmpty()) {
            getWrapper().getMemorySlots().forEach(pair -> {
                if(getMenu().getSlot(pair.getFirst()).getItem().isEmpty()) {
                    ItemStack itemstack = pair.getSecond().getFirst();
                    guiGraphics.renderFakeItem(itemstack, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y);
                    guiGraphics.fill(RenderType.guiGhostRecipeOverlay(), this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y, this.getGuiLeft() + getMenu().getSlot(pair.getFirst()).x + 16, this.getGuiTop() + getMenu().getSlot(pair.getFirst()).y + 16, 822083583);
                }
            });
        }
    }

    @Override
    protected boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop, int pMouseButton) {
        if(!this.menu.getCarried().isEmpty()) {
            for(GuiEventListener widget : children()) {
                if(widget instanceof WidgetBase base) {
                    if(base.isMouseOver(pMouseX, pMouseY)) return false;
                }
            }
        }
        return pMouseX < (double)pGuiLeft || pMouseY < (double)pGuiTop || pMouseX >= (double)(pGuiLeft + this.imageWidth) || pMouseY >= (double)(pGuiTop + this.imageHeight);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.buttons.forEach(b -> {
            if(showAllButtons || b instanceof MoreButton || b instanceof EquipButton) {
                b.mouseClicked(mouseX, mouseY, button);
            }
        });
        GuiEventListener focused = getFocused();
        if(focused != null && !focused.isMouseOver(mouseX, mouseY) && (focused instanceof WidgetBase widgetBase)) {
            widgetBase.setFocused(false);
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
        for(GuiEventListener child : children()) {
            if(child.isMouseOver(mouseX, mouseY) && child.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public int getSlotsInRow() {
        return this.wider ? 11 : 9;
    }

    public int getMaxScrollAmount() {
        return (int)Math.ceil((double)this.slotCount / getSlotsInRow()) - (int)Math.ceil((double)this.visibleSlots / getSlotsInRow());
    }

    @Override
    public void playUIClickSound() {
        menu.getPlayerInventory().player.level().playSound(menu.getPlayerInventory().player, menu.getPlayerInventory().player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.MASTER, 0.25F, 1.0F);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(KeybindHandler.SORT_BACKPACK.matches(pKeyCode, pScanCode)) {
            PacketDistributor.sendToServer(new ServerboundSorterPacket(getWrapper().getScreenID(), ContainerSorter.SORT_BACKPACK, BackpackDeathHelper.isShiftPressed()));
            playUIClickSound();
            return true;
        }
        if(KeybindHandler.OPEN_BACKPACK.matches(pKeyCode, pScanCode)) {
            LocalPlayer playerEntity = this.minecraft.player;
            if(playerEntity != null) {
                this.onClose();
            }
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public static void displayTanksUpgradeWarning(Player player) {
        if(player.level().isClientSide) {
            if(Minecraft.getInstance().screen instanceof BackpackScreen screen) {
                screen.warningTicks = 60;
            }
        }
    }
}
