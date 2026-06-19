package com.metal.damageslotdisabler.mixin;

import com.metal.damageslotdisabler.player.ILockedSlotsPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

    @Shadow @Final public PlayerEntity player;
    @Shadow @Final public DefaultedList<ItemStack> main;
    
    @Shadow public abstract boolean canStackAddMore(ItemStack existingStack, ItemStack stack);

    @Inject(method = "getEmptySlot", at = @At("HEAD"), cancellable = true)
    private void onGetEmptySlot(CallbackInfoReturnable<Integer> cir) {
        ILockedSlotsPlayer lockedPlayer = (ILockedSlotsPlayer) this.player;
        for (int i = 0; i < this.main.size(); ++i) {
            if (this.main.get(i).isEmpty() && !lockedPlayer.isSlotLocked(i)) {
                cir.setReturnValue(i);
                return;
            }
        }
        cir.setReturnValue(-1);
    }

    @Inject(method = "getOccupiedSlotWithRoomForStack", at = @At("HEAD"), cancellable = true)
    private void onGetOccupiedSlotWithRoomForStack(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        ILockedSlotsPlayer lockedPlayer = (ILockedSlotsPlayer) this.player;
        for (int i = 0; i < this.main.size(); ++i) {
            if (this.canStackAddMore(this.main.get(i), stack) && !lockedPlayer.isSlotLocked(i)) {
                cir.setReturnValue(i);
                return;
            }
        }
        cir.setReturnValue(-1);
    }
}
