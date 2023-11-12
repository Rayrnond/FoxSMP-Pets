package com.reflexian.foxsmp.pets.helpers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ArmorStandHoverTask extends BukkitRunnable {
        private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        private double phase = 0.0;
        private final int entityId = (int) (Math.random() * Integer.MAX_VALUE);
        private final UUID armorStandUUID = UUID.randomUUID();

        private final List<UUID> players = new LinkedList<>();

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    if (!players.contains(player.getUniqueId())) {
                        players.add(player.getUniqueId());
                        spawnArmorStand(player, 0.0);
                    }
                    // Calculate new Y position
                    double hoverHeight = 2.0 * Math.sin(phase);
                    phase += Math.PI / 20; // Adjust speed of hovering here
                    teleportArmorStand(player, hoverHeight);
                    
                    if (phase >= 2 * Math.PI) {
                        phase = 0;
                    }
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void spawnArmorStand(Player player, double yOffset) throws InvocationTargetException {
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
            dataWatcher.setObject(15, serializer,(byte) (0x01 | 0x08 | 0x10));

            if(protocolManager.getProtocolVersion(player) >= 16) {
                final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();



                for(final WrappedWatchableObject entry : dataWatcher.getWatchableObjects()) {
                    if(entry == null) continue;

                    final WrappedDataWatcher.WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
                    wrappedDataValueList.add(
                            new WrappedDataValue(
                                    watcherObject.getIndex(),
                                    watcherObject.getSerializer(),
                                    entry.getRawValue()
                            )
                    );
                }

                packet.getDataValueCollectionModifier().write(0, wrappedDataValueList);
            }
            
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