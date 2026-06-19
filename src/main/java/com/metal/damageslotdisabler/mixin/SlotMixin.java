package com.metal.damageslotdisabler.mixin;

import com.metal.damageslotdisabler.player.ILockedSlotsPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow @Final public Inventory inventory;
    
    @Shadow public abstract int getIndex();

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void onCanInsert(CallbackInfoReturnable<Boolean> cir) {
        if (this.inventory instanceof PlayerInventory) {
            PlayerEntity player = ((PlayerInventory) this.inventory).player;
            if (player != null && ((ILockedSlotsPlayer) player).isSlotLocked(this.getIndex())) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "canTakeItems", at = @At("HEAD"), cancellable = true)
    private void onCanTakeItems(PlayerEntity playerEntity, CallbackInfoReturnable<Boolean> cir) {
        if (this.inventory instanceof PlayerInventory) {
            PlayerEntity player = ((PlayerInventory) this.inventory).player;
            if (player != null && ((ILockedSlotsPlayer) player).isSlotLocked(this.getIndex())) {
                cir.setReturnValue(false);
            }
        }
    }
}
