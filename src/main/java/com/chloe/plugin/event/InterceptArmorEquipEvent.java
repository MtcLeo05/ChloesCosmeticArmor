package com.chloe.plugin.event;

import com.chloe.plugin.component.CCAData;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.protocol.packets.entities.EntityUpdates;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.receiver.IPacketReceiver;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class InterceptArmorEquipEvent implements IPacketReceiver {

    private final IPacketReceiver delegate;
    private final int netId;
    private final UUID playerUUID;

    public InterceptArmorEquipEvent(IPacketReceiver delegate, UUID playerUUID, int netId) {
        this.delegate = delegate;
        this.netId = netId;
        this.playerUUID = playerUUID;
    }

    public void write(@Nonnull Packet packet) {
        this.delegate.write(interceptPacket(packet));
    }

    public void writeNoCache(@Nonnull Packet packet) {
        this.delegate.writeNoCache(interceptPacket(packet));
    }

    private Packet interceptPacket(Packet packet) {
        if (!(packet instanceof EntityUpdates eu)) return packet;

        if (eu.updates == null || eu.updates.length == 0) return packet;

        PlayerRef worldRef = Universe.get().getPlayer(this.playerUUID);
        World world = Universe.get().getWorld(worldRef.getWorldUuid());

        boolean modified = false;
        EntityUpdate[] newEus = null;

        UUID target;

        for (int i = 0; i < eu.updates.length; i++) {
            EntityUpdate upd = eu.updates[i];
            if (upd == null) {
                continue;
            }

            if (upd.networkId != this.netId) {
                target = tryFindPlayer(upd.networkId, world);

                if(target == null) continue;
            } else {
                target = this.playerUUID;
            }

            CCAData data;
            PlayerRef playerRef = Universe.get().getPlayer(target);

            try {
                Ref<EntityStore> ref = playerRef.getReference();
                Store<EntityStore> store = ref.getStore();
                data = store.getComponent(ref, CCAData.INSTANCE);

                if (data == null) {
                    store.addComponent(ref, CCAData.INSTANCE, new CCAData());
                    data = store.getComponent(ref, CCAData.INSTANCE);
                }
            } catch (IllegalStateException _) {
                Holder<EntityStore> holder = playerRef.getHolder();
                if (holder == null) return packet;
                data = holder.getComponent(CCAData.INSTANCE);

                if (data == null) {
                    holder.addComponent(CCAData.INSTANCE, new CCAData());
                    data = holder.getComponent(CCAData.INSTANCE);
                }
            }

            Optional<String>[] armor = data.getReadyArmor();

            if (upd.updates == null || upd.updates.length == 0) {
                continue;
            }

            EntityUpdate newEu = null;

            for (int j = 0; j < upd.updates.length; j++) {
                ComponentUpdate cu = upd.updates[j];
                if (cu == null) {
                    continue;
                }

                if (cu.type != ComponentUpdateType.Equipment || cu.equipment == null) {
                    continue;
                }

                String[] armorIds = cu.equipment.armorIds;
                if (armorIds == null || armorIds.length == 0) {
                    continue;
                }
                int[] slots = {0, 1, 2, 3};

                if (!modified) {
                    newEus = eu.updates.clone();
                    modified = true;
                }

                if (newEu == null) {
                    newEu = new EntityUpdate();
                    newEu.networkId = upd.networkId;
                    newEu.removed = upd.removed;
                    newEu.updates = upd.updates.clone();
                    newEus[i] = newEu;
                }

                ComponentUpdate newCu = new ComponentUpdate();
                newCu.type = cu.type;
                Equipment editedEquipment = new Equipment();

                editedEquipment.rightHandItemId = cu.equipment.rightHandItemId;
                editedEquipment.leftHandItemId = cu.equipment.leftHandItemId;
                editedEquipment.armorIds = armorIds.clone();

                for (int slot : slots) {

                    if (armor[slot].isPresent()) {
                        editedEquipment.armorIds[slot] = armor[slot].get();
                    }

                }

                newCu.equipment = editedEquipment;
                newEu.updates[j] = newCu;
            }
        }

        if (!modified) return packet;

        EntityUpdates out = new EntityUpdates();
        out.removed = eu.removed;
        out.updates = newEus;
        return out;
    }

    public static void interceptEvent(Ref<EntityStore> ref, Player player) {
        if (ref == null) return;

        player.getWorld().execute(() -> {
            Store<EntityStore> store = ref.getStore();
            EntityTrackerSystems.EntityViewer viewer = store.getComponent(ref, EntityTrackerSystems.EntityViewer.getComponentType());

            if (viewer == null || viewer.packetReceiver == null) return;

            if (!(viewer.packetReceiver instanceof InterceptArmorEquipEvent)) {
                viewer.packetReceiver = new InterceptArmorEquipEvent(
                    viewer.packetReceiver,
                    player.getUuid(),
                    player.getNetworkId()
                );
            }

            player.invalidateEquipmentNetwork();
        });
    }

    public static void hookIntoEvent(LivingEntityInventoryChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        player.getWorld().execute(() -> {
            Ref<EntityStore> ref = player.getReference();

            InterceptArmorEquipEvent.interceptEvent(ref, player);
        });
    }

    private UUID tryFindPlayer(int networkId, World level) {
        try {
            for (Player player : level.getPlayers()) {
                if (player.getNetworkId() == networkId)
                    return player.getUuid();
            }

            return null;
        } catch (Throwable _) {
        }

        return null;
    }
}
