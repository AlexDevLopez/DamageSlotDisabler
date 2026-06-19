package com.metal.damageslotdisabler;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metal.damageslotdisabler.network.ModNetworking;
import com.metal.damageslotdisabler.player.ILockedSlotsPlayer;
import com.metal.damageslotdisabler.config.ModConfig;
import com.metal.damageslotdisabler.item.UnbindPotionItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DamageSlotDisabler implements ModInitializer {
    public static final String MOD_ID = "damageslotdisabler";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Item UNBIND_POTION = new UnbindPotionItem(new FabricItemSettings().maxCount(16));

    @Override
    public void onInitialize() {
        ModConfig.load();
        LOGGER.info("Initializing Damage Slot Disabler...");
        
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "unbind_potion"), UNBIND_POTION);
        
        BrewingRecipeRegistry.registerItemRecipe(Items.POTION, Items.GOLDEN_APPLE, UNBIND_POTION);
        
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ModNetworking.syncLockedSlots(handler.player);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            ModNetworking.syncLockedSlots(newPlayer);
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("unlockslots")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player != null) {
                        ILockedSlotsPlayer lockedPlayer = (ILockedSlotsPlayer) player;
                        lockedPlayer.unlockAll();
                        ModNetworking.syncLockedSlots(player);
                        context.getSource().sendFeedback(() -> Text.literal("Todos tus slots han sido desbloqueados."), false);
                    }
                    return 1;
                })
            );

            dispatcher.register(CommandManager.literal("reloadconfig")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    ModConfig.reload();
                    context.getSource().sendFeedback(() -> Text.literal("Configuración recargada exitosamente."), false);
                    return 1;
                })
            );
        });
    }
}