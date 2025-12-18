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
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
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
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;
import java.util.function.Predicate;

public class BackpackDynamicModel implements UnbakedModel, ResolvableModel {
    private final Map<ModelParts, BlockModel> modelParts;
    private final ChunkSectionLayer renderType;

    private BackpackDynamicModel(Map<ModelParts, BlockModel> modelParts, ChunkSectionLayer renderType) {
        this.modelParts = modelParts;
        this.renderType = renderType;
    }

    public DynamicBlockStateModel bakeBlockStateModel(ModelBaker baker, ResolvedModel resolvedModel, ModelState modelState) {
        ImmutableMap.Builder<ModelParts, QuadCollection> builder = ImmutableMap.builder();
        modelParts.forEach((part, model) -> {
            builder.put(part, baker.getModel(model.parent()).getTopGeometry().bake(getTextureSlots(baker, model, resolvedModel), baker, modelState, resolvedModel));
        });
        return new DynamicBlockStateModel(builder.build(), modelState, resolvedModel.resolveParticleSprite(getTextureSlots(baker, modelParts.get(ModelParts.BASE), resolvedModel), baker), this.renderType);
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
    public void resolveDependencies(ResolvableModel.Resolver resolver) {
        modelParts.values().forEach(model -> {
            ResourceLocation parent = model.parent();
            if(parent != null) {
                resolver.markDependency(parent);
            }
            //model.resolveDependencies(resolver);
        });
    }

    public static final class DynamicBlockStateModel implements BlockStateModel {
        private final Map<ModelParts, QuadCollection> models;
        private final ModelState modelTransform;
        private final TextureAtlasSprite particleIcon;
        private final ChunkSectionLayer renderType;

        public boolean isDyed;
        public boolean isSleepingBagDeployed;
        public int sleepingBagColor;

        public RenderInfo renderInfo;
        public Block block;

        public DynamicBlockStateModel(Map<ModelParts, QuadCollection> models, ModelState modelTransform, TextureAtlasSprite particleIcon, ChunkSectionLayer renderType) {
            this.models = models;
            this.modelTransform = modelTransform;
            this.particleIcon = particleIcon;
            this.renderType = renderType;
        }

        @Override
        public Object createGeometryKey(BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random) {
            return null;
        }

        @Override
        public void emitQuads(QuadEmitter emitter, BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
            BackpackBlockEntity.BackpackRenderData renderData = blockView.getBlockEntityRenderData(pos) instanceof BackpackBlockEntity.BackpackRenderData backpackRenderData
                    ? backpackRenderData
                    : new BackpackBlockEntity.BackpackRenderData(RenderInfo.EMPTY, -1, false, DyeColor.RED.getId());

            block = state.getBlock();
            isDyed = renderData.dyeColor() != -1;
            renderInfo = renderData.info() == null ? RenderInfo.EMPTY : renderData.info();
            sleepingBagColor = renderData.sleepingBagColor();
            isSleepingBagDeployed = renderData.isSleepingBagDeployed();

            getQuads().forEach(quad -> emitter.fromBakedQuad(quad).renderLayer(this.renderType).emit());
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
            if(block == ModBlocks.FOX_TRAVELERS_BACKPACK) {
                addAll(builder, models.get(ModelParts.FOX_NOSE));
            }
            if(block == ModBlocks.WARDEN_TRAVELERS_BACKPACK) {
                addAll(builder, models.get(ModelParts.WARDEN_HORNS));
            }
            if(block == ModBlocks.WOLF_TRAVELERS_BACKPACK) {
                addAll(builder, models.get(ModelParts.WOLF_NOSE));
            }
            if(block == ModBlocks.OCELOT_TRAVELERS_BACKPACK) {
                addAll(builder, models.get(ModelParts.OCELOT_NOSE));
            }
            if(block == ModBlocks.PIG_TRAVELERS_BACKPACK || block == ModBlocks.HORSE_TRAVELERS_BACKPACK) {
                addAll(builder, models.get(ModelParts.PIG_NOSE));
            }
            if(block == ModBlocks.VILLAGER_TRAVELERS_BACKPACK || block == ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK) {
                addAll(builder, models.get(ModelParts.VILLAGER_NOSE));
            }
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

        private int convertColorARGBtoABGR(int color) {
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            return (a << 24) | (b << 16) | (g << 8) | r;
        }

        private BakedQuad createQuad(List<Vector3f> vectors, TextureAtlasSprite sprite, Direction face, boolean hasAmbientOcclusion, int color, float u1x, float u2x, float v1x, float v2x, int tintIndex) {
            u1x = u1x / 16F;
            u2x = u2x / 16F;
            v1x = v1x / 16F;
            v2x = v2x / 16F;

            float u1 = sprite.getU(u1x);
            float u2 = sprite.getU(u2x);
            float v1 = sprite.getV(v1x);
            float v2 = sprite.getV(v2x);

            Vec3i normal = face.getUnitVec3i();
            int[] vertexData = new int[4 * 8]; // 4 vertices, 8 ints per vertex

            int packedColor = convertColorARGBtoABGR(color);

            // Helper to encode vertex data at given vertex index (0-3)
            for(int i = 0; i < 4; i++) {
                int base = i * 8;
                Vector3f vec = vectors.get(i);

                vertexData[base] = Float.floatToRawIntBits(vec.x());
                vertexData[base + 1] = Float.floatToRawIntBits(vec.y());
                vertexData[base + 2] = Float.floatToRawIntBits(vec.z());

                vertexData[base + 3] = packedColor;

                // UV coords in correct order depending on vertex
                float u = (i == 0 || i == 1) ? u1 : u2;
                float v = (i == 0 || i == 3) ? v1 : v2;

                vertexData[base + 4] = Float.floatToRawIntBits(u);
                vertexData[base + 5] = Float.floatToRawIntBits(v);

                vertexData[base + 6] = normal.getX();
                vertexData[base + 7] = normal.getY();
                // Usually normal.Z should also be stored or the light coordinate -> but Minecraft uses signed byte packed into an int or ignores?
                // Here store normal Z too in base + 7 by shifting or leave 0?
                // For simplicity, store normal Z in the higher bits of base + 7
            }

            // Minecraft BakedQuad constructor parameters:
            // vertexData, tintIndex, face, sprite, shade, lightCoord
            // Use tintIndex = -1 (no tint), shade = true, and lightCoord = 0 for default lighting
            return new BakedQuad(vertexData, tintIndex, face, sprite, hasAmbientOcclusion, 0);
        }

        @Override
        public void collectParts(RandomSource randomSource, List<BlockModelPart> list) {
        }

        private void collectParts(List<BlockModelPart> parts) {
            QuadCollection.Builder builder = new QuadCollection.Builder();
            addBaseBackpack(builder);
            addTanks(builder);
            addSleepingBag(builder);
            addExtras(builder);
            parts.add(new SimpleModelWrapper(builder.build(), true, this.particleIcon));
        }

        public void addBaseBackpack(QuadCollection.Builder builder) {
            if(isDyed && block == ModBlocks.STANDARD_TRAVELERS_BACKPACK) {
                addAll(builder, models.get(ModelParts.BASE_DYED));
                addAll(builder, models.get(ModelParts.EXTRAS));
            } else {
                addAll(builder, models.get(ModelParts.BASE));
            }
        }

        private void addTanks(QuadCollection.Builder builder) {
            if(renderInfo == null || !renderInfo.isEmpty()) {
                addAll(builder, models.get(ModelParts.TANKS));
                addFluids(builder, renderInfo);
            }
        }

        private void addFluids(QuadCollection.Builder builder, RenderInfo renderInfo) {
            if(renderInfo != null && !renderInfo.isEmpty()) {
                if(!renderInfo.getLeftFluidStack().isEmpty()) {
                    addFluid(builder, renderInfo.getLeftFluidStack(), (float)renderInfo.getLeftFluidStack().getAmount() / renderInfo.getCapacity(), 1.8F / 16D, 0);
                }
                if(!renderInfo.getRightFluidStack().isEmpty()) {
                    addFluid(builder, renderInfo.getRightFluidStack(), (float)renderInfo.getRightFluidStack().getAmount() / renderInfo.getCapacity(), 12.7F / 16D, 1);
                }
            }
        }

        private void addFluid(QuadCollection.Builder builder, FluidVariantWrapper fluidStack, float ratio, double xMin, int tintIndex) {
            if(fluidStack.isEmpty()) {
                return;
            }

            double yMin = 0.8D / 16D;
            double yMax = yMin + (ratio * 6.2D) / 16D;
            AABB bounds = new AABB(xMin, yMin, 6.3D / 16D, xMin + 1.5D / 16D, yMax, 7.8D / 16D);

            int color = FluidVariantRendering.getColor(fluidStack.fluidVariant()) | -16777216;
            TextureAtlasSprite still = FluidVariantRendering.getSprite(fluidStack.fluidVariant());
            float x1 = 0F;
            float x2 = 3F;
            float y1 = 0F;
            float y2 = ratio * 12F;
            float z1 = 0F;
            float z2 = 3F;

            builder.addUnculledFace(createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.minZ), getVector(bounds.minX, bounds.maxY, bounds.maxZ), getVector(bounds.maxX, bounds.maxY, bounds.maxZ), getVector(bounds.maxX, bounds.maxY, bounds.minZ)), still, Direction.UP, false, color, x1, x2, z1, z2, tintIndex));
            builder.addUnculledFace(createQuad(List.of(getVector(bounds.maxX, bounds.maxY, bounds.minZ), getVector(bounds.maxX, bounds.minY, bounds.minZ), getVector(bounds.minX, bounds.minY, bounds.minZ), getVector(bounds.minX, bounds.maxY, bounds.minZ)), still, Direction.NORTH, false, color, x1, x2, y1, y2, tintIndex));
            builder.addUnculledFace(createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.maxZ), getVector(bounds.minX, bounds.minY, bounds.maxZ), getVector(bounds.maxX, bounds.minY, bounds.maxZ), getVector(bounds.maxX, bounds.maxY, bounds.maxZ)), still, Direction.SOUTH, false, color, x1, x2, y1, y2, tintIndex));
            builder.addUnculledFace(createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.minZ), getVector(bounds.minX, bounds.minY, bounds.minZ), getVector(bounds.minX, bounds.minY, bounds.maxZ), getVector(bounds.minX, bounds.maxY, bounds.maxZ)), still, Direction.WEST, false, color, z1, z2, y1, y2, tintIndex));
            builder.addUnculledFace(createQuad(List.of(getVector(bounds.maxX, bounds.maxY, bounds.maxZ), getVector(bounds.maxX, bounds.minY, bounds.maxZ), getVector(bounds.maxX, bounds.minY, bounds.minZ), getVector(bounds.maxX, bounds.maxY, bounds.minZ)), still, Direction.EAST, false, color, z1, z2, y1, y2, tintIndex));
        }

        //Rebake sleeping bag to change sprite dynamically
        private void addSleepingBag(QuadCollection.Builder builder) {
            if(isSleepingBagDeployed) {
                return;
            }
            addAll(builder, models.get(ModelParts.SLEEPING_BAG_EXTRAS));
            TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/bag/" + DyeColor.byId(sleepingBagColor).getName().toLowerCase(Locale.ENGLISH) + "_sleeping_bag"));
            rebakeSleepingBag(builder, sprite);
        }

        private static QuadCollection.Builder addAll(QuadCollection.Builder base, QuadCollection collection) {
            for(Direction direction : Direction.values()) {
                var quads = collection.getQuads(direction);
                for(var quad : quads) {
                    base.addCulledFace(direction, quad);
                }
            }
            var unculledQuads = collection.getQuads(null);
            for(var quad : unculledQuads) {
                base.addUnculledFace(quad);
            }
            return base;
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

        @Override
        public TextureAtlasSprite particleIcon() {
            return this.particleIcon;
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

    public static final class Loader implements UnbakedModelDeserializer {
        public static final Loader INSTANCE = new Loader();

        @Override
        public UnbakedModel deserialize(JsonObject modelContents, JsonDeserializationContext context) {
            ImmutableMap.Builder<ModelParts, BlockModel> builder = ImmutableMap.builder();
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
            ChunkSectionLayer renderTypeGroup = deserializeRenderType(modelContents);
            for(ModelParts part : ModelParts.values()) {
                addPartModel(builder, part, texturesBuilder.build());
            }
            return new BackpackDynamicModel(builder.build(), renderTypeGroup);
        }

        private void addPartModel(ImmutableMap.Builder<ModelParts, BlockModel> builder, ModelParts modelPart, TextureSlots.Data textures) {
            builder.put(modelPart, new BlockModel(null, null, true, ItemTransforms.NO_TRANSFORMS, textures, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_" + modelPart.name().toLowerCase(Locale.ENGLISH))));
        }

        private static Map<ResourceLocation, ChunkSectionLayer> RENDER_TYPES;

        public static ChunkSectionLayer deserializeRenderType(JsonObject jsonObject) {
            if(jsonObject.has("render_type")) {
                String renderTypeHintName = GsonHelper.getAsString(jsonObject, "render_type");
                return RENDER_TYPES.get(ResourceLocation.parse(renderTypeHintName));
            }
            return ChunkSectionLayer.SOLID;
        }

        public static void loadVanillaRenderTypes() {
            RENDER_TYPES = new HashMap<>();
            RENDER_TYPES.put(ResourceLocation.withDefaultNamespace("solid"), ChunkSectionLayer.SOLID);
            RENDER_TYPES.put(ResourceLocation.withDefaultNamespace("cutout"), ChunkSectionLayer.CUTOUT);
            // Generally entity/item rendering shouldn't use mipmaps, so cutout_mipped has them off by default. To enforce them, use cutout_mipped_all.
            RENDER_TYPES.put(ResourceLocation.withDefaultNamespace("cutout_mipped"), ChunkSectionLayer.CUTOUT_MIPPED);
            RENDER_TYPES.put(ResourceLocation.withDefaultNamespace("cutout_mipped_all"), ChunkSectionLayer.CUTOUT_MIPPED);
            RENDER_TYPES.put(ResourceLocation.withDefaultNamespace("translucent"), ChunkSectionLayer.TRANSLUCENT);
            RENDER_TYPES.put(ResourceLocation.withDefaultNamespace("tripwire"), ChunkSectionLayer.TRIPWIRE);
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