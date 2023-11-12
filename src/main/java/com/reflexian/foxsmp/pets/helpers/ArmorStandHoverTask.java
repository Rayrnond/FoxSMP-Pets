package com.reflexian.foxsmp.pets.helpers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.UUID;

public class ArmorStandHoverTask extends BukkitRunnable {
        private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        private double phase = 0.0;
        private final int entityId = (int) (Math.random() * Integer.MAX_VALUE);
        private final UUID armorStandUUID = UUID.randomUUID();
        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    // Calculate new Y position
                    double hoverHeight = 2.0 * Math.sin(phase);
                    phase += Math.PI / 20; // Adjust speed of hovering here
                    
                    if (phase <= Math.PI / 20) {
                        spawnArmorStand(player, hoverHeight);
                    } else {
                        teleportArmorStand(player, hoverHeight);
                    }
                    
                    if (phase >= 2 * Math.PI) {
                        phase = 0;
                    }
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void spawnArmorStand(Player player, double yOffset) throws InvocationTargetException {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            packet.getIntegers()
                    .write(0, entityId)
                    .write(1, 1); // ArmorStand entity type ID for Minecraft 1.20 - CHANGE THIS IF INCORRECT
            packet.getUUIDs()
                    .write(0, armorStandUUID);
            packet.getDoubles()
                    .write(0, player.getLocation().getX())
                    .write(1, player.getLocation().getY() + yOffset)
                    .write(2, player.getLocation().getZ());
            
            WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
            dataWatcher.setEntity(player);
            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.getVectorSerializer()), (byte) (0x20));
            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.get(Boolean.class)), true);
            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);
            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(4, WrappedDataWatcher.Registry.getVectorSerializer()), (byte) (0x08));
            
            packet.getDataWatcherModifier().write(0, dataWatcher);
            
            protocolManager.sendServerPacket(player, packet);
            
            // Set armor stand's helmet
            PacketContainer equipmentPacket = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
            equipmentPacket.getIntegers().write(0, entityId);
            equipmentPacket.getSlotStackPairLists().write(0, Collections.singletonList(
                    new Pair<>(EnumWrappers.ItemSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET))));
            protocolManager.sendServerPacket(player, equipmentPacket);
        }
        
        private void teleportArmorStand(Player player, double yOffset) throws InvocationTargetException {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
            packet.getIntegers().write(0, entityId);
            packet.getDoubles()
                    .write(0, player.getLocation().getX())
                    .write(1, player.getLocation().getY() + yOffset)
                    .write(2, player.getLocation().getZ());
            packet.getBooleans()
                    .write(0, false); // Set to false to indicate that it's not on the ground
            
            protocolManager.sendServerPacket(player, packet);
        }
    }