package com.chloe.plugin.gui.window;

import com.chloe.plugin.component.CCAData;
import com.chloe.plugin.event.InterceptArmorEquipEvent;
import com.google.gson.JsonObject;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.protocol.packets.window.WindowType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ItemContainerWindow;
import com.hypixel.hytale.server.core.entity.entities.player.windows.Window;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ClearTransaction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class MagicMirrorMainWindow extends Window implements ItemContainerWindow {

    @Nonnull
    private final JsonObject windowData = new JsonObject();
    @Nonnull
    private final ItemContainer itemContainer;
    @Nullable
    private EventRegistration eventRegistration;

    public MagicMirrorMainWindow(UUID playerUUID) {
        super(WindowType.Container);

        Ref<EntityStore> ref = Universe.get().getPlayer(playerUUID).getReference();
        Store<EntityStore> store = ref.getStore();
        CCAData data = store.getComponent(ref, CCAData.INSTANCE);

        if(data == null) {
            store.addComponent(ref, CCAData.INSTANCE, new CCAData());
            data = store.getComponent(ref, CCAData.INSTANCE);
        }

        itemContainer = data.getArmor();
    }

    @NonNullDecl
    @Override
    public JsonObject getData() {
        return windowData;
    }

    @Override
    protected boolean onOpen0() {
        this.eventRegistration = this.itemContainer.registerChangeEvent((event) -> {
            IEventDispatcher<LivingEntityInventoryChangeEvent, LivingEntityInventoryChangeEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(LivingEntityInventoryChangeEvent.class);

            PlayerRef playerRef = getPlayerRef();
            Ref<EntityStore> ref = playerRef.getReference();
            Store<EntityStore> store = ref.getStore();

            Player player = store.getComponent(ref, Player.getComponentType());

            if(dispatcher.hasListener()) {
                LivingEntityInventoryChangeEvent newEv = new LivingEntityInventoryChangeEvent(player, player.getInventory().getArmor(), ClearTransaction.EMPTY);
                dispatcher.dispatch(newEv);

                InterceptArmorEquipEvent.interceptEvent(ref, player);
            }
        });

        return true;
    }

    @Override
    protected void onClose0() {
        this.eventRegistration.unregister();
        this.eventRegistration = null;
    }

    @NonNullDecl
    @Override
    public ItemContainer getItemContainer() {
        return itemContainer;
    }
}
