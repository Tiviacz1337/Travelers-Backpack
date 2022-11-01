package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBaseScreenHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.InventorySorter;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
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
import java.util.Arrays;
import java.util.List;

public class TravelersBackpackHandledScreen extends HandledScreen<TravelersBackpackBaseScreenHandler>
{
    public static final Identifier SCREEN_TRAVELERS_BACKPACK = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack.png");
    private static final ScreenImageButton BED_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton EQUIP_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton UNEQUIP_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton DISABLED_CRAFTING_BUTTON = new ScreenImageButton(225, 96, 18, 18);
    private static final ScreenImageButton ABILITY_SLIDER = new ScreenImageButton(5, 56,18, 11);
    private static final ScreenImageButton SORT_BUTTON = new ScreenImageButton(61, -10, 14, 13);
    private static final ScreenImageButton QUICK_STACK_BUTTON = new ScreenImageButton(75, -10, 11, 13);
    private static final ScreenImageButton TRANSFER_TO_BACKPACK_BUTTON = new ScreenImageButton(86, -10, 11, 13);
    private static final ScreenImageButton TRANSFER_TO_PLAYER_BUTTON = new ScreenImageButton(97, -10, 14, 13);
    private final ITravelersBackpackInventory inventory;
    private final byte screenID;
    private final TankScreen tankLeft;
    private final TankScreen tankRight;

