package com.chloe.plugin.command;

import com.chloe.plugin.component.CCAData;
import com.chloe.plugin.event.InterceptArmorEquipEvent;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.transaction.ClearTransaction;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

import static com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand.MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD;

public class CCATestItemVanishCommand extends AbstractPlayerCommand {

    private final RequiredArg<String> armorType;

    public CCATestItemVanishCommand() {
        super("cca-vanish-item", "Manually vanishes an item in one of the cosmetic armor slots");

        armorType = withRequiredArg("armor", "Armor slot to target", ArgTypes.STRING);
        setPermissionGroup(GameMode.Adventure);
    }


    @Override
    protected void execute(@Nonnull CommandContext ctx, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        if(CCAData.INSTANCE == null) return;

        CommandSender sender = ctx.sender();

        if(!(sender instanceof Player senderPlayer)) return;

        if(!ref.isValid()) {
            ctx.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
            return;
        }

        Store<EntityStore> playerStore = ref.getStore();
        CCAData data = playerStore.getComponent(ref, CCAData.INSTANCE);

        if(data == null) {
            playerStore.addComponent(ref, CCAData.INSTANCE, new CCAData());
            data = playerStore.getComponent(ref, CCAData.INSTANCE);
        }

        String armor = armorType.get(ctx);

        switch (armor.toLowerCase()) {
            case "head": {
                ctx.sendMessage(Message.raw((data.vanishHead()? "Vanished": "Unvanished") + " head slot!"));
                break;
            }
            case "chest": {
                ctx.sendMessage(Message.raw((data.vanishChest()? "Vanished": "Unvanished") + " chest slot!"));
                break;
            }
            case "hands": {
                ctx.sendMessage(Message.raw((data.vanishHands()? "Vanished": "Unvanished") + " hands slot!"));
                break;
            }
            case "legs": {
                ctx.sendMessage(Message.raw((data.vanishLegs()? "Vanished": "Unvanished") + " legs slot!"));
                break;
            }
            default: {
                ctx.sendMessage(Message.raw("Invalid armor type! Valid types: head - chest - hands - legs"));
                break;
            }
        }

        playerStore.removeComponent(ref, CCAData.INSTANCE);
        playerStore.addComponent(ref, CCAData.INSTANCE, data);

        IEventDispatcher<LivingEntityInventoryChangeEvent, LivingEntityInventoryChangeEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(LivingEntityInventoryChangeEvent.class);

        if(dispatcher.hasListener()) {
            LivingEntityInventoryChangeEvent event = new LivingEntityInventoryChangeEvent(senderPlayer, senderPlayer.getInventory().getArmor(), ClearTransaction.EMPTY);
            dispatcher.dispatch(event);

            InterceptArmorEquipEvent.interceptEvent(ref, senderPlayer);
        }
    }
}
