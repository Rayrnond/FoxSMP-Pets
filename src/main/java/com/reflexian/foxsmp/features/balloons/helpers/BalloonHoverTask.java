package com.reflexian.foxsmp.features.balloons.helpers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.reflexian.foxsmp.utilities.objects.ItemBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public class BalloonHoverTask extends BukkitRunnable {

    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    private final BalloonImpl impl;
    private final int entityId = (int) (Math.random() * Integer.MAX_VALUE);
    private final UUID armorStandUUID = UUID.randomUUID();

    private final Queue<Player> tempList = new ConcurrentLinkedQueue<>();
    private final Queue<Player> shownTo = new ConcurrentLinkedQueue<>(); // using players since querying Player for each UUID every 1 tick is not profitable
    // this adds extra responsibility to always be up-to date with players logging on/off, but it's worth it long-term.
    // also this is a queue so that it can be removed & queried in O(1) time

    private PacketContainer spawnPacket;
    private PacketContainer despawnPacket;

    private double phase = 0.0;


    public BalloonHoverTask(BalloonImpl impl) {
        this.impl = impl;

        despawnPacket= protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        despawnPacket.getIntegers().write(0, entityId);
        despawnPacket.getDoubles()
                .write(0, 0.0)
                .write(1, 0.0)
                .write(2, 0.0);
        despawnPacket.getBooleans()
                .write(0, false);

    }

    public boolean toggleAllHide() {
        if (tempList.isEmpty()) {
            tempList.addAll(shownTo);
            for (Player player : shownTo) {
                removeShownTo(player);
            }
            return true;
        } else {
            for (Player player : tempList) {
                addShownTo(player);
            }
            tempList.clear();
            return false;
        }
    }

    public void addShownTo(Player player) {
        shownTo.add(player);
        spawnArmorStand(player, calculatePetLocation(impl.getOwner().getLocation()).getY());
    }

    public void removeShownTo(Player player) {
        shownTo.remove(player);
        despawnArmorStand(player);
    }

    public void kill() {
        this.cancel();
        for (Player player : shownTo) {
            if (player == null || !player.isOnline()) continue;
            despawnArmorStand(player);
        }
        shownTo.clear();
    }

    @Override
    public void run() {
        if (Bukkit.isPrimaryThread()) {
            this.cancel();
            throw new RuntimeException("BalloonHoverTask cannot be ran on the main thread!");
        }
        try {
            double hoverHeight = Math.sin(phase);
            phase += Math.PI / 60; // Adjust speed of hovering here

            Location loc = calculatePetLocation(impl.getOwner().getLocation());
            teleportArmorStand(loc,hoverHeight);

            if (phase >= 2 * Math.PI) {
                phase = 0;
            }
        } catch (InvocationTargetException e) {
            impl.kill();
            e.printStackTrace();
        }
    }

    private void despawnArmorStand(Player player) {
        protocolManager.sendServerPacket(player, despawnPacket);
    }

    private void spawnArmorStand(Player player, double yOffset) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        packet.getIntegers()
                .write(0, entityId);
        packet.getIntegers().write(1, 78);
        packet.getUUIDs()
                .write(0, armorStandUUID);
        packet.getDoubles()
                .write(0, player.getLocation().getX())
                .write(1, player.getLocation().getY() + yOffset)
                .write(2, player.getLocation().getZ());


        protocolManager.sendServerPacket(player, packet);

        packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, entityId);

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        dataWatcher.setEntity(player);
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
        dataWatcher.setObject(0, serializer, (byte) (0x20 | 0x40)); // invisible, glowing
        dataWatcher.setObject(15, serializer, (byte) (0x01 | 0x08 | 0x10)); // small, baseplate, marker

        final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
        for (final WrappedWatchableObject entry : dataWatcher.getWatchableObjects()) {
            if (entry == null) continue;
            final WrappedDataWatcher.WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
            wrappedDataValueList.add(new WrappedDataValue(watcherObject.getIndex(), watcherObject.getSerializer(), entry.getRawValue()));
        }

        packet.getDataValueCollectionModifier().write(0, wrappedDataValueList);

        protocolManager.sendServerPacket(player, packet);

        // Set armor stand's helmet
        PacketContainer equipmentPacket = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipmentPacket.getIntegers().write(0, entityId);
        equipmentPacket.getSlotStackPairLists().write(0, Collections.singletonList(
                new Pair<>(EnumWrappers.ItemSlot.HEAD, new ItemBuilder(Material.AIR).setSkull(impl.getSkin()).build())));
        protocolManager.sendServerPacket(player, equipmentPacket);
    }

    private void teleportArmorStand(Location loc, double yOffset) throws InvocationTargetException {

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entityId);
        packet.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY() + yOffset + 0.8)
                .write(2, loc.getZ());
        packet.getBooleans()
                .write(0, false);

        for (Player player : shownTo) {
            protocolManager.sendServerPacket(player, packet);
        }
    }


    // works great
    private Location calculatePetLocation(Location playerLocation) {
        Location loc = playerLocation.clone();
        double angle = Math.toRadians(loc.getYaw() - 180.0F);
        loc = loc.add(Math.cos(angle), 0, Math.sin(angle)).add(new Vector());
        return loc;
    }
}