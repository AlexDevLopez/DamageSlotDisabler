package com.metal.damageslotdisabler.mixin;

import com.metal.damageslotdisabler.player.ILockedSlotsPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.metal.damageslotdisabler.handler.DamageSlotHandler;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ILockedSlotsPlayer {
    
    @Unique
    private final List<Integer> damageslotdisabler$lockedSlots = new ArrayList<>();

    @Override
    public List<Integer> getLockedSlots() {
        return this.damageslotdisabler$lockedSlots;
    }

    @Override
    public void lockSlot(int slotId) {
        if (!this.damageslotdisabler$lockedSlots.contains(slotId)) {
            this.damageslotdisabler$lockedSlots.add(slotId);
        }
    }

    @Override
    public void unlockSlot(int slotId) {
        this.damageslotdisabler$lockedSlots.remove(Integer.valueOf(slotId));
    }

    @Override
    public void unlockAll() {
        this.damageslotdisabler$lockedSlots.clear();
    }

    @Override
    public boolean isSlotLocked(int slotId) {
        return this.damageslotdisabler$lockedSlots.contains(slotId);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtList list = new NbtList();
        for (int slot : this.damageslotdisabler$lockedSlots) {
            list.add(NbtInt.of(slot));
        }
        nbt.put("DamageSlotDisabler_LockedSlots", list);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.damageslotdisabler$lockedSlots.clear();
        if (nbt.contains("DamageSlotDisabler_LockedSlots", NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList("DamageSlotDisabler_LockedSlots", NbtElement.INT_TYPE);
            for (int i = 0; i < list.size(); i++) {
                this.damageslotdisabler$lockedSlots.add(list.getInt(i));
            }
        }
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && !((PlayerEntity)(Object)this).getWorld().isClient()) {
            DamageSlotHandler.handleDamage((ServerPlayerEntity)(Object)this, source, amount);
        }
    }
}
