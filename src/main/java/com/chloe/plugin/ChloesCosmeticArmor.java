package com.chloe.plugin;

import com.chloe.plugin.command.CCATestItemDeleteCommand;
import com.chloe.plugin.command.CCATestItemInsertCommand;
import com.chloe.plugin.command.CCATestItemVanishCommand;
import com.chloe.plugin.component.CCAData;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class ChloesCosmeticArmor extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

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
    }
}