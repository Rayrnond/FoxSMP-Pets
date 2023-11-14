package com.reflexian.foxsmp.features.pets.helpers;

import com.reflexian.foxsmp.FoxSMP;
import com.reflexian.foxsmp.features.balloons.helpers.BalloonImpl;
import com.reflexian.foxsmp.features.candy.PetCandyItem;
import com.reflexian.foxsmp.features.inventories.JourneyCrystalGUI;
import com.reflexian.foxsmp.features.pets.SMPPet;
import com.reflexian.foxsmp.features.pets.list.AvalancheArtisanPet;
import com.reflexian.foxsmp.features.pets.list.GlacialGuardianPet;
import com.reflexian.foxsmp.features.pets.list.NorthernNomadPet;
import com.reflexian.foxsmp.features.pets.list.PolarExplorerPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.lucko.helper.Events;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PetListeners {

    public PetListeners() {

        Events.subscribe(PlayerJoinEvent.class)
                .handler(event -> {

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
                });

        Events.subscribe(PlayerQuitEvent.class)
                        .handler(event -> {
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
                        });


        Events.subscribe(EntityDamageEvent.class)
                .handler(event -> {

                    if (event.getEntity() instanceof Player) {
                        Player player = (Player) event.getEntity();
                        if (isPlayerInPveZone(player)) {
                            return;
                        }

                        final PlayerData playerData = PlayerData.map.getOrDefault(player.getUniqueId(), null);
                        boolean hasPet = playerData.hasPet();
                        if (!hasPet) return;
                        if (!(playerData.getPet() instanceof GlacialGuardianPet pet)) return;
                        double buff = 1 + 0.29 * playerData.getPet().getLevel(); // 1% at level 0, 30% at level 100
                        double reduction = 1 - buff;

                        event.setDamage(event.getDamage() * reduction);
                    }

                });


        // avalanche artisan
        Events.subscribe(EntityDamageByEntityEvent.class)
                .handler(event -> {

                    if (event.getDamager() instanceof Player) {
                        Player player = (Player) event.getDamager();
                        if (!isPlayerInPveZone(player)) {
                            return;
                        }
                        final PlayerData playerData = PlayerData.map.getOrDefault(player.getUniqueId(), null);
                        boolean hasPet = playerData.hasPet();
                        if (!hasPet) return;
                        if (!(playerData.getPet() instanceof AvalancheArtisanPet pet)) return;
                        double buff = 1 + 0.29 * playerData.getPet().getLevel(); // 1% at level 0, 30% at level 100
                        double multiplier = 1 + buff;
                        event.setDamage(event.getDamage() * multiplier);
                    }

                });

        // polar explorer
        Events.subscribe(BlockBreakEvent.class)
                .handler(event -> {
                    Player player = event.getPlayer();

                    if (!event.getBlock().getType().name().endsWith("ORE")) return;
                    final PlayerData playerData = PlayerData.map.getOrDefault(player.getUniqueId(), null);
                    boolean hasPet = playerData.hasPet();
                    if (!hasPet) return;
                    if (!(playerData.getPet() instanceof PolarExplorerPet)) return;
                    double buff = 1 + 0.29 * playerData.getPet().getLevel(); // 1% at level 0, 15% at level 100
                    boolean proc = new Random().nextDouble() < buff;
                    if (proc) {
                        for (ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand(), player)) {
                            event.getPlayer().getWorld().dropItem(event.getBlock().getLocation(), drop);
                        }
                    }
                });
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
