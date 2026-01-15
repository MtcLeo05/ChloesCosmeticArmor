package com.chloe.plugin.component;

import com.google.gson.JsonElement;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.protocol.ItemArmorSlot;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.*;
import com.hypixel.hytale.server.core.inventory.container.filter.*;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;
import java.util.Optional;

public class CCAData implements Component<EntityStore> {

    public static ComponentType<EntityStore, CCAData> INSTANCE;

    public static final BuilderCodec<CCAData> CODEC = BuilderCodec.builder(CCAData.class, CCAData::new)
        .append(new KeyedCodec<>("CCA_CosmeticArmor", SimpleItemContainer.CODEC), (data, value) -> {
            data.armor = value;
            applyArmorFilters(data.armor);
        }, (data) -> data.armor)
        .add()
        .build();

    private SimpleItemContainer armor;

    private boolean headV, chestV, handsV, legsV;

    public CCAData() {
        armor = new SimpleItemContainer((short) 4);

        applyArmorFilters(armor);

        headV = false;
        chestV = false;
        handsV = false;
        legsV = false;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        CCAData copy = new CCAData();

        copy.armor = this.armor;

        applyArmorFilters(copy.armor);

        copy.headV = this.headV;
        copy.chestV = this.chestV;
        copy.handsV = this.handsV;
        copy.legsV = this.legsV;

        return copy;
    }

    //Game seems to often forget that the armor has filters, so I simply re-apply them every occasion
    private static void applyArmorFilters(SimpleItemContainer armor) {
        ItemArmorSlot[] itemArmorSlots = ItemArmorSlot.VALUES;

        for (short i = 0; i < armor.getCapacity(); ++i) {
            if (i < itemArmorSlots.length) {
                if (i < 5) {
                    armor.setSlotFilter(FilterActionType.ADD, i, new ArmorSlotAddFilter(itemArmorSlots[i]));
                } else {
                    armor.setSlotFilter(FilterActionType.ADD, i, new NoDuplicateFilter(armor));
                }
            } else {
                armor.setSlotFilter(FilterActionType.ADD, i, SlotFilter.DENY);
            }
        }
    }

    public boolean headV() {
        return headV;
    }

    public boolean chestV() {
        return chestV;
    }

    public boolean handsV() {
        return handsV;
    }

    public boolean legsV() {
        return legsV;
    }

    public ItemStack setHead(ItemStack item) {
        return armor.setItemStackForSlot((short) 0, item).getRemainder();
    }

    public ItemStack setChest(ItemStack item) {
        return armor.setItemStackForSlot((short) 1, item).getRemainder();
    }

    public ItemStack setHands(ItemStack item) {
        return armor.setItemStackForSlot((short) 2, item).getRemainder();
    }

    public ItemStack setLegs(ItemStack item) {
        return armor.setItemStackForSlot((short) 3, item).getRemainder();
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
        return armor.getItemStack((short) 0);
    }

    @Nullable
    public ItemStack getChest() {
        return armor.getItemStack((short) 1);
    }

    @Nullable
    public ItemStack getHands() {
        return armor.getItemStack((short) 2);
    }

    @Nullable
    public ItemStack getLegs() {
        return armor.getItemStack((short) 3);
    }

    public void clearHead() {
        armor.setItemStackForSlot((short) 0, ItemStack.EMPTY);
    }

    public void clearChest() {
        armor.setItemStackForSlot((short) 1, ItemStack.EMPTY);
    }

    public void clearHands() {
        armor.setItemStackForSlot((short) 2, ItemStack.EMPTY);
    }

    public void clearLegs() {
        armor.setItemStackForSlot((short) 3, ItemStack.EMPTY);
    }

    public Optional<String>[] getReadyArmor() {
        ItemStack head = getHead();
        ItemStack chest = getChest();
        ItemStack hands = getHands();
        ItemStack legs = getLegs();

        Optional<String> headS = Optional.ofNullable(headV ? "" : head == null || head.isEmpty() ? null : head.getItemId());
        Optional<String> chestS = Optional.ofNullable(chestV ? "" : chest == null || chest.isEmpty() ? null : chest.getItemId());
        Optional<String> handsS = Optional.ofNullable(handsV ? "" : hands == null || hands.isEmpty() ? null : hands.getItemId());
        Optional<String> legsS = Optional.ofNullable(legsV ? "" : legs == null || legs.isEmpty() ? null : legs.getItemId());

        return new Optional[]{headS, chestS, handsS, legsS};
    }

    public ItemContainer getArmor() {
        return armor;
    }
}
