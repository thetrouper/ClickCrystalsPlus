package io.github.itzispyder.clickcrystals.modules.modules.chat;

import io.github.itzispyder.clickcrystals.events.EventHandler;
import io.github.itzispyder.clickcrystals.events.Listener;
import io.github.itzispyder.clickcrystals.events.events.PacketReceiveEvent;
import io.github.itzispyder.clickcrystals.modules.Categories;
import io.github.itzispyder.clickcrystals.modules.Module;
import io.github.itzispyder.clickcrystals.util.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

import java.util.HashMap;
import java.util.Map;

public class PopCounter extends Module implements Listener {

    private static final Map<String,Integer> totemPops = new HashMap<>();

    public PopCounter() {
        super("PopCounter", Categories.CHAT, "Send messages when a player pops their totem.");
    }

    @Override
    protected void onEnable() {
        system.addListener(this);
    }

    @Override
    protected void onDisable() {
        system.removeListener(this);
    }

    @EventHandler
    private void onReceiveStatus(PacketReceiveEvent e) {
        if (e.getPacket() instanceof EntityStatusS2CPacket packet) {
            final Entity ent = packet.getEntity(mc.player.getWorld());
            if (ent == null) return;
            if (ent.getType() != EntityType.PLAYER) return;
            String name = ent.getDisplayName().getString();
            name = name.equals(mc.player.getDisplayName().getString()) ? "§6§oYou" : name;

            switch (packet.getStatus()) {
                case 35 -> {
                    setPops(ent,getPops(ent) + 1);
                    ChatUtils.sendPrefixMessage("§b§l" + name + "§7§o popped §8[§e" + getPops(ent) + "§8]§7§o totems.");
                }
                case 3 -> {
                    ChatUtils.sendPrefixMessage("§b§l" + name + "§7§o died after popping §8[§6" + getPops(ent) + "§8]§7§o totems!");
                    setPops(ent,0);
                }
            }
        }
    }

    private int getPops(Entity ent) {
        return getOrDefault(totemPops.get(ent.getDisplayName().getString()),0);
    }

    private void setPops(Entity ent, int pops) {
        totemPops.put(ent.getDisplayName().getString(), pops);
    }

    private <T> T getOrDefault(T value, T def) {
        return value != null ? value : def;
    }
}