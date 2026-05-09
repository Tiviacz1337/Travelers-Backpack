package com.tiviacz.travelersbackpack.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.component.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BackpackDynamicModel implements UnbakedModel {
    private final Map<ModelParts, UnbakedModel> modelParts;

    private BackpackDynamicModel(Map<ModelParts, UnbakedModel> modelParts) {
        this.modelParts = modelParts;
    }

    public BlockStateModel bakeBlockStateModel(ModelBaker baker, ResolvedModel resolvedModel, ModelState modelState) {
        ImmutableMap.Builder<ModelParts, QuadCollection> builder = ImmutableMap.builder();
        modelParts.forEach((part, model) -> builder.put(part, baker.getModel(model.parent()).getTopGeometry().bake(getTextureSlots(baker, model, resolvedModel), baker, modelState, resolvedModel, ContextMap.EMPTY)));
        return new BlockStateModel(builder.build(), modelState, resolvedModel.resolveParticleMaterial(getTextureSlots(baker, modelParts.get(ModelParts.BASE), resolvedModel), baker));
    }

    private TextureSlots getTextureSlots(ModelBaker baker, UnbakedModel partModel, ModelDebugName debugName) {
        TextureSlots.Resolver resolver = new TextureSlots.Resolver();
        resolver.addLast(partModel.textureSlots());
        Identifier parent = partModel.parent();
        if(parent != null) {
            ResolvedModel resolvedParent = baker.getModel(parent);
            while(resolvedParent != null) {
                resolver.addLast(resolvedParent.wrapped().textureSlots());
                resolvedParent = resolvedParent.parent();
            }
        }
        return resolver.resolve(debugName);
    }

    @Override
    public void resolveDependencies(Resolver resolver) {
        modelParts.values().forEach(model -> {
            Identifier parent = model.parent();
            if(parent != null) {
                resolver.markDependency(parent);
            }
            model.resolveDependencies(resolver);
        });
    }

    public static final class BlockStateModel implements DynamicBlockStateModel {
        private final Map<ModelParts, QuadCollection> models;
        private final ModelState modelTransform;
        private final Material.Baked particleMaterial;

        public boolean isDyed;
        public boolean isSleepingBagDeployed;
        public int sleepingBagColor;

        public RenderInfo renderInfo;
        public Block block;

        public BlockStateModel(Map<ModelParts, QuadCollection> models, ModelState modelTransform, Material.Baked particleMaterial) {
            this.models = models;
            this.modelTransform = modelTransform;
            this.particleMaterial = particleMaterial;
        }

        @Override
        public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
            ModelData extraData = level.getModelData(pos);
            if(state != null) {
                block = state.getBlock();
                isDyed = extraData.has(BackpackBlockEntity.DYE_COLOR);
                renderInfo = extraData.has(BackpackBlockEntity.RENDER_INFO) ? extraData.get(BackpackBlockEntity.RENDER_INFO) : RenderInfo.EMPTY;
                sleepingBagColor = extraData.has(BackpackBlockEntity.SLEEPING_BAG_COLOR) ? extraData.get(BackpackBlockEntity.SLEEPING_BAG_COLOR) : DyeColor.RED.getId();
                isSleepingBagDeployed = extraData.has(BackpackBlockEntity.SLEEPING_BAG_DEPLOYED) ? extraData.get(BackpackBlockEntity.SLEEPING_BAG_DEPLOYED) : false;
            }
            collectParts(parts);
        }

        private void collectParts(List<BlockStateModelPart> parts) {
            QuadCollection.Builder builder = new QuadCollection.Builder();
            addBaseBackpack(builder);
            addTanks(builder);
            addSleepingBag(builder);
            addExtras(builder);
            parts.add(new SimpleModelWrapper(builder.build(), true, this.particleMaterial));
        }

        public void addBaseBackpack(QuadCollection.Builder builder) {
            if(isDyed && block == ModBlocks.STANDARD_TRAVELERS_BACKPACK.get()) {
                builder.addAll(models.get(ModelParts.BASE_DYED));
                builder.addAll(models.get(ModelParts.EXTRAS));
            } else {
                builder.addAll(models.get(ModelParts.BASE));
            }
        }

        @Override
        public Material.Baked particleMaterial() {
            return this.particleMaterial;
        }

        @Override
        public int materialFlags() {
            return models.values().stream().mapToInt(QuadCollection::materialFlags).reduce(0, (a, b) -> a | b);
        }

        public List<BakedQuad> getQuads() {
            List<BlockStateModelPart> parts = new ArrayList<>();
            collectParts(parts);

            List<BakedQuad> bakedQuads = new ArrayList<>();

            for(BlockStateModelPart part : parts) {
                for(Direction dir : Direction.values()) {
                    bakedQuads.addAll(part.getQuads(dir));
                }
                bakedQuads.addAll(part.getQuads(null));
            }

            return bakedQuads;
        }

        private void addFluids(QuadCollection.Builder builder, RenderInfo renderInfo) {
            if(renderInfo != null && renderInfo.hasTanks()) {
                if(!renderInfo.getLeftFluidStack().isEmpty()) {
                    addFluid(builder, renderInfo.getLeftFluidStack(), (float)renderInfo.getLeftFluidStack().getAmount() / renderInfo.getCapacity(), 1.8F / 16D);
                }
                if(!renderInfo.getRightFluidStack().isEmpty()) {
                    addFluid(builder, renderInfo.getRightFluidStack(), (float)renderInfo.getRightFluidStack().getAmount() / renderInfo.getCapacity(), 12.7F / 16D);
                }
            }
        }

        //Rebake sleeping bag to change sprite dynamically
        private void addSleepingBag(QuadCollection.Builder builder) {
            if(isSleepingBagDeployed || sleepingBagColor == -1) {
                return;
            }

            builder.addAll(models.get(ModelParts.SLEEPING_BAG_EXTRAS));

            TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/bag/" + DyeColor.byId(sleepingBagColor).getName().toLowerCase(Locale.ENGLISH) + "_sleeping_bag"));
            rebakeSleepingBag(builder, sprite);
        }

        private void rebakeSleepingBag(QuadCollection.Builder builder, TextureAtlasSprite sprite) {
            models.get(ModelParts.SLEEPING_BAG).getAll().forEach(quad -> {
                TextureAtlasSprite oldSprite = quad.materialInfo().sprite();

                long[] oldUVs = {quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3()};
                long[] newUVs = new long[4];

                for(int i = 0; i < 4; i++) {
                    // Unpack using UVPair format: U in upper 32 bits, V in lower 32 bits
                    float oldU = UVPair.unpackU(oldUVs[i]);
                    float oldV = UVPair.unpackV(oldUVs[i]);

                    // Convert from old sprite's atlas space to normalized 0-1 range
                    float uNormalized = (oldU - oldSprite.getU0()) / (oldSprite.getU1() - oldSprite.getU0());
                    float vNormalized = (oldV - oldSprite.getV0()) / (oldSprite.getV1() - oldSprite.getV0());

                    // Convert to new sprite's atlas space
                    float newU = sprite.getU(uNormalized);
                    float newV = sprite.getV(vNormalized);

                    // Pack using UVPair format
                    newUVs[i] = UVPair.pack(newU, newV);
                }

                BakedQuad.MaterialInfo newInfo = new BakedQuad.MaterialInfo(
                        sprite,
                        quad.materialInfo().layer(),
                        quad.materialInfo().itemRenderType(),
                        quad.materialInfo().tintIndex(),
                        quad.materialInfo().shade(),
                        quad.materialInfo().lightEmission(),
                        quad.materialInfo().ambientOcclusion()
                );

                BakedQuad newQuad = new BakedQuad(
                        quad.position0(),
                        quad.position1(),
                        quad.position2(),
                        quad.position3(),
                        newUVs[0],
                        newUVs[1],
                        newUVs[2],
                        newUVs[3],
                        quad.direction(),
                        newInfo
                );

                builder.addUnculledFace(newQuad);
            });
        }

        private void addExtras(QuadCollection.Builder builder) {
            if(block == ModBlocks.FOX_TRAVELERS_BACKPACK.get()) {
                builder.addAll(models.get(ModelParts.FOX_NOSE));
            }
            if(block == ModBlocks.WARDEN_TRAVELERS_BACKPACK.get()) {
                builder.addAll(models.get(ModelParts.WARDEN_HORNS));
            }
            if(block == ModBlocks.WOLF_TRAVELERS_BACKPACK.get()) {
                builder.addAll(models.get(ModelParts.WOLF_NOSE));
            }
            if(block == ModBlocks.OCELOT_TRAVELERS_BACKPACK.get()) {
                builder.addAll(models.get(ModelParts.OCELOT_NOSE));
            }
            if(block == ModBlocks.PIG_TRAVELERS_BACKPACK.get() || block == ModBlocks.HORSE_TRAVELERS_BACKPACK.get()) {
                builder.addAll(models.get(ModelParts.PIG_NOSE));
            }
            if(block == ModBlocks.VILLAGER_TRAVELERS_BACKPACK.get() || block == ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK.get()) {
                builder.addAll(models.get(ModelParts.VILLAGER_NOSE));
            }
        }

        private void addTanks(QuadCollection.Builder builder) {
            if(renderInfo == null || renderInfo.hasTanks()) {
                builder.addAll(models.get(ModelParts.TANKS));
                addFluids(builder, renderInfo);
            }
        }

        private void addFluid(QuadCollection.Builder builder, FluidStack fluidStack, float ratio, double xMin) {
            if(fluidStack.isEmpty()) {
                return;
            }

            double yMin = 0.8D / 16D;
            double yMax = yMin + (ratio * 6.2D) / 16D;
            AABB bounds = new AABB(xMin, yMin, 6.3D / 16D, xMin + 1.5D / 16D, yMax, 7.8D / 16D);


            FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidStack.getFluid().defaultFluidState());
            TextureAtlasSprite still = fluidModel.stillMaterial().sprite();
            int color = getTintColor(fluidStack) | -16777216;
            float x1 = 0F;
            float x2 = 3F;
            float y1 = 0F;
            float y2 = ratio * 12F;
            float z1 = 0F;
            float z2 = 3F;

            builder.addUnculledFace(createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.minZ), getVector(bounds.minX, bounds.maxY, bounds.maxZ), getVector(bounds.maxX, bounds.maxY, bounds.maxZ), getVector(bounds.maxX, bounds.maxY, bounds.minZ)), still, Direction.UP, false, color, x1, x2, z1, z2));
            builder.addUnculledFace(createQuad(List.of(getVector(bounds.maxX, bounds.maxY, bounds.minZ), getVector(bounds.maxX, bounds.minY, bounds.minZ), getVector(bounds.minX, bounds.minY, bounds.minZ), getVector(bounds.minX, bounds.maxY, bounds.minZ)), still, Direction.NORTH, false, color, x1, x2, y1, y2));
            builder.addUnculledFace(createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.maxZ), getVector(bounds.minX, bounds.minY, bounds.maxZ), getVector(bounds.maxX, bounds.minY, bounds.maxZ), getVector(bounds.maxX, bounds.maxY, bounds.maxZ)), still, Direction.SOUTH, false, color, x1, x2, y1, y2));
            builder.addUnculledFace(createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.minZ), getVector(bounds.minX, bounds.minY, bounds.minZ), getVector(bounds.minX, bounds.minY, bounds.maxZ), getVector(bounds.minX, bounds.maxY, bounds.maxZ)), still, Direction.WEST, false, color, z1, z2, y1, y2));
            builder.addUnculledFace(createQuad(List.of(getVector(bounds.maxX, bounds.maxY, bounds.maxZ), getVector(bounds.maxX, bounds.minY, bounds.maxZ), getVector(bounds.maxX, bounds.minY, bounds.minZ), getVector(bounds.maxX, bounds.maxY, bounds.minZ)), still, Direction.EAST, false, color, z1, z2, y1, y2));
        }

        private BakedQuad createQuad(List<Vector3f> vectors, TextureAtlasSprite sprite, Direction face, boolean hasAmbientOcclusion, int color, float u1x, float u2x, float v1x, float v2x) {
            QuadBakingVertexConsumer quadBaker = new QuadBakingVertexConsumer();
            quadBaker.setSprite(sprite, ChunkSectionLayer.CUTOUT, Sheets.cutoutBlockItemSheet());
            Vec3i dirVec = face.getUnitVec3i();
            quadBaker.setDirection(face);
            quadBaker.setTintIndex(-1);
            quadBaker.setShade(true);
            quadBaker.setAmbientOcclusion(hasAmbientOcclusion);

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

        private static int getTintColor(FluidStack fluidStack) {
            FluidState fluidState = fluidStack.getFluid().defaultFluidState();
            FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);
            return fluidModel.tintSource() instanceof FluidTintSource fluidTintSource ? fluidTintSource.colorAsStack(fluidStack) : fluidModel.tintSource() != null ? fluidModel.tintSource().color(fluidState.createLegacyBlock()) : -1;
        }

        private Vector3f getVector(double x, double y, double z) {
            Vector3f ret = new Vector3f((float)x, (float)y, (float)z);
            rotate(ret, modelTransform.transformation().getMatrix());
            return ret;
        }

        private void rotate(Vector3f posIn, Matrix4fc transform) {
            Vector3f originIn = new Vector3f(0.5f, 0.5f, 0.5f);
            Vector4f vector4f = transform.transform(new Vector4f(posIn.x() - originIn.x(), posIn.y() - originIn.y(), posIn.z() - originIn.z(), 1.0F));
            posIn.set(vector4f.x() + originIn.x(), vector4f.y() + originIn.y(), vector4f.z() + originIn.z());
        }
    }

    public record UnbakedBlockStateModel(Variant variant) implements CustomUnbakedBlockStateModel {
        public static final MapCodec<UnbakedBlockStateModel> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(Variant.MAP_CODEC.forGetter(UnbakedBlockStateModel::variant)).apply(instance, UnbakedBlockStateModel::new));
        public static final Identifier ID = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_loader");

        @Override
        public BlockStateModel bake(ModelBaker modelBaker) {
            ResolvedModel resolvedModel = modelBaker.getModel(variant.modelLocation());
            if(resolvedModel.wrapped() instanceof BackpackDynamicModel model) {
                return model.bakeBlockStateModel(modelBaker, resolvedModel, variant.modelState().asModelState());
            }
            throw new IllegalStateException("Expected BackpackDynamicModel, instead received " + resolvedModel.wrapped().getClass().getName());
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(variant.modelLocation());
        }

        @Override
        public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
            return CODEC;
        }
    }

    public static final class Loader implements UnbakedModelLoader<BackpackDynamicModel> {
        public static final Loader INSTANCE = new Loader();

        @Override
        public BackpackDynamicModel read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
            ImmutableMap.Builder<ModelParts, UnbakedModel> builder = ImmutableMap.builder();
            TextureSlots.Data.Builder texturesBuilder = new TextureSlots.Data.Builder();
            if(modelContents.has("backpackTexture")) {
                Identifier backpackTexture = Identifier.tryParse(modelContents.get("backpackTexture").getAsString());
                if(backpackTexture != null) {
                    texturesBuilder.addTexture("0", new Material(backpackTexture));
                }
            }
            if(modelContents.has("particle")) {
                Identifier particleTexture = Identifier.tryParse(modelContents.get("particle").getAsString());
                if(particleTexture != null) {
                    texturesBuilder.addTexture("particle", new Material(particleTexture));
                }
            }
            texturesBuilder.addTexture("missing", new Material(MissingTextureAtlasSprite.getLocation()));
            for(ModelParts part : ModelParts.values()) {
                addPartModel(builder, part, texturesBuilder.build());
            }
            return new BackpackDynamicModel(builder.build());
        }

        private void addPartModel(ImmutableMap.Builder<ModelParts, UnbakedModel> builder, ModelParts modelPart, TextureSlots.Data textures) {
            builder.put(modelPart, new PartModel(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_" + modelPart.name().toLowerCase(Locale.ENGLISH)), textures));
        }
    }

    private record PartModel(Identifier parent, TextureSlots.Data textures) implements UnbakedModel {
        @Override
        public Boolean ambientOcclusion() {
            return true;
        }

        @Override
        public ItemTransforms transforms() {
            return ItemTransforms.NO_TRANSFORMS;
        }

        @Override
        public TextureSlots.Data textureSlots() {
            return textures;
        }
    }

    private enum ModelParts {
        BASE,
        BASE_DYED,
        EXTRAS,
        TANKS,
        SLEEPING_BAG,
        SLEEPING_BAG_EXTRAS,
        //Noses, Extras
        FOX_NOSE,
        OCELOT_NOSE,
        WOLF_NOSE,
        VILLAGER_NOSE,
        PIG_NOSE,
        WARDEN_HORNS
    }
}