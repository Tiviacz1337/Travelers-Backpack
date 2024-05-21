package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurios;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod("travelersbackpack")
public class TravelersBackpack
{
    public static final String MODID = "travelersbackpack";
    public static final Logger LOGGER = LogManager.getLogger();

    private static boolean curiosLoaded;
    private static boolean craftingTweaksLoaded;

    public static boolean corpseLoaded;
    public static boolean gravestoneLoaded;

    public static boolean comfortsLoaded;
    public static boolean endermanOverhaulLoaded;

    public TravelersBackpack(IEventBus eventBus)
    {
        NeoForgeMod.enableMilkFluid();

        TravelersBackpackConfig.register(ModLoadingContext.get());

        eventBus.addListener(this::setup);
        eventBus.addListener(this::doClientStuff);
        eventBus.addListener(this::onFinish);
        eventBus.addListener(this::onEnqueueIMC);
        eventBus.addListener(this::registerPayloadHandler);
        eventBus.addListener(this::registerCapabilities);

        ModItems.ITEMS.register(eventBus);
        ModBlocks.BLOCKS.register(eventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(eventBus);
        ModMenuTypes.MENU_TYPES.register(eventBus);
        ModRecipeSerializers.SERIALIZERS.register(eventBus);
        ModFluids.FLUID_TYPES.register(eventBus);
        ModFluids.FLUIDS.register(eventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(eventBus);
        ModLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(eventBus);
        ModLootConditions.LOOT_CONDITIONS.register(eventBus);
        ModAttachmentTypes.ATTACHMENT_TYPES.register(eventBus);

        curiosLoaded = ModList.get().isLoaded("curios");
        craftingTweaksLoaded = ModList.get().isLoaded("craftingtweaks");

        corpseLoaded = ModList.get().isLoaded("corpse");
        gravestoneLoaded = ModList.get().isLoaded("gravestone");

        comfortsLoaded = ModList.get().isLoaded("comforts");
        endermanOverhaulLoaded = ModList.get().isLoaded("endermanoverhaul");
    }

    private void onEnqueueIMC(InterModEnqueueEvent event)
    {
        if(!enableCurios()) return;
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BACK.getMessageBuilder().build());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            EffectFluidRegistry.initEffects();
            enableCraftingTweaks();
        });
    }

    private void registerPayloadHandler(final RegisterPayloadHandlerEvent event)
    {
        ModNetwork.register(event.registrar(TravelersBackpack.MODID));
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        ModClientEventHandler.registerScreenFactory();
        ModClientEventHandler.bindTileEntityRenderer();
        ModClientEventHandler.registerItemModelProperty();
    }

    private void onFinish(final FMLLoadCompleteEvent event)
    {
        ModItems.addBackpacksToList();

        //Slots
        TravelersBackpackConfig.COMMON.loadItemsFromConfig(TravelersBackpackConfig.toolSlotsAcceptableItems, ToolSlotItemHandler.TOOL_SLOTS_ACCEPTABLE_ITEMS);
        TravelersBackpackConfig.COMMON.loadItemsFromConfig(TravelersBackpackConfig.blacklistedItems, BackpackSlotItemHandler.BLACKLISTED_ITEMS);

        //Backpack spawn
        TravelersBackpackConfig.COMMON.loadEntityTypesFromConfig(TravelersBackpackConfig.possibleOverworldEntityTypes, Reference.ALLOWED_TYPE_ENTRIES);
        TravelersBackpackConfig.COMMON.loadEntityTypesFromConfig(TravelersBackpackConfig.possibleNetherEntityTypes, Reference.ALLOWED_TYPE_ENTRIES);
        TravelersBackpackConfig.COMMON.loadItemsFromConfig(TravelersBackpackConfig.overworldBackpacks, ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES);
        TravelersBackpackConfig.COMMON.loadItemsFromConfig(TravelersBackpackConfig.netherBackpacks, ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES);

        //Abilities
        TravelersBackpackConfig.COMMON.loadItemsFromConfig(TravelersBackpackConfig.allowedAbilities, BackpackAbilities.ALLOWED_ABILITIES);

        ResourceUtils.createTextureLocations();
        ResourceUtils.createSleepingBagTextureLocations();
    }

    public static boolean enableCurios()
    {
        return curiosLoaded && TravelersBackpackConfig.curiosIntegration;
    }

    public static void enableCraftingTweaks()
    {
        if(craftingTweaksLoaded)
        {
            try {
                Class.forName("com.tiviacz.travelersbackpack.compat.craftingtweaks.TravelersBackpackCraftingGridProvider").getConstructor().newInstance();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isAnyGraveModInstalled()
    {
        return TravelersBackpack.corpseLoaded || TravelersBackpack.gravestoneLoaded;
    }

    public void registerCapabilities(final RegisterCapabilitiesEvent event)
    {
        if(enableCurios())
        {
            TravelersBackpackCurios.registerCurio(event);
        }

        //Register block capability
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntityTypes.TRAVELERS_BACKPACK.get(), (blockEntity, side) ->
        {
            return blockEntity.getHandler();
        });

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntityTypes.TRAVELERS_BACKPACK.get(), (blockEntity, side) ->
        {
            Direction direction = blockEntity.getBlockDirection(blockEntity);

            if(side == null) return blockEntity.getLeftTank();

            if(direction == Direction.NORTH)
            {
                switch(side)
                {
                    case WEST: return blockEntity.getRightTank();
                    case EAST: return blockEntity.getLeftTank();
                }
            }
            if(direction == Direction.SOUTH)
            {
                switch(side)
                {
                    case EAST: return blockEntity.getRightTank();
                    case WEST: return blockEntity.getLeftTank();
                }
            }

            if(direction == Direction.EAST)
            {
                switch(side)
                {
                    case NORTH: return blockEntity.getRightTank();
                    case SOUTH: return blockEntity.getLeftTank();
                }
            }

            if(direction == Direction.WEST)
            {
                switch(side)
                {
                    case SOUTH: return blockEntity.getRightTank();
                    case NORTH: return blockEntity.getLeftTank();
                }
            }
            return blockEntity.getLeftTank();
        });
    }
}