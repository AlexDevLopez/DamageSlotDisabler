package com.metal.damageslotdisabler.item;

import com.metal.damageslotdisabler.network.ModNetworking;
import com.metal.damageslotdisabler.player.ILockedSlotsPlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;

public class UnbindPotionItem extends PotionItem {
    public UnbindPotionItem(Settings settings) {
        super(settings);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return this.getTranslationKey();
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;
        if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
            stack.decrement(1);
        }

        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            ILockedSlotsPlayer lockedPlayer = (ILockedSlotsPlayer) player;
            List<Integer> lockedSlots = lockedPlayer.getLockedSlots();
            
            if (!lockedSlots.isEmpty()) {
                int slotToUnlock = lockedSlots.get(lockedSlots.size() - 1);
                lockedPlayer.unlockSlot(slotToUnlock);
                ModNetworking.syncLockedSlots(player);
            }
        }
        
        return stack;
    }
}
