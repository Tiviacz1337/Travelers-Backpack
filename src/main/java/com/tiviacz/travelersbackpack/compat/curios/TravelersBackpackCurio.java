package com.tiviacz.travelersbackpack.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackWearableModel;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;

public record TravelersBackpackCurio(ItemStack stack) implements ICurio
{
    public static void registerCurio(RegisterCapabilitiesEvent event)
    {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> event.registerItem(CuriosCapability.ITEM, (stack, context) -> new TravelersBackpackCurio(stack), holder::get));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerCurioRenderer()
    {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> CuriosRendererRegistry.register(holder.get(), Renderer::new));
    }

    @Override
    public ItemStack getStack()
    {
        return this.stack;
    }

    @Override
    public boolean canEquip(SlotContext context)
    {
        return TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get();
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack)
    {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get()) return;

        if(slotContext.entity() instanceof Player player)
        {
            if(player.containerMenu instanceof TravelersBackpackItemMenu) return;

            if(!player.level().isClientSide)
            {
                AttachmentUtils.getAttachment(player).ifPresent(data ->
                {
                    data.setWearable(stack);
                    data.setContents(stack);

                    data.synchronise();
                    data.synchroniseToOthers(player);
                });
            }
        }
    }

    @Override
    public void onEquipFromUse(SlotContext slotContext)
    {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get()) return;

        if(slotContext.entity() instanceof Player player)
        {
            if(!player.level().isClientSide)
            {
                AttachmentUtils.getAttachment(player).ifPresent(data ->
                {
                    data.setWearable(stack);
                    data.setContents(stack);

                    data.synchronise();
                    data.synchroniseToOthers(player);
                });
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack)
    {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get()) return;

        if(slotContext.entity() instanceof Player player)
        {
            if(player.containerMenu instanceof TravelersBackpackItemMenu) return;

            if(!player.level().isClientSide)
            {
                AttachmentUtils.getAttachment(player).ifPresent(data ->
                {
                    data.removeWearable();

                    data.synchronise();
                    data.synchroniseToOthers(player);
                });
            }
        }
    }

    @Override
    public void curioTick(SlotContext slotContext)
    {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.curiosIntegration.get()) return;

        if(slotContext.entity() instanceof Player player)
        {
            if(player.containerMenu instanceof TravelersBackpackItemMenu || !AttachmentUtils.isWearingBackpack(player)) return;

            TravelersBackpackContainer container = AttachmentUtils.getBackpackInv(player);

            if(!ItemStack.isSameItemSameComponents(container.getItemStack(), getStack()))
            {
                getStack().applyComponents(container.getItemStack().getComponentsPatch());
                //this.onEquip(stack, slot, entity);
            }
        }
    }

    @Nonnull
    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit)
    {
        return DropRule.DEFAULT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Renderer implements ICurioRenderer
    {
        public TravelersBackpackWearableModel model;

        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
        {
            if(slotContext.entity() instanceof Player player && renderLayerParent.getModel() instanceof HumanoidModel humanoidModel)
            {
                TravelersBackpackContainer container = AttachmentUtils.getBackpackInv(player);
                if(container == null) return;
                model = new TravelersBackpackWearableModel(player, renderTypeBuffer, TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true).bakeRoot());
                boolean flag = container.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK.get() || container.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK.get();

                if(container.getItemStack().isEmpty() || !(container.getItemStack().getItem() instanceof TravelersBackpackItem travelersBackpackItem)) return;

                ResourceLocation loc = travelersBackpackItem.getBackpackTexture();

                boolean isColorable = false;
                boolean isCustomSleepingBag = false;

                if(container.getItemStack().has(DataComponents.DYED_COLOR) && container.getItemStack().getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                {
                    isColorable = true;
                    loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed.png");
                }

                if(container.getItemStack().has(ModDataComponents.SLEEPING_BAG_COLOR))
                {
                    isCustomSleepingBag = true;
                }

                VertexConsumer vertexConsumer = renderTypeBuffer.getBuffer(flag ? RenderType.entityTranslucentCull(loc) : RenderType.entitySolid(loc));

                matrixStack.pushPose();

                if(player.isCrouching())
                {
                    matrixStack.translate(0D, -0.155D, 0.025D);
                }

                humanoidModel.copyPropertiesTo(model);
                model.setupAngles(humanoidModel);

                matrixStack.translate(0, 0.175, 0.325);
                matrixStack.scale(0.85F, 0.85F, 0.85F);

                if(isColorable)
                {
                    model.renderToBuffer(matrixStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.opaque(container.getItemStack().get(DataComponents.DYED_COLOR).rgb()));

                    loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
                    vertexConsumer = renderTypeBuffer.getBuffer(RenderType.entityCutout(loc));
                }

                model.renderToBuffer(matrixStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);

                if(isCustomSleepingBag)
                {
                    loc = ResourceUtils.getSleepingBagTexture(container.getSleepingBagColor());
                }
                else
                {
                    loc = ResourceUtils.getDefaultSleepingBagTexture();
                }

                vertexConsumer = renderTypeBuffer.getBuffer(RenderType.entityCutout(loc));
                model.sleepingBag.render(matrixStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);

                matrixStack.popPose();
            }
        }
    }
}