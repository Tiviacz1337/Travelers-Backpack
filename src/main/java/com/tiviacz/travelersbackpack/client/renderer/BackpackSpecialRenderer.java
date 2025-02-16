package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialBlockRendererRegistry;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record BackpackSpecialRenderer(ItemStack stack) implements SpecialModelRenderer<ItemStack> {

    public static void register() {
        SpecialModelRenderers.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"), BackpackSpecialRenderer.Unbaked.MAP_CODEC);

        BuiltInRegistries.BLOCK.stream()
                .filter(holder -> holder instanceof TravelersBackpackBlock)
                .forEach(holder -> SpecialBlockRendererRegistry.register(holder, new BackpackSpecialRenderer.Unbaked(holder.asItem())));
    }

    @Override
    public void render(@Nullable ItemStack patterns, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean hasFoilType) {
        BackpackBlockEntityRenderer.renderByItem(patterns, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public @Nullable ItemStack extractArgument(ItemStack stack) {
        return stack.getItem() instanceof TravelersBackpackItem ? stack : ModItems.STANDARD_TRAVELERS_BACKPACK.asItem().getDefaultInstance();
    }

    // The model to read the json from
    public static record Unbaked(Item defaultStack) implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ItemStack.SIMPLE_ITEM_CODEC.fieldOf("default").forGetter((Unbaked unbaked) -> unbaked.defaultStack.getDefaultInstance())
                ).apply(instance, (itemStack) -> new BackpackSpecialRenderer.Unbaked(itemStack.getItem()))
        );

        // Create the special model renderer, or null if it fails
        @Nullable
        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet modelSet) {
            return new BackpackSpecialRenderer(defaultStack.getDefaultInstance());
        }

        @Override
        public MapCodec<BackpackSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}

