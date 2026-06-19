package com.metal.damageslotdisabler.handler;

import com.metal.damageslotdisabler.config.ModConfig;
import com.metal.damageslotdisabler.network.ModNetworking;
import com.metal.damageslotdisabler.player.ILockedSlotsPlayer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class DamageSlotHandler {
    
    public static void handleDamage(ServerPlayerEntity player, DamageSource source, float amount) {
        if (amount <= 0) return;

        ILockedSlotsPlayer lockedPlayer = (ILockedSlotsPlayer) player;

        if (lockedPlayer.getLockedSlots().size() >= ModConfig.get().maxLockedSlots) {
            return;
        }
        
        List<Integer> targetOrder = new ArrayList<>();
        targetOrder.add(40); // Off-hand
        
        // Main Inventory (top to bottom, 9 to 35)
        for (int i = 9; i <= 35; i++) {
            targetOrder.add(i);
        }
        
        // Hotbar (8 down to 0)
        for (int i = 8; i >= 0; i--) {
            targetOrder.add(i);
        }

        int slotToLock = -1;
        for (int slotId : targetOrder) {
            if (!lockedPlayer.isSlotLocked(slotId)) {
                slotToLock = slotId;
                break;
            }
        }

        if (slotToLock != -1) {
            lockedPlayer.lockSlot(slotToLock);
            
            ItemStack stack = player.getInventory().getStack(slotToLock);
            if (!stack.isEmpty()) {
                if (ModConfig.get().dropItemsOnLock) {
                    ItemEntity itemEntity = player.dropItem(stack, true, false);
                    if (itemEntity != null) {
                        itemEntity.setPickupDelay(40);
                    }
                }
                player.getInventory().setStack(slotToLock, ItemStack.EMPTY);
            }

            ModNetworking.syncLockedSlots(player);
        }
    }
}
