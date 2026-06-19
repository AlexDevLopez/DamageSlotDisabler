package com.metal.damageslotdisabler.network;

import com.metal.damageslotdisabler.DamageSlotDisabler;
import com.metal.damageslotdisabler.player.ILockedSlotsPlayer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModNetworking {
    public static final Identifier SYNC_LOCKED_SLOTS = new Identifier(DamageSlotDisabler.MOD_ID, "sync_locked_slots");

    public static void syncLockedSlots(ServerPlayerEntity player) {
        List<Integer> lockedSlots = ((ILockedSlotsPlayer) player).getLockedSlots();
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(lockedSlots.size());
        for (int slot : lockedSlots) {
            buf.writeInt(slot);
        }
        ServerPlayNetworking.send(player, SYNC_LOCKED_SLOTS, buf);
    }
}
