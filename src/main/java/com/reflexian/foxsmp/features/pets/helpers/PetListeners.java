package com.reflexian.foxsmp.features.pets.helpers;

import com.reflexian.foxsmp.FoxSMP;
import com.reflexian.foxsmp.features.balloons.helpers.BalloonImpl;
import com.reflexian.foxsmp.features.inventories.JourneyCrystalGUI;
import com.reflexian.foxsmp.features.pets.SMPPet;
import com.reflexian.foxsmp.features.pets.list.AvalancheArtisanPet;
import com.reflexian.foxsmp.features.pets.list.GlacialGuardianPet;
import com.reflexian.foxsmp.features.pets.list.NorthernNomadPet;
import com.reflexian.foxsmp.features.pets.list.PolarExplorerPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PetListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(FoxSMP.getInstance(),()->{
            PlayerData playerData = PlayerData.load(event.getPlayer().getUniqueId());
            PlayerData.map.put(event.getPlayer().getUniqueId(), playerData);
            Bukkit.getScheduler().scheduleSyncDelayedTask(FoxSMP.getInstance(), () -> {
                if (playerData.hasPet() && playerData.getPet() instanceof NorthernNomadPet pet) {
                    pet.updateSpeed(event.getPlayer(), false);
                } else if (playerData.getPet() == null || !playerData.hasPet()) {
                    new JourneyCrystalGUI().init(event.getPlayer());
                }

                for (BalloonImpl value : BalloonImpl.balloons.values()) {
                    value.showFor(event.getPlayer());
                }
            }, 40L);

        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (BalloonImpl value : BalloonImpl.balloons.values()) {
            value.hideFor(event.getPlayer());
        }

        PlayerData playerData = PlayerData.map.getOrDefault(event.getPlayer().getUniqueId(),null);
        if (playerData == null) return;
        if (playerData.hasPet()) {
            playerData.getPet().getBalloon().kill();
        }
        PlayerData.save(playerData);
        PlayerData.map.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onSwitchWorld(PlayerChangedWorldEvent event) {
        recentSwaps.add(event.getPlayer().getUniqueId());
    }

    private final List<UUID> recentSwaps = new ArrayList<>();
    @EventHandler
    public void onWalk(PlayerMoveEvent event) {
        if (recentSwaps.contains(event.getPlayer().getUniqueId())) {
            recentSwaps.remove(event.getPlayer().getUniqueId());
            PlayerData playerData = PlayerData.map.getOrDefault(event.getPlayer().getUniqueId(),null);
            if (playerData == null || playerData.getPet() == null) return;
            playerData.getPet().getBalloon().getTask().respawn();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (isPlayerInPveZone(player)) {
                return;
            }

            final PlayerData playerData = PlayerData.map.getOrDefault(player.getUniqueId(), null);
            if (playerData == null) return;
            boolean hasPet = playerData.hasPet();
            if (!hasPet) return;
            if (!(playerData.getPet() instanceof GlacialGuardianPet)) return;
            double buff = (1 + 0.29 * playerData.getPet().getLevel())/100; // 1% at level 0, 30% at level 100
            double reduction = 1 - buff;

            event.setDamage(event.getDamage() * reduction);
        }
    }


    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (!isPlayerInPveZone(player)) {
                return;
            }
            final PlayerData playerData = PlayerData.map.getOrDefault(player.getUniqueId(), null);
            if (playerData == null) return;
            boolean hasPet = playerData.hasPet();
            if (!hasPet) return;
            if (!(playerData.getPet() instanceof AvalancheArtisanPet)) return;
            double buff = (1 + 0.29 * playerData.getPet().getLevel())/100; // 1% at level 0, 30% at level 100
            double multiplier = 1 + buff;
            event.setDamage(event.getDamage() * multiplier);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!event.getBlock().getType().name().endsWith("ORE")) return;
        final PlayerData playerData = PlayerData.map.getOrDefault(player.getUniqueId(), null);
        if (playerData == null) return;
        boolean hasPet = playerData.hasPet();
        if (!hasPet) return;
        if (!(playerData.getPet() instanceof PolarExplorerPet)) return;
        double buff = 1 + 0.29 * playerData.getPet().getLevel(); // 1% at level 0, 15% at level 100
        boolean proc = new Random().nextDouble() < (buff / 100);
        if (proc) {
            for (ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand(), player)) {
                event.getPlayer().getWorld().dropItem(event.getBlock().getLocation(), drop);
            }
        }
    }

    private boolean isPlayerInPveZone(Player player) {
        WorldGuard worldGuard = WorldGuard.getInstance();
        RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        Location playerLocation = player.getLocation();
        Set<ProtectedRegion> regions = query.getApplicableRegions(BukkitAdapter.adapt(playerLocation)).getRegions();
        for (ProtectedRegion region : regions) {
            StateFlag pveZoneFlag = FoxSMP.getInstance().getPveZoneFlag();
            if (region.getFlag(pveZoneFlag) != null && region.getFlag(pveZoneFlag).equals(StateFlag.State.ALLOW)) {
                return true;
            }
        }
        return false;
    }

}
