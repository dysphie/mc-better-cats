package com.dysphie.bettercats.events;

import com.dysphie.bettercats.BetterCats;
import com.dysphie.bettercats.items.CatCarrierItem;
import com.dysphie.bettercats.utils.NBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BetterCats.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {

        BetterCats.LOGGER.info("e: onPlayerInteract");

        Entity target = event.getTarget();
        if (!(target instanceof CatEntity))
        {
            BetterCats.LOGGER.info("e: Not a CatEntity");
            return;
        }

        CatEntity cat = (CatEntity) target;
        PlayerEntity player = event.getPlayer();

        if (!cat.isTamed() || cat.getOwner() != player)
        {
            BetterCats.LOGGER.info("e: Not tamed/owner");
            return;
        }

        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        if (!(item instanceof CatCarrierItem))
        {
            BetterCats.LOGGER.info("e: Not CatCarrierItem");
            return;
        }

        if (CatCarrierItem.isHoldingCat(stack))
        {
            BetterCats.LOGGER.info("e: Already holding cat");
            return;
        }

        CompoundNBT catData = cat.serializeNBT();
        NBTHelper.setCompound(stack, CatCarrierItem.CAT_DATA, catData);

        BlockPos pos = event.getPos();
        World world = event.getWorld();
        world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        cat.remove();
    }
}
