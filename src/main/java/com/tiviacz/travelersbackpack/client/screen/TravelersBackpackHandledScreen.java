package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screen.widget.*;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TravelersBackpackHandledScreen extends HandledScreen<TravelersBackpackBaseScreenHandler>
{
    public static final Identifier SCREEN_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack.png");
    public static final Identifier SETTINGS_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack_settings.png");
    private static final ScreenImageButton BED_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton EQUIP_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton UNEQUIP_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton DISABLED_CRAFTING_BUTTON = new ScreenImageButton(225, 96, 18, 18);
    private static final ScreenImageButton ABILITY_SLIDER = new ScreenImageButton(5, 56,18, 11);
    public ControlTab controlTab;
    public SettingsWidget settingsWidget;
    public SortWidget sortWidget;
    public MemoryWidget memoryWidget;

    public final ITravelersBackpackInventory inventory;
    private final PlayerInventory playerInventory;
    private final byte screenID;
    private final TankScreen tankLeft;
    private final TankScreen tankRight;

    public TravelersBackpackHandledScreen(TravelersBackpackBaseScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.inventory = handler.inventory;
        this.playerInventory = inventory;
        this.screenID = handler.inventory.getScreenID();

        this.x = 0;
        this.y = 0;

        this.backgroundWidth = 248;
        this.backgroundHeight = 207;

        this.tankLeft = new TankScreen(handler.inventory.getLeftTank(), 25, 7, 100, 16);
        this.tankRight = new TankScreen(handler.inventory.getRightTank(), 207, 7, 100, 16);
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    @Override
    protected void init()
    {
        super.init();
        initControlTab();
        initSettingsTab();
    }

    public void initControlTab()
    {
        this.controlTab = new ControlTab(this, x + 61, y - 10, 61, 13);
        addSelectableChild(controlTab);
    }

    public void initSettingsTab()
    {
        this.settingsWidget = new SettingsWidget(this, x + backgroundWidth, y + 10, 15, 18);
        addSelectableChild(settingsWidget);
        this.sortWidget = new SortWidget(this, x + backgroundWidth, y + 29, 15, 18);
        addSelectableChild(sortWidget);
        this.memoryWidget = new MemoryWidget(this, x + backgroundWidth, y + 48, 15, 18);
        addSelectableChild(memoryWidget);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {}

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        if(!this.inventory.getLeftTank().isResourceBlank())
        {
            this.tankLeft.drawScreenFluidBar(this, matrices);
        }
        if(!this.inventory.getRightTank().isResourceBlank())
        {
            this.tankRight.drawScreenFluidBar(this, matrices);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SCREEN_TRAVELERS_BACKPACK);

        if(TravelersBackpackConfig.disableCrafting)
        {
            DISABLED_CRAFTING_BUTTON.draw(matrices, this, 77, 208);
        }

        if(inventory.hasTileEntity())
        {
            if(BED_BUTTON.inButton(this, mouseX, mouseY))
            {
                BED_BUTTON.draw(matrices, this, 20, 227);
            }
            else
            {
                BED_BUTTON.draw(matrices, this, 1, 227);
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, inventory.getItemStack()))
            {
                if(ABILITY_SLIDER.inButton(this, mouseX, mouseY))
                {
                    if(inventory.getAbilityValue())
                    {
                        ABILITY_SLIDER.draw(matrices, this, 115, 208);
                    }
                    else
                    {
                        ABILITY_SLIDER.draw(matrices, this, 115, 220);
                    }
                }
                else
                {
                    if(inventory.getAbilityValue())
                    {
                        ABILITY_SLIDER.draw(matrices, this, 96, 208);
                    }
                    else
                    {
                        ABILITY_SLIDER.draw(matrices, this, 96, 220);
                    }
                }
            }
        }
        else
        {
            if(!ComponentUtils.isWearingBackpack(getScreenHandler().playerInventory.player) && this.screenID == Reference.ITEM_SCREEN_ID)
            {
                if(EQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    EQUIP_BUTTON.draw(matrices, this, 58, 208);
                }
                else
                {
                    EQUIP_BUTTON.draw(matrices,this, 39, 208);
                }
            }

            if(ComponentUtils.isWearingBackpack(getScreenHandler().playerInventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
            {
                if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, inventory.getItemStack()))
                {
                    if(ABILITY_SLIDER.inButton(this, mouseX, mouseY))
                    {
                        if(inventory.getAbilityValue())
                        {
                            ABILITY_SLIDER.draw(matrices, this, 115, 208);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(matrices, this, 115, 220);
                        }
                    }
                    else
                    {
                        if(inventory.getAbilityValue())
                        {
                            ABILITY_SLIDER.draw(matrices, this, 96, 208);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(matrices, this, 96, 220);
                        }
                    }
                }

                if(UNEQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    UNEQUIP_BUTTON.draw(matrices,this, 58, 227);
                }
                else
                {
                    UNEQUIP_BUTTON.draw(matrices,this, 39, 227);
                }
            }
        }

        this.controlTab.render(matrices, mouseX, mouseY, delta);

        this.settingsWidget.render(matrices, mouseX, mouseY, delta);
        this.children().stream().filter(w -> w instanceof WidgetBase).filter(w -> ((WidgetBase) w).isSettingsChild() && ((WidgetBase) w).isVisible()).forEach(w -> ((WidgetBase) w).render(matrices, mouseX, mouseY, delta));

        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y)
    {
        super.drawMouseoverTooltip(matrices, x, y);

        if(this.tankLeft.inTank(this, x, y))
        {
            this.renderTooltip(matrices, tankLeft.getTankTooltip(), x, y);
        }

        if(this.tankRight.inTank(this, x, y))
        {
            this.renderTooltip(matrices, tankRight.getTankTooltip(), x, y);
        }

        if(this.screenID == Reference.BLOCK_ENTITY_SCREEN_ID || this.screenID == Reference.WEARABLE_SCREEN_ID)
        {
            if(BackpackAbilities.isOnList(this.screenID == Reference.WEARABLE_SCREEN_ID ? BackpackAbilities.ITEM_ABILITIES_LIST : BackpackAbilities.BLOCK_ABILITIES_LIST, inventory.getItemStack()) && ABILITY_SLIDER.inButton(this, x, y))
            {
                if(inventory.getAbilityValue())
                {
                    List<Text> list = new ArrayList<>();
                    list.add(new TranslatableText("screen.travelersbackpack.ability_enabled"));
                    if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_TIMER_ABILITIES_LIST, inventory.getItemStack()) || BackpackAbilities.isOnList(BackpackAbilities.BLOCK_TIMER_ABILITIES_LIST, inventory.getItemStack()))
                    {
                        list.add(inventory.getLastTime() == 0 ? new TranslatableText("screen.travelersbackpack.ability_ready") : new LiteralText(BackpackUtils.getConvertedTime(inventory.getLastTime())));
                    }
                    this.renderTooltip(matrices, list, x, y);
                }
                else
                {
                    if(!TravelersBackpackConfig.enableBackpackAbilities)
                    {
                        this.renderTooltip(matrices, new TranslatableText("screen.travelersbackpack.ability_disabled_config"), x, y);
                    }
                    else
                    {
                        this.renderTooltip(matrices, new TranslatableText("screen.travelersbackpack.ability_disabled"), x, y);
                    }
                }
            }
        }

        if(TravelersBackpack.enableTrinkets())
        {
            if(ComponentUtils.isWearingBackpack(getScreenHandler().playerInventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
            {
                if(UNEQUIP_BUTTON.inButton(this, x, y))
                {
                    this.renderTooltip(matrices, new TranslatableText("screen.travelersbackpack.unequip_integration"), x, y);
                }
            }

            if(!ComponentUtils.isWearingBackpack(getScreenHandler().playerInventory.player) && this.screenID == Reference.ITEM_SCREEN_ID)
            {
                if(EQUIP_BUTTON.inButton(this, x, y))
                {
                    this.renderTooltip(matrices, new TranslatableText("screen.travelersbackpack.equip_integration"), x, y);
                }
            }
        }

        if(TravelersBackpackConfig.disableCrafting)
        {
            if(DISABLED_CRAFTING_BUTTON.inButton(this, x, y))
            {
                this.renderTooltip(matrices, new TranslatableText("screen.travelersbackpack.disabled_crafting"), x, y);
            }
        }
    }

    public int getX(int slot)
    {
        if(slot <= 7)
        {
            return 62 + (18 * (slot));
        }
        else if(slot >= 8 && slot <= 15)
        {
            return 62 + (18 * (slot - 8));
        }
        else if(slot >= 16 && slot <= 23)
        {
            return 62 + (18 * (slot - 16));
        }
        else if(slot >= 24 && slot <= 28)
        {
            return 62 + (18 * (slot - 24));
        }
        else if(slot >= 29 && slot <= 33)
        {
            return 62 + (18 * (slot - 29));
        }
        else if(slot >= 34 && slot <= 38)
        {
            return 62 + (18 * (slot - 34));
        }

        return 0;
    }

    public int getY(int slot)
    {
        if(slot <= 7) return 7;
        else if(slot <= 15) return 25;
        else if(slot <= 23) return 43;
        else if(slot <= 28) return 61;
        else if(slot <= 33) return 79;
        else if(slot <= 38) return 97;

        return 0;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SCREEN_TRAVELERS_BACKPACK);
        this.client.getTextureManager().bindTexture(SCREEN_TRAVELERS_BACKPACK);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if(!inventory.getSlotManager().getUnsortableSlots().isEmpty())
        {
            inventory.getSlotManager().getUnsortableSlots()
                    .forEach(i -> drawTexture(matrices, this.getX() + getX(i), this.getY() + getY(i), 78, 228, 16, 16));
        }

        if(!inventory.getSlotManager().getMemorySlots().isEmpty())
        {
            this.setZOffset(100);
            this.itemRenderer.zOffset = 100.0F;

            inventory.getSlotManager().getMemorySlots()
                    .forEach(pair -> {

                        if(!inventory.getInventory().getStack(pair.getFirst()).isEmpty()) return;

                        ItemStack itemstack = pair.getSecond();
                        RenderSystem.enableDepthTest();
                        this.itemRenderer.renderInGuiWithOverrides(this.client.player, itemstack, this.getX() + getX(pair.getFirst()), this.getY() + getY(pair.getFirst()), 100);
                        drawMemoryOverlay(matrices, this.getX() + getX(pair.getFirst()), this.getY() + getY(pair.getFirst()));
                    });

            this.itemRenderer.zOffset = 0.0F;
            this.setZOffset(0);
        }
    }

    public void drawMemoryOverlay(MatrixStack matrices, int x, int y)
    {
        matrices.push();
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SCREEN_TRAVELERS_BACKPACK);
        drawTexture(matrices, x, y, 97, 232, 16, 16);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType)
    {
        super.onMouseClick(slot, slotId, button, actionType);

        if((slotId >= 10 && slotId <= 48) && inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
        {
            inventory.getSlotManager().setUnsortableSlot(slotId - 10);
        }

        if((slotId >= 10 && slotId <= 48) && inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY) && (!slot.getStack().isEmpty() || (slot.getStack().isEmpty() && inventory.getSlotManager().isSlot(SlotManager.MEMORY, slotId - 10))))
        {
            inventory.getSlotManager().setMemorySlot(slotId - 10, slot.getStack());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if((inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) && !this.sortWidget.isMouseOver(mouseX, mouseY)) || (inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY) && !this.memoryWidget.isMouseOver(mouseX, mouseY)))
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if(!inventory.getLeftTank().isResourceBlank())
        {
            if(this.tankLeft.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeByte(screenID).writeByte(Reference.EMPTY_TANK).writeDouble(1.0D);

                ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, buf);

                if(inventory.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(1, playerInventory.player, inventory.getWorld(), inventory.getScreenID());
            }
        }

        if(!inventory.getRightTank().isResourceBlank())
        {
            if(this.tankRight.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeByte(screenID).writeByte(Reference.EMPTY_TANK).writeDouble(2.0D);

                ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, buf);

                if(inventory.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(2, playerInventory.player, inventory.getWorld(), inventory.getScreenID());
            }
        }

        if(inventory.hasTileEntity())
        {
            if(BED_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
            {
                ClientPlayNetworking.send(ModNetwork.DEPLOY_SLEEPING_BAG_ID, PacketByteBufs.create().writeBlockPos(inventory.getPosition()));
                return true;
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, inventory.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY))
            {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeByte(screenID).writeBoolean(!inventory.getAbilityValue());

                ClientPlayNetworking.send(ModNetwork.ABILITY_SLIDER_ID, buf);
                playUIClickSound();
                return true;
            }
        }

        if(!inventory.hasTileEntity())
        {
            if(!TravelersBackpack.enableTrinkets())
            {
                if(!ComponentUtils.isWearingBackpack(getScreenHandler().playerInventory.player) && this.screenID == Reference.ITEM_SCREEN_ID)
                {
                    if(EQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeBoolean(true);

                        ClientPlayNetworking.send(ModNetwork.EQUIP_BACKPACK_ID, buf);
                        return true;
                    }
                }

                if(ComponentUtils.isWearingBackpack(getScreenHandler().playerInventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
                {
                    if(UNEQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeBoolean(false);

                        ClientPlayNetworking.send(ModNetwork.EQUIP_BACKPACK_ID, buf);
                        return true;
                    }
                }
            }

            if(ComponentUtils.isWearingBackpack(getScreenHandler().playerInventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
            {
                if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, inventory.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY))
                {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeByte(screenID).writeBoolean(!inventory.getAbilityValue());

                    ClientPlayNetworking.send(ModNetwork.ABILITY_SLIDER_ID, buf);
                    playUIClickSound();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void playUIClickSound()
    {
        playerInventory.player.world.playSound(playerInventory.player, playerInventory.player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 0.25F, 1.0F);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(KeybindHandler.OPEN_INVENTORY.matchesKey(keyCode, scanCode))
        {
            ClientPlayerEntity playerEntity = this.client.player;

            if(playerEntity != null)
            {
                this.close();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}