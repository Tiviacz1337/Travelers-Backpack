package com.tiviacz.travelersbackpack.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;

public class BackpackDynamicModel implements IUnbakedGeometry<BackpackDynamicModel> {
    private final Map<ModelParts, UnbakedModel> modelParts;

    private BackpackDynamicModel(Map<ModelParts, UnbakedModel> modelParts) {
        this.modelParts = modelParts;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides) {
        ImmutableMap.Builder<ModelParts, BakedModel> builder = ImmutableMap.builder();
        modelParts.forEach((part, model) -> {
            BakedModel bakedModel = model.bake(baker, spriteGetter, modelTransform);
            if(bakedModel != null) {
                builder.put(part, bakedModel);
            }
        });
        return new BackpackBakedModel(builder.build(), modelTransform);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        modelParts.values().forEach(model -> model.resolveParents(modelGetter));
    }

    private static final class BackpackBakedModel implements IDynamicBakedModel {
        @Override
        public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
            if(state.getBlock() == ModBlocks.QUARTZ_TRAVELERS_BACKPACK.get() || state.getBlock() == ModBlocks.SNOW_TRAVELERS_BACKPACK.get()) {
                return ChunkRenderTypeSet.of(RenderType.translucent());
            }
            return ChunkRenderTypeSet.of(RenderType.cutout());
        }

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

        private final BackpackItemOverrideList overrideList = new BackpackItemOverrideList(this);
        private final Map<ModelParts, BakedModel> models;
        private final ModelState modelTransform;

        private boolean isDyed;
        private boolean isSleepingBagDeployed;
        private int sleepingBagColor;

        private RenderInfo renderInfo;
        private Block block;

        public BackpackBakedModel(Map<ModelParts, BakedModel> models, ModelState modelTransform) {
            this.models = models;
            this.modelTransform = modelTransform;
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
            List<BakedQuad> ret = new ArrayList<>();
            if(state != null) {
                block = state.getBlock();
                isDyed = extraData.has(BackpackBlockEntity.DYE_COLOR);
                renderInfo = extraData.has(BackpackBlockEntity.RENDER_INFO) ? extraData.get(BackpackBlockEntity.RENDER_INFO) : RenderInfo.EMPTY;
                sleepingBagColor = extraData.has(BackpackBlockEntity.SLEEPING_BAG_COLOR) ? extraData.get(BackpackBlockEntity.SLEEPING_BAG_COLOR) : DyeColor.RED.getId();
                isSleepingBagDeployed = extraData.has(BackpackBlockEntity.SLEEPING_BAG_DEPLOYED) ? extraData.get(BackpackBlockEntity.SLEEPING_BAG_DEPLOYED) : false;
            }

            if(isDyed && block == ModBlocks.STANDARD_TRAVELERS_BACKPACK.get()) {
                ret.addAll(models.get(ModelParts.BASE_DYED).getQuads(state, side, rand, extraData, renderType));
                ret.addAll(models.get(ModelParts.EXTRAS).getQuads(state, side, rand, extraData, renderType));
            } else {
                ret.addAll(models.get(ModelParts.BASE).getQuads(state, side, rand, extraData, renderType));
            }
            if(renderInfo == null || !renderInfo.isEmpty()) {
                addTanks(state, side, rand, extraData, ret, renderType);
            }
            if(!isSleepingBagDeployed) {
                addSleepingBag(ret, state, side, rand, extraData, renderType);
            }
            addExtras(ret, state, side, rand, extraData, renderType);

            return ret;
        }

        private void addFluids(List<BakedQuad> ret, RenderInfo renderInfo) {
            if(renderInfo != null && !renderInfo.isEmpty()) {
                if(!renderInfo.getLeftFluidStack().isEmpty()) {
                    addFluid(ret, renderInfo.getLeftFluidStack(), (float)renderInfo.getLeftFluidStack().getAmount() / renderInfo.getCapacity(), 1.8F / 16D);
                }
                if(!renderInfo.getRightFluidStack().isEmpty()) {
                    addFluid(ret, renderInfo.getRightFluidStack(), (float)renderInfo.getRightFluidStack().getAmount() / renderInfo.getCapacity(), 12.7F / 16D);
                }
            }
        }