    public TravelersBackpackHandledScreen(TravelersBackpackBaseScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
        this.inventory = handler.inventory;
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
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
    {
        if(!this.inventory.getLeftTank().isResourceBlank())
        {
            this.tankLeft.drawScreenFluidBar(matrices);
        }
        if(!this.inventory.getRightTank().isResourceBlank())
        {
            this.tankRight.drawScreenFluidBar(matrices);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);

        if(this.tankLeft.inTank(this, mouseX, mouseY))
        {
            this.renderTooltip(matrices, tankLeft.getTankTooltip(), mouseX, mouseY);
        }

        if(this.tankRight.inTank(this, mouseX, mouseY))
        {
            this.renderTooltip(matrices, tankRight.getTankTooltip(), mouseX, mouseY);
        }

        if(BackpackUtils.isShiftPressed())
        {
            if(SORT_BUTTON.inButton(this, mouseX, mouseY, 65))
            {
                List<Text> list = new ArrayList<>();
                list.add(new TranslatableText("screen.travelersbackpack.sort"));
                list.add(new TranslatableText("screen.travelersbackpack.sort_shift"));

                this.renderTooltip(matrices, list, mouseX, mouseY);
            }

            if(QUICK_STACK_BUTTON.inButton(this, mouseX, mouseY, 76))
            {
                List<Text> list = new ArrayList<>();
                list.add(new TranslatableText("screen.travelersbackpack.quick_stack"));
                list.add(new TranslatableText("screen.travelersbackpack.quick_stack_shift"));

                this.renderTooltip(matrices, list, mouseX, mouseY);
            }

            if(TRANSFER_TO_BACKPACK_BUTTON.inButton(this, mouseX, mouseY, 87))
            {
                List<Text> list = new ArrayList<>();
                list.add(new TranslatableText("screen.travelersbackpack.transfer_to_backpack"));
                list.add(new TranslatableText("screen.travelersbackpack.transfer_to_backpack_shift"));

                this.renderTooltip(matrices, list, mouseX, mouseY);
            }

            if(TRANSFER_TO_PLAYER_BUTTON.inButton(this, mouseX, mouseY, 98))
            {
                this.renderTooltip(matrices, new TranslatableText("screen.travelersbackpack.transfer_to_player"), mouseX, mouseY);
            }
        }

        if(this.screenID == Reference.BLOCK_ENTITY_SCREEN_ID || this.screenID == Reference.WEARABLE_SCREEN_ID)
        {
            if(BackpackAbilities.isOnList(this.screenID == Reference.WEARABLE_SCREEN_ID ? BackpackAbilities.ITEM_ABILITIES_LIST : BackpackAbilities.BLOCK_ABILITIES_LIST, inventory.getItemStack()) && ABILITY_SLIDER.inButton(this, mouseX, mouseY))
            {
                if(inventory.getAbilityValue())
                {
                    List<Text> list = new ArrayList<>();
                    list.add(new TranslatableText("screen.travelersbackpack.ability_enabled"));
                    if(Arrays.stream(BackpackAbilities.ITEM_TIMER_ABILITIES_LIST).anyMatch(s -> s.asItem() == inventory.getItemStack().getItem() || BackpackAbilities.isOnList(BackpackAbilities.BLOCK_TIMER_ABILITIES_LIST, inventory.getItemStack())))
                    {
                        list.add(inventory.getLastTime() == 0 ? new TranslatableText("screen.travelersbackpack.ability_ready") : new LiteralText(BackpackUtils.getConvertedTime(inventory.getLastTime())));
                    }
                    this.renderTooltip(matrices, list, mouseX, mouseY);
                }
                else
                {
                    if(!TravelersBackpackConfig.enableBackpackAbilities)
                    {
                        this.renderTooltip(matrices, new TranslatableText("screen.travelersbackpack.ability_disabled_config"), mouseX, mouseY);
                    }
                    else
                    {
                        this.renderTooltip(matrices, new TranslatableText("screen.travelersbackpack.ability_disabled"), mouseX, mouseY);
                    }
                }
            }
        }

        if(TravelersBackpackConfig.disableCrafting)
        {
            if(DISABLED_CRAFTING_BUTTON.inButton(this, mouseX, mouseY))
            {
                this.renderTooltip(matrices, new TranslatableText("screen.travelersbackpack.disabled_crafting"), mouseX, mouseY);
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
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(SCREEN_TRAVELERS_BACKPACK);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if(!inventory.getSlotManager().getUnsortableSlots().isEmpty())
        {
            inventory.getSlotManager().getUnsortableSlots()
                    .forEach(i -> drawTexture(matrices, this.getX() + getX(i), this.getY() + getY(i), 78, 228, 16, 16));
        }

        if(TravelersBackpackConfig.disableCrafting)
        {
            DISABLED_CRAFTING_BUTTON.draw(matrices, this, 77, 208);
        }

        if(SORT_BUTTON.inButton(this, mouseX, mouseY, 65))
        {
            SORT_BUTTON.draw(matrices, this, 134, 222);
        }
        else
        {
            SORT_BUTTON.draw(matrices, this, 134, 208);
        }

        if(inventory.getSlotManager().isActive())
        {
            SORT_BUTTON.draw(matrices, this, 134, 236);
        }

        if(QUICK_STACK_BUTTON.inButton(this, mouseX, mouseY, 76))
        {
            QUICK_STACK_BUTTON.draw(matrices, this, 148, 222);
        }
        else
        {
            QUICK_STACK_BUTTON.draw(matrices, this, 148, 208);
        }

        if(TRANSFER_TO_BACKPACK_BUTTON.inButton(this, mouseX, mouseY, 87))
        {
            TRANSFER_TO_BACKPACK_BUTTON.draw(matrices, this, 159, 222);
        }
        else
        {
            TRANSFER_TO_BACKPACK_BUTTON.draw(matrices, this, 159, 208);
        }

        if(TRANSFER_TO_PLAYER_BUTTON.inButton(this, mouseX, mouseY, 98))
        {
            TRANSFER_TO_PLAYER_BUTTON.draw(matrices, this, 170, 222);
        }
        else
        {
            TRANSFER_TO_PLAYER_BUTTON.draw(matrices, this, 170, 208);
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
            if(!ComponentUtils.isWearingBackpack(playerInventory.player) && this.screenID == Reference.ITEM_SCREEN_ID)
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

            if(ComponentUtils.isWearingBackpack(playerInventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
            {
                if(UNEQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    UNEQUIP_BUTTON.draw(matrices,this, 58, 227);
                }
                else
                {
                    UNEQUIP_BUTTON.draw(matrices,this, 39, 227);
                }

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
            }
        }
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType)
    {
        super.onMouseClick(slot, slotId, button, actionType);

        if((slotId >= 10 && slotId <= 48) && inventory.getSlotManager().isActive())
        {
            inventory.getSlotManager().setUnsortableSlot(slotId - 10);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(inventory.getSlotManager().isActive() && !SORT_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 65))
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if(SORT_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 65))
        {
            //Turns slot checking on server
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(screenID).writeByte(InventorySorter.SORT_BACKPACK).writeBoolean(BackpackUtils.isShiftPressed());

            ClientPlayNetworking.send(ModNetwork.SORTER_ID, buf);

            //Turns slot checking on client
            if(BackpackUtils.isShiftPressed())
            {
                ClientPlayNetworking.send(ModNetwork.SLOT_ID, PacketByteBufs.copy(PacketByteBufs.create().writeByte(inventory.getScreenID()).writeBoolean(inventory.getSlotManager().isActive())).writeIntArray(inventory.getSlotManager().getUnsortableSlots().stream().mapToInt(i -> i).toArray()));
                inventory.getSlotManager().setActive(!inventory.getSlotManager().isActive());
            }

            playUIClickSound();
            return true;
        }

        if(QUICK_STACK_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 76))
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(screenID).writeByte(InventorySorter.QUICK_STACK).writeBoolean(BackpackUtils.isShiftPressed());

            ClientPlayNetworking.send(ModNetwork.SORTER_ID, buf);
            playUIClickSound();
            return true;
        }

        if(TRANSFER_TO_BACKPACK_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 87))
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(screenID).writeByte(InventorySorter.TRANSFER_TO_BACKPACK).writeBoolean(BackpackUtils.isShiftPressed());

            ClientPlayNetworking.send(ModNetwork.SORTER_ID, buf);
            playUIClickSound();
            return true;
        }

        if(TRANSFER_TO_PLAYER_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 98))
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(screenID).writeByte(InventorySorter.TRANSFER_TO_PLAYER).writeBoolean(BackpackUtils.isShiftPressed());

            ClientPlayNetworking.send(ModNetwork.SORTER_ID, buf);
            playUIClickSound();
            return true;
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
        playerInventory.player.world.playSound(playerInventory.player, playerInventory.player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0F, 1.0F);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(KeybindHandler.OPEN_INVENTORY.matchesKey(keyCode, scanCode))
        {
            ClientPlayerEntity playerEntity = this.client.player;

            if(playerEntity != null)
            {
                this.onClose();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}