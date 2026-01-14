package com.chloe.plugin.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.protocol.ItemArmorSlot;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;
import java.util.Optional;

public class CCAData implements Component<EntityStore> {

    public static ComponentType<EntityStore, CCAData> INSTANCE;

    public static final BuilderCodec<CCAData> CODEC = BuilderCodec.builder(CCAData.class, CCAData::new).append(new KeyedCodec<>("Head", SimpleItemContainer.CODEC), (data, value) -> data.head = value, (data) -> data.head).add().append(new KeyedCodec<>("Chest", SimpleItemContainer.CODEC), (data, value) -> data.chest = value, (data) -> data.chest).add().append(new KeyedCodec<>("Hands", SimpleItemContainer.CODEC), (data, value) -> data.hands = value, (data) -> data.hands).add().append(new KeyedCodec<>("Legs", SimpleItemContainer.CODEC), (data, value) -> data.legs = value, (data) -> data.legs).add().build();

    private SimpleItemContainer head, chest, hands, legs;
    private boolean headV, chestV,  handsV, legsV;

    public CCAData() {
        head = new SimpleItemContainer((short) 1);
        chest = new SimpleItemContainer((short) 1);
        hands = new SimpleItemContainer((short) 1);
        legs = new SimpleItemContainer((short) 1);

        head.setSlotFilter(FilterActionType.ADD, (short) 0, (t, s, c, i) -> i.getItem().getArmor().getArmorSlot() == ItemArmorSlot.Head);
        chest.setSlotFilter(FilterActionType.ADD, (short) 0, (t, s, c, i) -> i.getItem().getArmor().getArmorSlot() == ItemArmorSlot.Chest);
        hands.setSlotFilter(FilterActionType.ADD, (short) 0, (t, s, c, i) -> i.getItem().getArmor().getArmorSlot() == ItemArmorSlot.Hands);
        legs.setSlotFilter(FilterActionType.ADD, (short) 0, (t, s, c, i) -> i.getItem().getArmor().getArmorSlot() == ItemArmorSlot.Legs);

        headV = false;
        chestV = false;
        handsV = false;
        legsV = false;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        CCAData copy = new CCAData();

        copy.head = this.head;
        copy.chest = this.chest;
        copy.hands = this.hands;
        copy.legs = this.legs;

        return copy;
    }

    public ItemStack setHead(ItemStack item) {
        return head.setItemStackForSlot((short) 0, item).getRemainder();
    }

    public ItemStack setChest(ItemStack item) {
        return chest.setItemStackForSlot((short) 0, item).getRemainder();
    }

    public ItemStack setHands(ItemStack item) {
        return hands.setItemStackForSlot((short) 0, item).getRemainder();
    }

    public ItemStack setLegs(ItemStack item) {
        return legs.setItemStackForSlot((short) 0, item).getRemainder();
    }

    public boolean vanishHead() {
        headV = !headV;
        return headV;
    }

    public boolean vanishChest() {
        chestV = !chestV;
        return chestV;
    }

    public boolean vanishHands() {
        handsV = !handsV;
        return handsV;
    }

    public boolean vanishLegs() {
        legsV = !legsV;
        return legsV;
    }

    @Nullable
    public ItemStack getHead() {
        return head.getItemStack((short) 0);
    }

    @Nullable
    public ItemStack getChest() {
        return chest.getItemStack((short) 0);
    }

    @Nullable
    public ItemStack getHands() {
        return hands.getItemStack((short) 0);
    }

    @Nullable
    public ItemStack getLegs() {
        return legs.getItemStack((short) 0);
    }

    public void clearHead() {
        head.clear();
    }

    public void clearChest() {
        chest.clear();
    }

    public void clearHands() {
        hands.clear();
    }

    public void clearLegs() {
        legs.clear();
    }

    public Optional<String>[] getReadyArmor() {
        ItemStack head = getHead();
        ItemStack chest = getChest();
        ItemStack hands = getHands();
        ItemStack legs = getLegs();

        Optional<String> headS = Optional.ofNullable(headV? "": head == null || head.isEmpty() ? null : head.getItemId());
        Optional<String> chestS = Optional.ofNullable(chestV? "": chest == null || chest.isEmpty() ? null : chest.getItemId());
        Optional<String> handsS = Optional.ofNullable(handsV? "": hands == null || hands.isEmpty() ? null : hands.getItemId());
        Optional<String> legsS = Optional.ofNullable(legsV? "": legs == null || legs.isEmpty() ? null : legs.getItemId());

        return new Optional[]{headS, chestS, handsS, legsS};
    }
}