        private void addSleepingBag(List<BakedQuad> ret, BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
            ret.addAll(models.get(ModelParts.SLEEPING_BAG).getQuads(state, side, rand, extraData, renderType));

            float minX = 2.6F / 16;
            float minY = 0.8F / 16;
            float minZ = 8.9F / 16;
            float maxX = 13.5F / 16;
            float maxY = 2.4F / 16;
            float maxZ = 10.5F / 16;

            AABB bounds = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/bag/" + DyeColor.byId(sleepingBagColor).getName().toLowerCase(Locale.ENGLISH) + "_sleeping_bag"));
            ret.add(createQuad(List.of(getVector(bounds.maxX, bounds.maxY, bounds.minZ), getVector(bounds.maxX, bounds.minY, bounds.minZ), getVector(bounds.minX, bounds.minY, bounds.minZ), getVector(bounds.minX, bounds.maxY, bounds.minZ)), sprite,
                    Direction.NORTH, true, 0xFFFFFFFF, 11.5F, 15.25F, 0.5F, 1F));
            ret.add(createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.maxZ), getVector(bounds.minX, bounds.minY, bounds.maxZ), getVector(bounds.maxX, bounds.minY, bounds.maxZ), getVector(bounds.maxX, bounds.maxY, bounds.maxZ)), sprite,
                    Direction.SOUTH, true, 0xFFFFFFFF, 8.25F, 12.25F, 0.5F, 1F));
            ret.add(createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.minZ), getVector(bounds.minX, bounds.minY, bounds.minZ), getVector(bounds.minX, bounds.minY, bounds.maxZ), getVector(bounds.minX, bounds.maxY, bounds.maxZ)), sprite,
                    Direction.WEST, true, 0xFFFFFFFF, 7.75F, 8.25F, 0.5F, 1F));
            ret.add(createQuad(List.of(getVector(bounds.maxX, bounds.maxY, bounds.maxZ), getVector(bounds.maxX, bounds.minY, bounds.maxZ), getVector(bounds.maxX, bounds.minY, bounds.minZ), getVector(bounds.maxX, bounds.maxY, bounds.minZ)), sprite,
                    Direction.EAST, true, 0xFFFFFFFF, 15.25F, 15.75F, 0.5F, 1F));
            ret.add(createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.minZ), getVector(bounds.minX, bounds.maxY, bounds.maxZ), getVector(bounds.maxX, bounds.maxY, bounds.maxZ), getVector(bounds.maxX, bounds.maxY, bounds.minZ)), sprite,
                    Direction.UP, true, 0xFFFFFFFF, 12F, 8.25F, 0.5F, 0F));
            ret.add(createQuad(List.of(getVector(bounds.maxX, bounds.minY, bounds.minZ), getVector(bounds.maxX, bounds.minY, bounds.maxZ), getVector(bounds.minX, bounds.minY, bounds.maxZ), getVector(bounds.minX, bounds.minY, bounds.minZ)), sprite,
                    Direction.DOWN, true, 0xFFFFFFFF, 15.25F, 11.5F, 0F, 0.5F));
        }

        private void addExtras(List<BakedQuad> ret, BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
            if(block == ModBlocks.FOX_TRAVELERS_BACKPACK.get()) {
                ret.addAll(models.get(ModelParts.FOX_NOSE).getQuads(state, side, rand, extraData, renderType));
            }
            if(block == ModBlocks.WARDEN_TRAVELERS_BACKPACK.get()) {
                ret.addAll(models.get(ModelParts.WARDEN_HORNS).getQuads(state, side, rand, extraData, renderType));
            }
            if(block == ModBlocks.WOLF_TRAVELERS_BACKPACK.get()) {
                ret.addAll(models.get(ModelParts.WOLF_NOSE).getQuads(state, side, rand, extraData, renderType));
            }
            if(block == ModBlocks.OCELOT_TRAVELERS_BACKPACK.get()) {
                ret.addAll(models.get(ModelParts.OCELOT_NOSE).getQuads(state, side, rand, extraData, renderType));
            }
            if(block == ModBlocks.PIG_TRAVELERS_BACKPACK.get() || block == ModBlocks.HORSE_TRAVELERS_BACKPACK.get()) {
                ret.addAll(models.get(ModelParts.PIG_NOSE).getQuads(state, side, rand, extraData, renderType));
            }
            if(block == ModBlocks.VILLAGER_TRAVELERS_BACKPACK.get() || block == ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK.get()) {
                ret.addAll(models.get(ModelParts.VILLAGER_NOSE).getQuads(state, side, rand, extraData, renderType));
            }
        }

        private void addTanks(BlockState state, Direction side, RandomSource rand, ModelData extraData, List<BakedQuad> ret, RenderType renderType) {
            ret.addAll(models.get(ModelParts.TANKS).getQuads(state, side, rand, extraData, renderType));
            addFluids(ret, renderInfo);
        }

        private void addFluid(List<BakedQuad> ret, FluidStack fluidStack, float ratio, double xMin) {
            if(fluidStack.isEmpty() || Mth.equal(ratio, 0F)) {
                return;
            }

            double yMin = 0.8 / 16d;
            double yMax = yMin + (ratio * 6.2) / 16d;
            AABB bounds = new AABB(xMin, yMin, 6.3 / 16d, xMin + 1.5 / 16d, yMax, 7.8 / 16d);

            IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            ResourceLocation stillTexture = renderProperties.getStillTexture(fluidStack);
            int color = renderProperties.getTintColor(fluidStack) | -16777216;
            TextureAtlasSprite still = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
            float bx1 = 0F;
            float bx2 = 3F;
            float by1 = 0F;
            float by2 = ratio * 12F;
            float bz1 = 0F;
            float bz2 = 3F;

            ret.add(createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.minZ), getVector(bounds.minX, bounds.maxY, bounds.maxZ), getVector(bounds.maxX, bounds.maxY, bounds.maxZ), getVector(bounds.maxX, bounds.maxY, bounds.minZ)), still, Direction.UP, false, color, bx1, 4, bz1, 4));
            ret.add(createQuad(List.of(getVector(bounds.maxX, bounds.maxY, bounds.minZ), getVector(bounds.maxX, bounds.minY, bounds.minZ), getVector(bounds.minX, bounds.minY, bounds.minZ), getVector(bounds.minX, bounds.maxY, bounds.minZ)), still, Direction.NORTH, false, color, bx1, bx2, by1, by2));
            ret.add(createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.maxZ), getVector(bounds.minX, bounds.minY, bounds.maxZ), getVector(bounds.maxX, bounds.minY, bounds.maxZ), getVector(bounds.maxX, bounds.maxY, bounds.maxZ)), still, Direction.SOUTH, false, color, bx1, bx2, by1, by2));
            ret.add(createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.minZ), getVector(bounds.minX, bounds.minY, bounds.minZ), getVector(bounds.minX, bounds.minY, bounds.maxZ), getVector(bounds.minX, bounds.maxY, bounds.maxZ)), still, Direction.WEST, false, color, bz1, bz2, by1, by2));
            ret.add(createQuad(List.of(getVector(bounds.maxX, bounds.maxY, bounds.maxZ), getVector(bounds.maxX, bounds.minY, bounds.maxZ), getVector(bounds.maxX, bounds.minY, bounds.minZ), getVector(bounds.maxX, bounds.maxY, bounds.minZ)), still, Direction.EAST, false, color, bz1, bz2, by1, by2));
        }

        @Override
        public boolean useAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean isGui3d() {
            return true;
        }

        @Override
        public boolean usesBlockLight() {
            return true;
        }

        @Override
        public boolean isCustomRenderer() {
            return true;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return models.get(ModelParts.BASE).getParticleIcon();
        }

        @Override
        public ItemOverrides getOverrides() {
            return overrideList;
        }

        @Override
        public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
            if(transformType == ItemDisplayContext.NONE) {
                return this;
            }
            ITEM_TRANSFORMS.getTransform(transformType).apply(applyLeftHandTransform, poseStack);
            return this;
        }

        @Override
        public ItemTransforms getTransforms() {
            return ITEM_TRANSFORMS;
        }

        private BakedQuad createQuad(List<Vector3f> vectors, TextureAtlasSprite sprite, Direction face, boolean hasAmbientOcclusion, int color, float u1x, float u2x, float v1x, float v2x) {
            QuadBakingVertexConsumer quadBaker = new QuadBakingVertexConsumer();
            quadBaker.setSprite(sprite);
            Vec3i dirVec = face.getNormal();
            quadBaker.setDirection(face);
            quadBaker.setTintIndex(-1);
            quadBaker.setShade(true);
            quadBaker.setHasAmbientOcclusion(hasAmbientOcclusion);

            u1x = u1x / 16F;
            u2x = u2x / 16F;
            v1x = v1x / 16F;
            v2x = v2x / 16F;

            float u1 = sprite.getU(u1x);
            float u2 = sprite.getU(u2x);
            float v1 = sprite.getV(v1x);
            float v2 = sprite.getV(v2x);

            quadBaker.addVertex(vectors.get(0).x(), vectors.get(0).y(), vectors.get(0).z()).setColor(color).setUv(u1, v1).setNormal(dirVec.getX(), dirVec.getY(), dirVec.getZ());
            quadBaker.addVertex(vectors.get(1).x(), vectors.get(1).y(), vectors.get(1).z()).setColor(color).setUv(u1, v2).setNormal(dirVec.getX(), dirVec.getY(), dirVec.getZ());
            quadBaker.addVertex(vectors.get(2).x(), vectors.get(2).y(), vectors.get(2).z()).setColor(color).setUv(u2, v2).setNormal(dirVec.getX(), dirVec.getY(), dirVec.getZ());
            quadBaker.addVertex(vectors.get(3).x(), vectors.get(3).y(), vectors.get(3).z()).setColor(color).setUv(u2, v1).setNormal(dirVec.getX(), dirVec.getY(), dirVec.getZ());
            return quadBaker.bakeQuad();
        }

        private Vector3f getVector(double x, double y, double z) {
            Vector3f ret = new Vector3f((float)x, (float)y, (float)z);
            rotate(ret, modelTransform.getRotation().getMatrix());
            return ret;
        }

        private void rotate(Vector3f posIn, Matrix4f transform) {
            Vector3f originIn = new Vector3f(0.5f, 0.5f, 0.5f);
            Vector4f vector4f = transform.transform(new Vector4f(posIn.x() - originIn.x(), posIn.y() - originIn.y(), posIn.z() - originIn.z(), 1.0F));
            posIn.set(vector4f.x() + originIn.x(), vector4f.y() + originIn.y(), vector4f.z() + originIn.z());
        }
    }

    private static class BackpackItemOverrideList extends ItemOverrides {
        private final BackpackDynamicModel.BackpackBakedModel backpackModel;

        public BackpackItemOverrideList(BackpackDynamicModel.BackpackBakedModel backpackModel) {
            this.backpackModel = backpackModel;
        }

        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, ClientLevel world, LivingEntity livingEntity, int seed) {
            backpackModel.isDyed = stack.has(DataComponents.DYED_COLOR);
            backpackModel.renderInfo = stack.get(ModDataComponents.RENDER_INFO);
            backpackModel.sleepingBagColor = stack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.RED.getId());
            backpackModel.isSleepingBagDeployed = false;
            backpackModel.block = Block.byItem(stack.getItem());
            return backpackModel;
        }
    }

    public static final class Loader implements IGeometryLoader<BackpackDynamicModel> {
        public static final Loader INSTANCE = new Loader();

        @Override
        public BackpackDynamicModel read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
            ImmutableMap.Builder<ModelParts, UnbakedModel> builder = ImmutableMap.builder();
            ImmutableMap.Builder<String, Either<Material, String>> texturesBuilder = ImmutableMap.builder();
            if(modelContents.has("backpackTexture")) {
                ResourceLocation backpackTexture = ResourceLocation.tryParse(modelContents.get("backpackTexture").getAsString());
                if(backpackTexture != null) {
                    texturesBuilder.put("0", Either.left(new Material(InventoryMenu.BLOCK_ATLAS, backpackTexture)));
                }
            }
            if(modelContents.has("particle")) {
                ResourceLocation particleTexture = ResourceLocation.tryParse(modelContents.get("particle").getAsString());
                if(particleTexture != null) {
                    texturesBuilder.put("particle", Either.left(new Material(InventoryMenu.BLOCK_ATLAS, particleTexture)));
                }
            }
            ImmutableMap<String, Either<Material, String>> textures = texturesBuilder.build();
            for(ModelParts part : ModelParts.values()) {
                addPartModel(builder, part, textures);
            }
            return new BackpackDynamicModel(builder.build());
        }

        private void addPartModel(ImmutableMap.Builder<ModelParts, UnbakedModel> builder, ModelParts modelPart, ImmutableMap<String, Either<Material, String>> textures) {
            builder.put(modelPart, new BlockModel(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_" + modelPart.name().toLowerCase(Locale.ENGLISH)), Collections.emptyList(), textures, true, null, ItemTransforms.NO_TRANSFORMS, Collections.emptyList()));
        }
    }

    private enum ModelParts {
        BASE,
        BASE_DYED,
        EXTRAS,
        TANKS,
        SLEEPING_BAG,
        //Noses, Extras
        FOX_NOSE,
        OCELOT_NOSE,
        WOLF_NOSE,
        VILLAGER_NOSE,
        PIG_NOSE,
        WARDEN_HORNS
    }
}