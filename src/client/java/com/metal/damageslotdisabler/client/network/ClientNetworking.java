package com.metal.damageslotdisabler.client.network;

import com.metal.damageslotdisabler.network.ModNetworking;
import com.metal.damageslotdisabler.player.ILockedSlotsPlayer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworking {
    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.SYNC_LOCKED_SLOTS, (client, handler, buf, responseSender) -> {
            int size = buf.readInt();
            int[] slots = new int[size];
            for (int i = 0; i < size; i++) {
                slots[i] = buf.readInt();
            }
            client.execute(() -> {
                if (client.player != null) {
                    ILockedSlotsPlayer lockedPlayer = (ILockedSlotsPlayer) client.player;
                    lockedPlayer.unlockAll();
                    for (int slot : slots) {
                        lockedPlayer.lockSlot(slot);
                    }
                }
            });
        });
    }
}
