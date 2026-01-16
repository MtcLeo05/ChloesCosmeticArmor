package com.chloe.plugin;

import com.chloe.plugin.command.CCATestItemDeleteCommand;
import com.chloe.plugin.command.CCATestItemInsertCommand;
import com.chloe.plugin.command.CCATestItemVanishCommand;
import com.chloe.plugin.component.CCAData;
import com.chloe.plugin.event.InterceptArmorEquipEvent;
import com.chloe.plugin.interaction.OpenMirrorInteraction;
import com.chloe.plugin.interaction.OpenVanishInteraction;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

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

        getCodecRegistry(Interaction.CODEC).register("CCA_OpenMirror", OpenMirrorInteraction.class, OpenMirrorInteraction.CODEC);
        getCodecRegistry(Interaction.CODEC).register("CCA_OpenVanish", OpenVanishInteraction.class, OpenVanishInteraction.CODEC);

        getEventRegistry().registerGlobal(LivingEntityInventoryChangeEvent.class, InterceptArmorEquipEvent::hookIntoEvent);
    }
}