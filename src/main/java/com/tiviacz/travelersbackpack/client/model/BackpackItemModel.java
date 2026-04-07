package com.tiviacz.travelersbackpack.client.model;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;
import java.util.function.Consumer;

public class BackpackItemModel implements ItemModel {
    private static final ItemTransforms ITEM_TRANSFORMS = createItemTransforms();

    private static ItemTransforms createItemTransforms() {
        return new ItemTransforms(
                new ItemTransform(
                        new Vector3f(60, -180, 0),
                        new Vector3f(0, 1.5f / 16f, 0.5f / 16f),
                        new Vector3f(0.7f, 0.7f, 0.7f)
                ),
                new ItemTransform(
                        new Vector3f(60, -180, 0),
                        new Vector3f(0, 1.5f / 16f, 0.5f / 16f),
                        new Vector3f(0.7f, 0.7f, 0.7f)
                ),
                new ItemTransform(
                        new Vector3f(0, -90, 12.5f),
                        new Vector3f(1.13f / 16f, 6f / 16f, 2f / 16f),
                        new Vector3f(0.68f, 0.68f, 0.68f)
                ),
                new ItemTransform(
                        new Vector3f(0, -90, 12.5f),
                        new Vector3f(1.13f / 16f, 6f / 16f, 2f / 16f),
                        new Vector3f(0.68f, 0.68f, 0.68f)
                ),
                new ItemTransform(
                        new Vector3f(0, 180, 0),
                        new Vector3f(0, 14.5f / 16f, 0),
                        new Vector3f(1, 1, 1)
                ),
                new ItemTransform(
                        new Vector3f(30, -38, 0),
                        new Vector3f(-0.25f / 16f, 2.25f / 16f, 0),
                        new Vector3f(1, 1, 1)
                ),
                new ItemTransform(
                        new Vector3f(0, 0, 0),
                        new Vector3f(0, 2f / 16f, 0),
                        new Vector3f(0.5f, 0.5f, 0.5f)
                ),
                new ItemTransform(
                        new Vector3f(0, 180, 0),
                        new Vector3f(0, 2.25f / 16f, 0),
                        new Vector3f(1, 1, 1)
                ),
                new ItemTransform(
                        new Vector3f(0, 180, 0),
                        new Vector3f(0, 2.25f / 16f, 0),
                        new Vector3f(1, 1, 1)
                )
        );
    }

    private final BackpackDynamicModel.DynamicBlockStateModel baseModel;
    private final List<ItemTintSource> tintSources;
    private final Supplier<Vector3fc[]> extents;

    public BackpackItemModel(BackpackDynamicModel.DynamicBlockStateModel baseModel, List<ItemTintSource> tintSources) {
        this.baseModel = baseModel;
        this.tintSources = tintSources;
        this.extents = Suppliers.memoize(() -> CuboidItemModelWrapper.computeExtents(baseModel.getQuads()));
    }

    @Override
    public void update(ItemStackRenderState stackRenderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel clientLevel, @Nullable ItemOwner itemOwner, int seed) {
        stackRenderState.appendModelIdentityElement(this);

        final int[] tints = new int[this.tintSources.size()];
        LivingEntity livingEntity = itemOwner != null ? itemOwner.asLivingEntity() : null;
        for(int i = 0; i < tints.length; i++) {
            tints[i] = this.tintSources.get(i).calculate(stack, clientLevel, livingEntity);
            stackRenderState.appendModelIdentityElement(tints[i]);
        }

        ItemStackRenderState.LayerRenderState renderLayer = stackRenderState.newLayer();
        if(stack.hasFoil()) {
            renderLayer.setFoilType(ItemStackRenderState.FoilType.STANDARD);
            stackRenderState.appendModelIdentityElement(ItemStackRenderState.FoilType.STANDARD);
        }

        for(int tint : tints) {
            renderLayer.tintLayers().add(tint);
        }

        setProperties(stack, stackRenderState);

        renderLayer.setExtents(extents);
        renderLayer.setUsesBlockLight(true);
        List<BakedQuad> quads = baseModel.getQuads();
        renderLayer.setParticleMaterial(baseModel.particleMaterial());
        renderLayer.setItemTransform(ITEM_TRANSFORMS.getTransform(displayContext));
        renderLayer.prepareQuadList().addAll(quads);
        SpecialRenderer specialRenderer = new SpecialRenderer();
        specialRenderer.setModelRenderParameters(renderLayer.tintLayers() == null ? ItemStackRenderState.LayerRenderState.EMPTY_TINTS : renderLayer.tintLayers().toIntArray(), quads);

        renderLayer.setupSpecialModel(specialRenderer, specialRenderer.extractArgument(stack));
    }

    private void setProperties(ItemStack stack, ItemStackRenderState stackRenderState) {
        if(baseModel instanceof BackpackDynamicModel.DynamicBlockStateModel backpackModel) {
            backpackModel.isDyed = stack.has(DataComponents.DYED_COLOR);
            backpackModel.renderInfo = stack.get(ModDataComponents.RENDER_INFO);
            backpackModel.sleepingBagColor = stack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.RED.getId());
            backpackModel.isSleepingBagDeployed = false;
            backpackModel.block = Block.byItem(stack.getItem());

            if(backpackModel.renderInfo != null) {
                stackRenderState.appendModelIdentityElement("RenderInfo");
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.hasTanks());

                stackRenderState.appendModelIdentityElement("LeftTank");
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.getLeftFluidStack().fluidVariant().getFluid());
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.getLeftFluidStack().fluidVariant().getComponents());
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.getLeftFluidStack().getAmount());

                stackRenderState.appendModelIdentityElement("RightTank");
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.getRightFluidStack().fluidVariant().getFluid());
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.getRightFluidStack().fluidVariant().getComponents());
                stackRenderState.appendModelIdentityElement(backpackModel.renderInfo.getRightFluidStack().getAmount());
            }

            if(backpackModel.sleepingBagColor != DyeColor.RED.getId()) {
                stackRenderState.appendModelIdentityElement("SleepingBagColor");
                stackRenderState.appendModelIdentityElement(backpackModel.sleepingBagColor);
            }
        }
    }

    public record Unbaked(Identifier base, List<ItemTintSource> tintSources) implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                Identifier.CODEC.fieldOf("base").forGetter(Unbaked::base),
                ItemTintSources.CODEC.listOf().optionalFieldOf("tintSources", List.of()).forGetter(Unbaked::tintSources)
        ).apply(builder, Unbaked::new));

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(BakingContext context, Matrix4fc transformation) {
            ResolvedModel resolved = context.blockModelBaker().getModel(base);
            if(resolved.wrapped() instanceof BackpackDynamicModel baseModel) {
                return new BackpackItemModel(baseModel.bakeBlockStateModel(context.blockModelBaker(), resolved, BlockModelRotation.IDENTITY), tintSources);
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

        public void setModelRenderParameters(int[] tintLayers, List<BakedQuad> baseModel) {
            this.tintLayers = tintLayers;
            this.baseModel = baseModel;
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
            submitNodeCollector.submitItem(poseStack, ItemDisplayContext.NONE, lightCoords, overlayCoords, outlineColor, tintLayers, baseModel, hasFoil ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE);
        }

        @Override
        public void getExtents(Consumer<Vector3fc> consumer) {

        }
    }
}