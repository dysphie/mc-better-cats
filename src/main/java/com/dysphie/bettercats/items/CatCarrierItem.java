package com.dysphie.bettercats.items;
import com.dysphie.bettercats.BetterCats;
import com.dysphie.bettercats.utils.NBTHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class CatCarrierItem extends Item
{
    public static final String CAT_DATA = "carried_cat";

    public CatCarrierItem() {

        super(new Item.Properties()
                .group(ItemGroup.TRANSPORTATION)
                .maxStackSize(1)
                /* .food(Foods.BEEF) >:D */);
    }

    public static float getBreed(ItemStack stack)
    {
        CompoundNBT catNBT = NBTHelper.getCompound(stack, "carried_cat");
        if (!catNBT.isEmpty())
            return catNBT.getInt("CatType");

        return -1.0f;
    }

    public static boolean isHoldingCat(ItemStack stack)
    {
        boolean is = !NBTHelper.getCompound(stack, "carried_cat").isEmpty();
        BetterCats.LOGGER.info("isHoldingCat: {}", is);
        return is;
    }

    @Override
    public @NotNull ActionResultType onItemUse(ItemUseContext context) {

        if (context.getPlayer() == null)
            return ActionResultType.FAIL;

        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        ItemStack held = player.getHeldItem(hand);

        if (!CatCarrierItem.isHoldingCat(held))
        {
            BetterCats.LOGGER.info("onItemUse: Not holding cat");
            return ActionResultType.FAIL;
        }

        CompoundNBT data = NBTHelper.getCompound(held, CAT_DATA);

        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        Direction direction = context.getFace();
        BlockState blockstate = world.getBlockState(blockpos);

        if (!blockstate.getCollisionShape(world, blockpos).isEmpty()) {
            blockpos = blockpos.offset(direction);
        }

        CatEntity cat = new CatEntity(EntityType.CAT, world);
        cat.read(data);
        cat.setPosition(blockpos.getX(), blockpos.getY(), blockpos.getZ());
        world.addEntity(cat);

        player.swingArm(hand);
        NBTHelper.setCompound(held, CAT_DATA, new CompoundNBT());
        return ActionResultType.SUCCESS;
    }
}