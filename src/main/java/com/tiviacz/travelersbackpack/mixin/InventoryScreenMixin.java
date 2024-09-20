package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler>
{
    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text)
    {
        super(screenHandler, playerInventory, text);
    }

    @Inject(at = @At(value = "TAIL"), method = "render")
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci)
    {
        if(!TravelersBackpackConfig.getConfig().client.showBackpackIconInInventory) return;

        PlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;

        if(ComponentUtils.isWearingBackpack(player))
        {
            if(TravelersBackpack.enableTrinkets()) return;

            context.drawItem(ComponentUtils.getWearingBackpack(player), this.x + 77, this.y + 61 - 18);
            //guiGraphics.renderItem(AttachmentUtils.getWearingBackpack(player), screen.getGuiLeft() + 59, screen.getGuiTop() + 7);
            //guiGraphics.renderItem(AttachmentUtils.getWearingBackpack(player), screen.getGuiLeft() - 8 - 9, screen.getGuiTop() + 8 + 18);
            //guiGraphics.blit(TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK, screen.getGuiLeft() - 8 - 10, screen.getGuiTop() + 7 + 18, 213, 0, 18, 18);

            //if(event.getMouseX() >= screen.getGuiLeft() - 17 && event.getMouseX() < screen.getGuiLeft() - 1 && event.getMouseY() >= screen.getGuiTop() + 8 + 18 && event.getMouseY() < screen.getGuiTop() + 8 + 18 + 16)
            if(mouseX >= this.x + 77 && mouseX < this.x + 77 + 16 && mouseY >= this.y + 61 - 18 && mouseY < this.y + 61 - 18 + 16)
            {
                //AbstractContainerScreen.renderSlotHighlight(guiGraphics, screen.getGuiLeft() - 8 - 9, screen.getGuiTop() + 8 + 18, -1000);
                AbstractInventoryScreen.drawSlotHighlight(context, this.x + 77, this.y + 61 - 18, -1000);
                String button = KeybindHandler.OPEN_BACKPACK.getBoundKeyLocalizedText().getString();
                List<Text> components = Arrays.asList(Text.translatable("screen.travelersbackpack.open_inventory", button), Text.translatable("screen.travelersbackpack.hide_icon"));
                context.drawTooltip(this.textRenderer, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "mouseClicked")
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir)
    {
        if(!TravelersBackpackConfig.getConfig().client.showBackpackIconInInventory || TravelersBackpack.enableTrinkets()) return;

        PlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;

        if(ComponentUtils.isWearingBackpack(player))
        {
            if(TravelersBackpack.enableTrinkets()) return;

            if(mouseX >= this.x + 77 && mouseX < this.x + 77 + 16 && mouseY >= this.y + 61 - 18 && mouseY < this.y + 61 - 18 + 16)
            {
                if(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) && button == GLFW.GLFW_MOUSE_BUTTON_1)
                {
                    player.sendMessage(Text.translatable("screen.travelersbackpack.hidden_icon_info"), false);
                    TravelersBackpackConfig.getConfig().client.showBackpackIconInInventory = false;
                    TravelersBackpackConfig.saveConfig();
                }
            }
        }
    }
}
