package com.tiviacz.travelersbackpack.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackWearableModel;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.SimpleAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TravelersBackpackAccessory implements Accessory
{
    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference)
    {
        return TravelersBackpackConfig.SERVER.backpackSettings.accessoriesIntegration.get();
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference reference)
    {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.accessoriesIntegration.get()) return;

        if(reference.entity() instanceof Player player)
        {
            if(player.containerMenu instanceof TravelersBackpackItemMenu) return;

            if(!player.level().isClientSide)
            {
                AttachmentUtils.getAttachment(player).ifPresent(cap ->
                {
                    cap.setContents(stack);
                    cap.setWearable(stack);
                });
            }
            AttachmentUtils.synchronise(player);
            AttachmentUtils.synchroniseToOthers(player);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference reference)
    {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.accessoriesIntegration.get()) return;

        if(reference.entity() instanceof Player player)
        {
            if(player.containerMenu instanceof TravelersBackpackItemMenu) return;

            if(!player.level().isClientSide)
            {
                AttachmentUtils.getAttachment(player).ifPresent(ITravelersBackpack::removeWearable);
            }
            AttachmentUtils.synchronise(player);
            AttachmentUtils.synchroniseToOthers(player);
        }
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference)
    {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.accessoriesIntegration.get()) return;

        if(reference.entity() instanceof Player player)
        {
            if(player.containerMenu instanceof TravelersBackpackItemMenu || !AttachmentUtils.isWearingBackpack(player)) return;

            TravelersBackpackContainer container = AttachmentUtils.getBackpackInv(player);

            if(!ItemStack.isSameItemSameComponents(container.getItemStack(), stack))
            {
                stack.applyComponents(container.getItemStack().getComponentsPatch());
                //this.onEquip(stack, slot, entity);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientInit()
    {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> AccessoriesRendererRegistry.registerRenderer(holder.get(), Renderer::new));
    }

    public static void init()
    {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> AccessoriesAPI.registerAccessory(holder.get(), new TravelersBackpackAccessory()));
    }

    @OnlyIn(Dist.CLIENT)
    public static class Renderer implements SimpleAccessoryRenderer
    {
        public TravelersBackpackWearableModel model;

        @Override
        public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> entityModel, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
        {
            if(reference.entity() instanceof Player player && entityModel instanceof HumanoidModel humanoidModel)
            {
                TravelersBackpackContainer container = AttachmentUtils.getBackpackInv(player);
                if(container == null) return;
                model = new TravelersBackpackWearableModel(player, multiBufferSource, TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true).bakeRoot());
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

                VertexConsumer vertexConsumer = multiBufferSource.getBuffer(flag ? RenderType.entityTranslucentCull(loc) : RenderType.entitySolid(loc));

                matrices.pushPose();

                if(player.isCrouching())
                {
                    matrices.translate(0D, -0.155D, 0.025D);
                }

                humanoidModel.copyPropertiesTo(model);
                model.setupAngles(humanoidModel);

                matrices.translate(0, 0.175, 0.325);
                matrices.scale(0.85F, 0.85F, 0.85F);

                if(isColorable)
                {
                    model.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.opaque(container.getItemStack().get(DataComponents.DYED_COLOR).rgb()));

                    loc = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
                    vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutout(loc));
                }

                model.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);

                if(isCustomSleepingBag)
                {
                    loc = ResourceUtils.getSleepingBagTexture(container.getSleepingBagColor());
                }
                else
                {
                    loc = ResourceUtils.getDefaultSleepingBagTexture();
                }

                vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutout(loc));
                model.sleepingBag.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);

                matrices.popPose();
            }
        }

        @Override
        public <M extends LivingEntity> void align(ItemStack stack, SlotReference reference, EntityModel<M> model, PoseStack matrices) {}
    }
}