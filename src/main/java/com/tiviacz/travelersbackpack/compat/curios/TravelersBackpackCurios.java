package com.tiviacz.travelersbackpack.compat.curios;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;
import java.util.function.Predicate;

public class TravelersBackpackCurios
{
    public static void registerCurio(RegisterCapabilitiesEvent event)
    {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> event.registerItem(CuriosCapability.ITEM, (stack, context) -> new TravelersBackpackCurio(stack), holder::get));
    }

    public static Optional<SlotResult> getCurioTravelersBackpack(LivingEntity livingEntity)
    {
        Predicate<ItemStack> backpack = stack -> stack.getItem() instanceof TravelersBackpackItem;
        return CuriosApi.getCuriosInventory(livingEntity).flatMap(curio -> curio.findFirstCurio(backpack));
    }

    public static ItemStack getCurioTravelersBackpackStack(Player player)
    {
        if(getCurioTravelersBackpack(player).isPresent())
        {
            return getCurioTravelersBackpack(player).get().stack();
        }
        return ItemStack.EMPTY;
    }

    public static TravelersBackpackContainer getCurioTravelersBackpackInventory(Player player)
    {
        return AttachmentUtils.getAttachment(player).map(ITravelersBackpack::getContainer).orElse(null);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean renderCurioLayer(AbstractClientPlayer clientPlayer)
    {
        if(TravelersBackpackCurios.getCurioTravelersBackpack(clientPlayer).isPresent())
        {
            return CuriosApi.getCuriosInventory(clientPlayer).map(curios -> curios.findFirstCurio(stack -> stack.getItem() instanceof TravelersBackpackItem)
                    .map(result -> result.slotContext().visible()).orElse(false)).orElse(false);
        }
        return false;
    }

    public static void rightClickUnequip(Player player, ItemStack stack)
    {
        if(TravelersBackpack.enableCurios())
        {
            Optional<SlotContext> slotContext = CuriosApi.getCuriosInventory(player).flatMap(curios ->
                    curios.findFirstCurio(predicate -> ItemStack.isSameItemSameTags(predicate, stack))).flatMap(result -> Optional.ofNullable(result.slotContext()));

            slotContext.ifPresent(context -> CuriosApi.getCuriosInventory(player).ifPresent(curios -> curios.getCurios().get("back").getStacks().setStackInSlot(context.index(), ItemStack.EMPTY)));
        }
    }

    public static boolean rightClickEquip(Player player, ItemStack stack, boolean simulate)
    {
        if(CuriosApi.getCurio(stack).isPresent())
        {
            ICurio curio = CuriosApi.getCurio(stack).get();
            Optional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosInventory(player);

            if(curiosHandler.isPresent())
            {
                int index = -1;
                boolean isEmptySlot = false;

                ICurioStacksHandler curioHandler = curiosHandler.get().getCurios().get("back");

                for(int i = 0; i < curioHandler.getSlots(); i++)
                {
                    if(curioHandler.getStacks().getStackInSlot(i).isEmpty())
                    {
                        index = i;
                        isEmptySlot = true;
                    }
                }

                if(!isEmptySlot) return false;
                NonNullList<Boolean> renderStates = curioHandler.getRenders();

                SlotContext slotContext = new SlotContext(curioHandler.getIdentifier(), player, index, false, renderStates.size() > index && renderStates.get(index));

                if(curio.canEquip(slotContext))
                {
                    if(simulate) return true;
                    curioHandler.getStacks().setStackInSlot(index, stack.copy());
                    curio.onEquipFromUse(slotContext);

                    //Sync
                    AttachmentUtils.synchronise(player);
                    AttachmentUtils.synchroniseToOthers(player);
                    return true;
                }
            }
        }
        return false;
    }
}