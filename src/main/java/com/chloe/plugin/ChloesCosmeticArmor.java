package com.chloe.plugin;

import com.chloe.plugin.command.CCATestItemDeleteCommand;
import com.chloe.plugin.command.CCATestItemInsertCommand;
import com.chloe.plugin.command.CCATestItemVanishCommand;
import com.chloe.plugin.component.CCAData;
import com.chloe.plugin.event.InterceptArmorEquipEvent;
import com.chloe.plugin.interaction.OpenMagicMirrorInteraction;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class ChloesCosmeticArmor extends JavaPlugin {

    public ChloesCosmeticArmor(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        super.setup();
        CCAData.INSTANCE = getEntityStoreRegistry().registerComponent(CCAData.class, "CCA_Data", CCAData.CODEC);

        getCommandRegistry().registerCommand(new CCATestItemInsertCommand());
        getCommandRegistry().registerCommand(new CCATestItemDeleteCommand());
        getCommandRegistry().registerCommand(new CCATestItemVanishCommand());

        getCodecRegistry(Interaction.CODEC).register("CCA_OpenMirror", OpenMagicMirrorInteraction.class, OpenMagicMirrorInteraction.CODEC);

        getEventRegistry().registerGlobal(LivingEntityInventoryChangeEvent.class, InterceptArmorEquipEvent::hookIntoEvent);
    }
}