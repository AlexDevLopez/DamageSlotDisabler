package com.metal.damageslotdisabler.mixin.client;

import com.metal.damageslotdisabler.DamageSlotDisabler;
import com.metal.damageslotdisabler.player.ILockedSlotsPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    private static final Identifier LOCK_TEXTURE = new Identifier(DamageSlotDisabler.MOD_ID, "textures/gui/locked_slot.png");

    @Inject(method = "drawSlot", at = @At("TAIL"))
    private void onDrawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        if (slot.inventory instanceof PlayerInventory) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.player != null) {
                ILockedSlotsPlayer lockedPlayer = (ILockedSlotsPlayer) client.player;
                if (lockedPlayer.isSlotLocked(slot.getIndex())) {
                    context.drawTexture(LOCK_TEXTURE, slot.x, slot.y, 0, 0, 16, 16, 16, 16);
                }
            }
        }
    }
}
