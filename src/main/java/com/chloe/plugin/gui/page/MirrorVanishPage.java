package com.chloe.plugin.gui.page;

import com.chloe.plugin.component.CCAData;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.ItemArmorSlot;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class MirrorVanishPage extends InteractiveCustomUIPage<MirrorVanishPage.VanishData> {

    public MirrorVanishPage(@NonNullDecl PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, VanishData.CODEC);
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder cmd, @NonNullDecl UIEventBuilder evt, @NonNullDecl Store<EntityStore> store) {
        CCAData ccData = store.getComponent(ref, CCAData.INSTANCE);
        if (ccData == null) return;

        cmd.append("Pages/CCA_VanishPage.ui");

        evt.addEventBinding(CustomUIEventBindingType.ValueChanged, "#VanishHead #CheckBox", EventData.of("VanishSlot", ItemArmorSlot.Head.name()));
        evt.addEventBinding(CustomUIEventBindingType.ValueChanged, "#VanishChest #CheckBox", EventData.of("VanishSlot", ItemArmorSlot.Chest.name()));
        evt.addEventBinding(CustomUIEventBindingType.ValueChanged, "#VanishHands #CheckBox", EventData.of("VanishSlot", ItemArmorSlot.Hands.name()));
        evt.addEventBinding(CustomUIEventBindingType.ValueChanged, "#VanishLegs #CheckBox", EventData.of("VanishSlot", ItemArmorSlot.Legs.name()));

        cmd.set("#VanishHead #CheckBox.Value", ccData.headV());
        cmd.set("#VanishChest #CheckBox.Value", ccData.chestV());
        cmd.set("#VanishHands #CheckBox.Value", ccData.handsV());
        cmd.set("#VanishLegs #CheckBox.Value", ccData.legsV());
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, @NonNullDecl VanishData data) {
        CCAData ccData = store.getComponent(ref, CCAData.INSTANCE);
        if (ccData == null) return;

        switch (data.vanishSlot) {
            case "Head":
                ccData.vanishHead();
                break;
            case "Chest":
                ccData.vanishChest();
                break;
            case "Hands":
                ccData.vanishHands();
                break;
            case "Legs":
                ccData.vanishLegs();
                break;
        }

        Universe.get().getPlayers().forEach(pRef -> {
            Ref<EntityStore> ref1 = pRef.getReference();
            Store<EntityStore> store1 = ref1.getStore();

            store1.getComponent(ref1, Player.getComponentType()).invalidateEquipmentNetwork();
        });

        this.sendUpdate();
    }

    public static class VanishData {
        public static final BuilderCodec<VanishData> CODEC = BuilderCodec.builder(VanishData.class, VanishData::new)
            .append(new KeyedCodec<>("VanishSlot", BuilderCodec.STRING), (data, value) -> data.vanishSlot = value, (data) -> data.vanishSlot)
            .add()
            .build();

        public String vanishSlot;
    }
}
