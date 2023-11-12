package com.reflexian.foxsmp.pets.helpers;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.reflexian.foxsmp.FoxSMP;
import com.reflexian.foxsmp.utilities.balloons.Balloon;
import com.reflexian.foxsmp.utilities.balloons.BalloonBlueprint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class BalloonImpl implements Balloon {

    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

//    private static final float WIND_Y = 0.1F;
    private final Player owner;
    private final BalloonBlueprint blueprint;

    private final BukkitTask task;

    private final int entityId = (int) (Math.random() * Integer.MAX_VALUE);
    private final UUID armorStandUUID = UUID.randomUUID();

    private boolean goingUp=false;
    private double amount = 0;

    public BalloonImpl(Player owner, BalloonBlueprint blueprint) {
        this.owner = owner;
        this.blueprint = blueprint;


        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) sendSpawnPacket(onlinePlayer);

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(FoxSMP.getInstance(), () -> {

            if (!owner.isOnline()) {
                kill();
                return;
            }

            double minimumHeight=getFutureLocation().getBlockY()+0.5;
            double maximumHeight=minimumHeight+0.8;

            if (goingUp) {
                if (getOwner().getLocation().getY() + amount >= maximumHeight) {
                    goingUp = false;
                } else {
                    amount+=0.035;
                }
            } else {
                if (getOwner().getLocation().getY() + amount <= minimumHeight) {
                    goingUp = true;
                } else {
                    amount-=0.035;
                }
            }

            update();
        }, 1L, 2L);
    }

    public void update() {

        try {
            Location loc = getFutureLocation();
            if (!owner.isOnline()) return;
            if (!loc.getChunk().isLoaded()||!loc.getChunk().equals(owner.getLocation().getChunk())) return;

//            slime.setLocation(loc.getX(), loc.getY()+amount+1-2, loc.getZ(), 0.0F, 0.0F);
//            as.setLocation(loc.getX(), loc.getY()-2+amount, loc.getZ(), owner.getLocation().getPitch(), 0.0f);


            teleportArmorStand(owner, loc.getY()-2+amount);
//            sendPacket(new PacketPlayOutEntityTeleport(slime));
//            sendPacket(new PacketPlayOutEntityTeleport(as));

            Location start = owner.getLocation().clone();
            start.add(0,.2,0);
            double d = getOwner().getLocation().clone().add(0,1.8,0).distance(start) / 20;
            Location end = getOwner().getLocation().clone().add(0,1.8,0);
            for (int i = 0; i < 20; i++) {

                Location l = start.clone();
                Vector direction = end.toVector().subtract(start.toVector()).normalize();
                Vector v = direction.multiply(i * d);
                l.add(v.getX(), v.getY(), v.getZ());

            }
        }catch (Exception e){}
    }


    private void sendPacket(PacketContainer packet) {
        protocolManager.sendServerPacket(owner, packet);
    }

    public void sendSpawnPacket(Player player) {
        Location loc = getFutureLocation().clone();

//        slime=new EntityZombie(((CraftWorld)owner.getWorld()).getHandle());
//        slime.setInvisible(true);
//        slime.setAI(false);
//        slime.setBaby(true);
//        slime.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
//
//        as = new EntityArmorStand(((CraftWorld)owner.getWorld()).getHandle());
//        as.setGravity(false);
//        as.setBasePlate(false);
//        as.setInvisible(true);
//        as.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0.0F, 0.0F);
//
//        sendPacket(new PacketPlayOutSpawnEntityLiving(slime),player);
//        sendPacket(new PacketPlayOutSpawnEntityLiving(as),player);
//        sendPacket(new PacketPlayOutEntityEquipment(as.getId(), 4, CraftItemStack.asNMSCopy(blueprint.getBalloonItem())),player);


        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet.getIntegers()
                .write(0, entityId)
                .write(1, 30);// entity ID
        packet.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        packet.getUUIDs()
                .write(0, armorStandUUID);
        packet.getDoubles()
                .write(0, player.getLocation().getX())
                .write(1, player.getLocation().getY() + 3)
                .write(2, player.getLocation().getZ());

//        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
//        dataWatcher.setEntity(player);
//        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.getVectorSerializer()), (byte) (0x20));
//        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.get(Boolean.class)), true);
//        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);
//        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(4, WrappedDataWatcher.Registry.getVectorSerializer()), (byte) (0x08));

        sendPacket(packet);

        PacketContainer equipmentPacket = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipmentPacket.getIntegers().write(0, entityId);
        equipmentPacket.getSlotStackPairLists().write(0, Collections.singletonList(
                new Pair<>(EnumWrappers.ItemSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET))));
        sendPacket(equipmentPacket);

    }

    public static Map<Player, BalloonImpl> balloons=new HashMap<>();

    public static void setBalloon(Player owner, BalloonBlueprint blueprint) {
        BalloonImpl balloonImpl = balloons.getOrDefault(owner,null);
        if (balloonImpl!=null) balloonImpl.kill();
        balloonImpl = new BalloonImpl(owner, blueprint);
        balloons.put(owner, balloonImpl);
    }

    @Override
    public Location getFutureLocation() {
        int mod = owner.getTicksLived() % 100;

//        if (mod == 0) wind.setY(-wind.getY());

        if (mod > 50)
            mod = 100 - mod;

        float percent = mod / 50.0F;
//        float windY = WIND_Y * percent;

//        if (wind.getY() < 0.0D)
//            wind.setY(-windY);
//        else
//            wind.setY(windY);
//
//        wind=new Vector();

        Location loc = owner.getLocation().clone();
        double angle = Math.toRadians(loc.getYaw() - 180.0F);
        loc = loc.add(Math.cos(angle), 2.5D, Math.sin(angle));//.add(wind)
        loc.setYaw(owner.getLocation().getYaw());
        return loc;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void kill() {
        task.cancel();
//        sendPacket(new PacketPlayOutEntityDestroy(as.getId()));
//        sendPacket(new PacketPlayOutEntityDestroy(slime.getId()));
    }

    @Override
    public void showFor(PacketType.Play player) {

    }

    @Override
    public void refresh() {
        kill();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            sendSpawnPacket(onlinePlayer);
        }
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

