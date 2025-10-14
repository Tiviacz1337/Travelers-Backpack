package com.tiviacz.travelersbackpack.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.NeoForgeModelProperties;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

public class BackpackDynamicModel implements UnbakedModel {
    private final Map<ModelParts, UnbakedModel> modelParts;
    private final RenderTypeGroup renderTypeGroup;

    private BackpackDynamicModel(Map<ModelParts, UnbakedModel> modelParts, RenderTypeGroup renderTypeGroup) {
        this.modelParts = modelParts;
        this.renderTypeGroup = renderTypeGroup;
    }

    public BlockStateModel bakeBlockStateModel(ModelBaker baker, ResolvedModel resolvedModel, ModelState modelState) {
        ImmutableMap.Builder<ModelParts, QuadCollection> builder = ImmutableMap.builder();
        modelParts.forEach((part, model) -> builder.put(part, baker.getModel(model.parent()).getTopGeometry().bake(getTextureSlots(baker, model, resolvedModel), baker, modelState, resolvedModel, ContextMap.EMPTY)));
        return new BlockStateModel(builder.build(), modelState, resolvedModel.resolveParticleSprite(getTextureSlots(baker, modelParts.get(ModelParts.BASE), resolvedModel), baker), this.renderTypeGroup.block());
    }

    private TextureSlots getTextureSlots(ModelBaker baker, UnbakedModel partModel, ModelDebugName debugName) {
        TextureSlots.Resolver resolver = new TextureSlots.Resolver();
        resolver.addLast(partModel.textureSlots());
        ResourceLocation parent = partModel.parent();
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
            ResourceLocation parent = model.parent();
            if(parent != null) {
                resolver.markDependency(parent);
            }
            model.resolveDependencies(resolver);
        });
    }

    @Override
    public void fillAdditionalProperties(ContextMap.Builder propertiesBuilder) {
        NeoForgeModelProperties.fillRenderTypeProperty(propertiesBuilder, this.renderTypeGroup);
    }

    public static final class BlockStateModel implements DynamicBlockStateModel {
        private final Map<ModelParts, QuadCollection> models;
        private final ModelState modelTransform;
        private final TextureAtlasSprite particleIcon;
        private final ChunkSectionLayer chunkSectionLayer;

        public boolean isDyed;
        public boolean isSleepingBagDeployed;
        public int sleepingBagColor;

        public RenderInfo renderInfo;
        public Block block;

        public BlockStateModel(Map<ModelParts, QuadCollection> models, ModelState modelTransform, TextureAtlasSprite particleIcon, ChunkSectionLayer chunkSectionLayer) {
            this.models = models;
            this.modelTransform = modelTransform;
            this.particleIcon = particleIcon;
            this.chunkSectionLayer = chunkSectionLayer;
        }

        @Override
        public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts) {
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

        private void collectParts(List<BlockModelPart> parts) {
            QuadCollection.Builder builder = new QuadCollection.Builder();
            addBaseBackpack(builder);
            addTanks(builder);
            addSleepingBag(builder);
            addExtras(builder);
            parts.add(new SimpleModelWrapper(builder.build(), true, this.particleIcon, this.chunkSectionLayer));
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
        public TextureAtlasSprite particleIcon() {
            return this.particleIcon;
        }

        public List<BakedQuad> getQuads() {
            List<BlockModelPart> parts = new ArrayList<>();
            collectParts(parts);

            List<BakedQuad> bakedQuads = new ArrayList<>();

            for(BlockModelPart part : parts) {
                for(Direction dir : Direction.values()) {
                    bakedQuads.addAll(part.getQuads(dir));
                }
                bakedQuads.addAll(part.getQuads(null));
            }

            return bakedQuads;
        }

        private void addFluids(QuadCollection.Builder builder, RenderInfo renderInfo) {
            if(renderInfo != null && !renderInfo.isEmpty()) {
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
            if(isSleepingBagDeployed) {
                return;
            }

            builder.addAll(models.get(ModelParts.SLEEPING_BAG_EXTRAS));

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/bag/" + DyeColor.byId(sleepingBagColor).getName().toLowerCase(Locale.ENGLISH) + "_sleeping_bag"));
            rebakeSleepingBag(builder, sprite);
        }

        private void rebakeSleepingBag(QuadCollection.Builder builder, TextureAtlasSprite sprite) {
            models.get(ModelParts.SLEEPING_BAG).getAll().forEach(quad -> {
                TextureAtlasSprite oldSprite = quad.sprite();
                int[] oldData = quad.vertices();
                int[] newData = Arrays.copyOf(oldData, oldData.length);

                for(int i = 0; i < 4; i++) {
                    int index = i * 8;

                    float oldU = Float.intBitsToFloat(oldData[index + 4]);
                    float oldV = Float.intBitsToFloat(oldData[index + 5]);

                    float uUn = oldSprite.getUOffset(oldU);
                    float vUn = oldSprite.getVOffset(oldV);

                    float newU = sprite.getU(uUn);
                    float newV = sprite.getV(vUn);

                    newData[index + 4] = Float.floatToRawIntBits(newU);
                    newData[index + 5] = Float.floatToRawIntBits(newV);

                    builder.addUnculledFace(new BakedQuad(newData, quad.tintIndex(), quad.direction(), sprite, quad.shade(), quad.lightEmission()));
                }
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
            if(renderInfo == null || !renderInfo.isEmpty()) {
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

            IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            ResourceLocation stillTexture = renderProperties.getStillTexture(fluidStack);
            int color = renderProperties.getTintColor(fluidStack) | -16777216;
            TextureAtlasSprite still = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(stillTexture);
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
            quadBaker.setSprite(sprite);
            Vec3i dirVec = face.getUnitVec3i();
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
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_loader");

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
                ResourceLocation backpackTexture = ResourceLocation.tryParse(modelContents.get("backpackTexture").getAsString());
                if(backpackTexture != null) {
                    texturesBuilder.addTexture("0", new Material(TextureAtlas.LOCATION_BLOCKS, backpackTexture));
                }
            }
            if(modelContents.has("particle")) {
                ResourceLocation particleTexture = ResourceLocation.tryParse(modelContents.get("particle").getAsString());
                if(particleTexture != null) {
                    texturesBuilder.addTexture("particle", new Material(TextureAtlas.LOCATION_BLOCKS, particleTexture));
                }
            }
            //?
            texturesBuilder.addTexture("missing", new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation()));
            RenderTypeGroup renderTypeGroup = NeoForgeModelProperties.deserializeRenderType(modelContents);
            for(ModelParts part : ModelParts.values()) {
                addPartModel(builder, part, texturesBuilder.build());
            }
            return new BackpackDynamicModel(builder.build(), renderTypeGroup);
        }

        private void addPartModel(ImmutableMap.Builder<ModelParts, UnbakedModel> builder, ModelParts modelPart, TextureSlots.Data textures) {
            builder.put(modelPart, new BlockModel(null, null, true, ItemTransforms.NO_TRANSFORMS, textures, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_" + modelPart.name().toLowerCase(Locale.ENGLISH))));
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