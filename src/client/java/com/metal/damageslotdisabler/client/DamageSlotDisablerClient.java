package com.metal.damageslotdisabler.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

import com.metal.damageslotdisabler.DamageSlotDisabler;
import com.metal.damageslotdisabler.client.network.ClientNetworking;
import com.metal.damageslotdisabler.player.ILockedSlotsPlayer;

@Environment(EnvType.CLIENT)
public class DamageSlotDisablerClient implements ClientModInitializer {
    
    private static final Identifier LOCKED_TEXTURE = new Identifier(DamageSlotDisabler.MOD_ID, "textures/gui/locked_slot.png");

    @Override
    public void onInitializeClient() {
        ClientNetworking.registerReceivers();

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;
            if (player == null) return;

            ILockedSlotsPlayer lockedPlayer = (ILockedSlotsPlayer) player;
            int scaledWidth = context.getScaledWindowWidth();
            int scaledHeight = context.getScaledWindowHeight();

            // Hotbar (slots 0 to 8)
            int startX = scaledWidth / 2 - 90;
            int startY = scaledHeight - 22;

            for (int i = 0; i < 9; i++) {
                if (lockedPlayer.isSlotLocked(i)) {
                    int x = startX + i * 20 + 2;
                    int y = startY + 3;
                    context.drawTexture(LOCKED_TEXTURE, x, y, 0, 0, 16, 16, 16, 16);
                }
            }

            // Offhand (slot 40)
            if (lockedPlayer.isSlotLocked(40)) {
                int offhandX;
                if (player.getMainArm() == Arm.RIGHT) {
                    offhandX = scaledWidth / 2 - 91 - 29;
                } else {
                    offhandX = scaledWidth / 2 + 91;
                }
                context.drawTexture(LOCKED_TEXTURE, offhandX + 3, startY + 3, 0, 0, 16, 16, 16, 16);
            }
        });
    }
}
