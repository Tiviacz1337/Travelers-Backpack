package com.tiviacz.travelersbackpack.client.model;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class BackpackItemModel implements ItemModel {
    public static final Vector3f DEFAULT_ROTATION = new Vector3f(0.0F, 0.0F, 0.0F);
    private static final ItemTransforms ITEM_TRANSFORMS = createItemTransforms();

    private static ItemTransforms createItemTransforms() {
        return new ItemTransforms(
                new ItemTransform(
                        new Vector3f(60, -180, 0),
                        new Vector3f(0, 1.5f / 16f, 0.5f / 16f),
                        new Vector3f(0.7f, 0.7f, 0.7f), DEFAULT_ROTATION
                ),
                new ItemTransform(
                        new Vector3f(60, -180, 0),
                        new Vector3f(0, 1.5f / 16f, 0.5f / 16f),
                        new Vector3f(0.7f, 0.7f, 0.7f), DEFAULT_ROTATION
                ),
                new ItemTransform(
                        new Vector3f(0, -90, 12.5f),
                        new Vector3f(1.13f / 16f, 6f / 16f, 2f / 16f),
                        new Vector3f(0.68f, 0.68f, 0.68f), DEFAULT_ROTATION
                ),
                new ItemTransform(
                        new Vector3f(0, -90, 12.5f),
                        new Vector3f(1.13f / 16f, 6f / 16f, 2f / 16f),
                        new Vector3f(0.68f, 0.68f, 0.68f), DEFAULT_ROTATION
                ),
                new ItemTransform(
                        new Vector3f(0, 180, 0),
                        new Vector3f(0, 14.5f / 16f, 0),
                        new Vector3f(1, 1, 1), DEFAULT_ROTATION
                ),
                new ItemTransform(
                        new Vector3f(30, -38, 0),
                        new Vector3f(-0.25f / 16f, 2.25f / 16f, 0),
                        new Vector3f(1, 1, 1), DEFAULT_ROTATION
                ),
                new ItemTransform(
                        new Vector3f(0, 0, 0),
                        new Vector3f(0, 2f / 16f, 0),
                        new Vector3f(0.5f, 0.5f, 0.5f), DEFAULT_ROTATION
                ),
                new ItemTransform(
                        new Vector3f(0, 180, 0),
                        new Vector3f(0, 2.25f / 16f, 0),
                        new Vector3f(1, 1, 1), DEFAULT_ROTATION
                ),
                ImmutableMap.of()
        );
    }

    private final BackpackDynamicModel.BlockStateModel baseModel;
    private final List<ItemTintSource> tintSources;
    private final Supplier<Vector3f[]> extents;

    public BackpackItemModel(BackpackDynamicModel.BlockStateModel baseModel, List<ItemTintSource> tintSources) {
        this.baseModel = baseModel;
        this.tintSources = tintSources;
        this.extents = Suppliers.memoize(() -> BlockModelWrapper.computeExtents(baseModel.getQuads()));
    }

    @Override
    public void update(ItemStackRenderState stackRenderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int seed) {
        stackRenderState.appendModelIdentityElement(this);

        ItemStackRenderState.LayerRenderState renderLayer = stackRenderState.newLayer();
        if(stack.hasFoil()) {
            renderLayer.setFoilType(ItemStackRenderState.FoilType.STANDARD);
            stackRenderState.appendModelIdentityElement(ItemStackRenderState.FoilType.STANDARD);
        }

        int k = this.tintSources.size();
        int[] aint = renderLayer.prepareTintLayers(k);

        for(int i = 0; i < k; i++) {
            int j = this.tintSources.get(i).calculate(stack, clientLevel, livingEntity);
            aint[i] = j;
            stackRenderState.appendModelIdentityElement(j);
        }

        setProperties(stack, stackRenderState);

        renderLayer.setExtents(extents);
        renderLayer.setUsesBlockLight(true);
        renderLayer.setParticleIcon(baseModel.particleIcon());
        renderLayer.setTransform(ITEM_TRANSFORMS.getTransform(displayContext));
        renderLayer.prepareQuadList().addAll(baseModel.getQuads());
        SpecialRenderer specialRenderer = new SpecialRenderer();
        specialRenderer.setModelRenderParameters(aint, baseModel.getQuads());

        renderLayer.setupSpecialModel(specialRenderer, specialRenderer.extractArgument(stack));
    }

    private void setProperties(ItemStack stack, ItemStackRenderState stackRenderState) {
        if(baseModel instanceof BackpackDynamicModel.BlockStateModel backpackModel) {
            backpackModel.isDyed = stack.has(DataComponents.DYED_COLOR);
            backpackModel.renderInfo = stack.get(ModDataComponents.RENDER_INFO);
            backpackModel.sleepingBagColor = stack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.RED.getId());
            backpackModel.isSleepingBagDeployed = false;
            backpackModel.block = Block.byItem(stack.getItem());

            if(backpackModel.renderInfo != null) {
                stackRenderState.appendModelIdentityElement("RenderInfo");
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.hasTanks());

                stackRenderState.appendModelIdentityElement("LeftTank");
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.getLeftFluidStack().getFluid());
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.getLeftFluidStack().getComponents());
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.getLeftFluidStack().getAmount());

                stackRenderState.appendModelIdentityElement("RightTank");
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.getRightFluidStack().getFluid());
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.getRightFluidStack().getComponents());
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.getRightFluidStack().getAmount());
            }

            if(backpackModel.sleepingBagColor != DyeColor.RED.getId()) {
                stackRenderState.appendModelIdentityElement("SleepingBagColor");
                stackRenderState.appendModelIdentityElement(backpackModel.sleepingBagColor);
            }
        }
    }

    public record Unbaked(ResourceLocation base, List<ItemTintSource> tintSources) implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                ResourceLocation.CODEC.fieldOf("base").forGetter(Unbaked::base),
                ItemTintSources.CODEC.listOf().optionalFieldOf("tintSources", List.of()).forGetter(Unbaked::tintSources)
        ).apply(builder, Unbaked::new));

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(BakingContext context) {
            ResolvedModel resolved = context.blockModelBaker().getModel(base);
            if(resolved.wrapped() instanceof BackpackDynamicModel baseModel) {
                return new BackpackItemModel(baseModel.bakeBlockStateModel(context.blockModelBaker(), resolved, BlockModelRotation.X0_Y0), tintSources);
            }
            throw new IllegalStateException("Expected BackpackDynamicModel, instead received " + resolved.getClass().getName());
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(base);
        }
    }

    public static class SpecialRenderer implements NoDataSpecialModelRenderer {
        private int[] tintLayers;
        private List<BakedQuad> baseModel;

        @Override
        public void render(ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int packedOverlay, boolean hasFoil) {
            ItemRenderer.renderItem(displayContext, poseStack, buffer, combinedLight, packedOverlay, tintLayers, baseModel, Sheets.translucentItemSheet(), hasFoil ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE);
        }

        public void setModelRenderParameters(int[] tintLayers, List<BakedQuad> baseModel) {
            this.tintLayers = tintLayers;
            this.baseModel = baseModel;
        }

        @Override
        public void getExtents(Set<Vector3f> set) {

        }
    }
}