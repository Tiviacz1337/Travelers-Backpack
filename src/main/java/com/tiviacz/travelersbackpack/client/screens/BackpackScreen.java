package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.buttons.*;
import com.tiviacz.travelersbackpack.client.screens.widgets.*;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.inventory.CommonFluid;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.sorter.ContainerSorter;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.items.upgrades.TanksUpgradeItem;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.KeyHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class BackpackScreen extends AbstractBackpackScreen<BackpackBaseMenu> implements MenuAccess<BackpackBaseMenu> {
    public boolean tanksVisible;
    public List<UpgradeSlot> upgradeSlots = new ArrayList<>();
    boolean upgradesInitialized = false;
    public List<IButton> buttons = new ArrayList<>();
    public SortingButtons sortingButtons;
    public ToolSlotsWidget toolSlotsWidget;
    public SettingsWidget settingsWidget;
    public int warningTicks = 0;

    public BackpackScreen(BackpackBaseMenu backpackMenu, Inventory inventory, Component component) {
        super(backpackMenu, inventory, backpackMenu.getWrapper().getBackpackScreenTitle());
        //Init getting called internally
        recalculate();

        //Update position
        updateBackpackSlotsPosition();
        updatePlayerSlotsPosition();

        this.titleLabelX = 8 + (tanksVisible ? 22 : 0);
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

    public void recalculate() {
        this.clearWidgets();
        upgradeSlots.clear();

        this.slotCount = getWrapper().getStorage().getSlots();
        this.visibleSlots = this.slotCount;
        this.slotsHeight = calculateSlotHeight(slotCount > 81);
        this.tanksVisible = getWrapper().tanksVisible();

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

    public int getMiddleBar() {
        return TOP_BAR_OFFSET + calculateSlotHeight(this.visibleRows) + 1;
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

    @Override
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
            guiGraphics.blit(TANKS, x, y, 0, 0, 27, TOP_BAR_OFFSET - 9 + halfTankHeight);
            guiGraphics.blit(TANKS, x, y + TOP_BAR_OFFSET - 9 + halfTankHeight, uOffset, 256 - (tanksHeight + halfTankHeight + TOP_BAR_OFFSET), 27, tanksHeight + halfTankHeight + TOP_BAR_OFFSET - 9);

            //Right Tank
            guiGraphics.blit(TANKS, x + posOffset, y, uOffset + 28, 0, 27, TOP_BAR_OFFSET - 9 + halfTankHeight);
            guiGraphics.blit(TANKS, x + posOffset, y + TOP_BAR_OFFSET - 9 + halfTankHeight, uOffset + 28, 256 - (tanksHeight + halfTankHeight + TOP_BAR_OFFSET), 27, tanksHeight + halfTankHeight + TOP_BAR_OFFSET - 9);
        }

        //Render Upgrades
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderAboveBg(guiGraphics, x, y, mouseX, mouseY, partialTicks));

        renderSlots(guiGraphics, x + slotsXOffset, y + TOP_BAR_OFFSET, this.slotCount);
        renderLockedBackpackSlot(guiGraphics);
    }

    public void renderUpgradeSlots(GuiGraphics guiGraphics, int x, int y) {
        for(UpgradeSlot slot : upgradeSlots) {
            slot.render(guiGraphics, x, y);
        }
    }

    public void initializeUpgradeSlots() {
        for(int i = 0; i < getWrapper().getUpgrades().getSlots(); i++) {
            int x = menu.upgradeSlot.get(i).x - 4;
            int y = menu.upgradeSlot.get(i).y - 4;
            upgradeSlots.add(new UpgradeSlot(getWrapper(), new Point(getGuiLeft() + x, getGuiTop() + y), i, x, y, menu.upgradeSlot.get(i).isHidden));
        }

        upgradesInitialized = true;
    }

    public void initWidgets() {
        this.settingsWidget = new SettingsWidget(this, new Point(this.leftPos + this.imageWidth - 3, this.topPos + 4), false);
        addRenderableWidget(this.settingsWidget);

        int xPos = leftPos + (wider ? 36 : 0) + (tanksVisible ? 22 : 0) + 120;
        this.sortingButtons = new SortingButtons(this, new Point(xPos, topPos - 10 + 12), 50, 13);
        addRenderableWidget(this.sortingButtons);

        int xPosTools = (wider ? 36 : 0) + (tanksVisible ? 22 : 0);
        this.toolSlotsWidget = new ToolSlotsWidget(this, new Point(this.leftPos + xPosTools + 110, topPos - 10 + 15), xPosTools);
        addRenderableWidget(this.toolSlotsWidget);

        UpgradeManager manager = getWrapper().getUpgradeManager();

        for(int i : manager.mappedUpgrades.keySet()) {
            Optional<UpgradeBase<?>> upgrade = manager.mappedUpgrades.get(i);
            upgrade.ifPresent(loadedUpgrade -> {
                int x = menu.upgradeSlot.get(i).x - 4;
                int y = menu.upgradeSlot.get(i).y - 4;

                addRenderableWidget(loadedUpgrade.createWidget(this, x, y));
            });
        }

        initializeUpgradeSlots();

        if(this.isScrollable) {
            int scrollXPos = leftPos + 7 + (tanksVisible ? 22 : 0); //leftPos + (wider ? 27 : 9) + (tanksVisible ? 22 : (wider ? 0 : 18));
            this.scroll = new InventoryScroll(this, Minecraft.getInstance(), 4, this.visibleRows * 18, topPos + TOP_BAR_OFFSET, scrollXPos + getSlotsInRow() * 18);
            if(this.scrollAmount != 0) {
                this.scroll.setScrollDistance(this.scrollAmount);
            }
            addRenderableWidget(this.scroll);
        }
    }

    public void initButtons() {
        buttons.clear();
        int xOffset = 0;
        if(getWrapper().getScreenID() == Reference.ITEM_SCREEN_ID && getWrapper().getBackpackSlotIndex() == getScreenPlayer().getInventory().selected) {
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

            if(TravelersBackpackConfig.SERVER.backpackSettings.quickSleepingBag.get()) {
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

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        this.buttons.forEach(button -> {
            if(getWrapper().showMoreButtons() || button instanceof MoreButton || button instanceof EquipButton) {
                button.render(guiGraphics, mouseX, mouseY, partialTicks);
            }
        });

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        this.buttons.forEach(button -> {
            if(getWrapper().showMoreButtons() || button instanceof MoreButton || button instanceof EquipButton) {
                button.renderTooltip(guiGraphics, mouseX, mouseY);
            }
        });
        this.children().stream().filter(w -> w instanceof WidgetBase).forEach(w -> ((WidgetBase)w).renderTooltip(guiGraphics, mouseX, mouseY));

        renderFluidWarningTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void drawUnsortableSlots(GuiGraphics guiGraphics) {
        if(!getWrapper().getUnsortableSlots().isEmpty()) {
            getWrapper().getUnsortableSlots().forEach(i -> guiGraphics.blit(ICONS, this.getGuiLeft() + getMenu().getSlot(i).x, this.getGuiTop() + getMenu().getSlot(i).y, 25, 55, 16, 16));
        }
    }

    @Override
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
            if(getWrapper().showMoreButtons() || b instanceof MoreButton || b instanceof EquipButton) {
                b.mouseClicked(mouseX, mouseY, button);
            }
        });
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(getChildAt(mouseX, mouseY).isPresent()) {
            GuiEventListener child = getChildAt(mouseX, mouseY).get();
            if(child instanceof FilterUpgradeWidgetBase<?, ?> widget) {
                if(widget.getUpgrade().isTagSelector()) {
                    if(widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                        return true;
                    }
                }
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(ModClientEventHandler.SORT_BACKPACK.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SORTER, ContainerSorter.SORT_BACKPACK, KeyHelper.isShiftPressed());
            playUIClickSound();
            return true;
        }
        if(ModClientEventHandler.OPEN_BACKPACK.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
            LocalPlayer playerEntity = this.getMinecraft().player;
            if(playerEntity != null && (hoveredSlot == null || !(hoveredSlot.getItem().getItem() instanceof TravelersBackpackItem))) {
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

    //Forge

    public void renderFluidWarningTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(warningTicks > 0) {
            if(!(menu.getCarried().getItem() instanceof TanksUpgradeItem)) {
                warningTicks = 0;
            }

            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("screen.travelersbackpack.cant_apply_upgrade"));
            tooltip.add(Component.translatable("screen.travelersbackpack.too_much_fluid"));
            FluidStack leftFluidStack = TanksUpgradeItem.getLeftFluidStack(menu.getCarried());
            FluidStack rightFluidStack = TanksUpgradeItem.getRightFluidStack(menu.getCarried());

            if(!leftFluidStack.isEmpty() && leftFluidStack.getAmount() > getWrapper().getBackpackTankCapacity()) {
                tooltip.add(crateFluidWarning(leftFluidStack, getWrapper().getBackpackTankCapacity()));
            }

            if(!rightFluidStack.isEmpty() && rightFluidStack.getAmount() > getWrapper().getBackpackTankCapacity()) {
                tooltip.add(crateFluidWarning(rightFluidStack, getWrapper().getBackpackTankCapacity()));
            }
            guiGraphics.renderTooltip(getFont(), tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    public Component crateFluidWarning(FluidStack fluidStack, int backpackCapacity) {
        return Component.literal(CommonFluid.getFluidName(fluidStack).getString() + " " + fluidStack.getAmount() + "/" + backpackCapacity + "mB").withStyle(ChatFormatting.RED);
    }
}