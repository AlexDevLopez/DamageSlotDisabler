package com.metal.damageslotdisabler.mixin;

import com.metal.damageslotdisabler.player.ILockedSlotsPlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void onCopyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ILockedSlotsPlayer newPlayer = (ILockedSlotsPlayer) this;
        ILockedSlotsPlayer oldLockedPlayer = (ILockedSlotsPlayer) oldPlayer;
        
        for (int slot : oldLockedPlayer.getLockedSlots()) {
            newPlayer.lockSlot(slot);
        }
    }
}
