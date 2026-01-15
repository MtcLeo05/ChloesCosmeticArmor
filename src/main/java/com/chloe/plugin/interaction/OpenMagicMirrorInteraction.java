package com.chloe.plugin.interaction;

import com.chloe.plugin.component.CCAData;
import com.chloe.plugin.gui.window.MagicMirrorMainWindow;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class OpenMagicMirrorInteraction extends SimpleInstantInteraction {

    public static BuilderCodec<OpenMagicMirrorInteraction> CODEC = BuilderCodec.<OpenMagicMirrorInteraction>builder(OpenMagicMirrorInteraction.class, OpenMagicMirrorInteraction::new)
        .build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType type, @NonNullDecl InteractionContext ctx, @NonNullDecl CooldownHandler cdHandler) {
        Ref<EntityStore> ref = ctx.getEntity();
        Store<EntityStore> store = ref.getStore();

        Player player = store.getComponent(ref, Player.getComponentType());
        CCAData data = store.getComponent(ref, CCAData.INSTANCE);

        if(player == null || data == null) return;

        PlayerRef playerRef = Universe.get().getPlayer(player.getDisplayName(), NameMatching.EXACT);

        player.getPageManager().setPageWithWindows(ref, store, Page.Bench, true, new MagicMirrorMainWindow(playerRef.getUuid()));
    }
}
