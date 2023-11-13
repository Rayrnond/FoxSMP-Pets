package com.reflexian.foxsmp.features.pets.helpers;

import com.reflexian.foxsmp.features.pets.list.NorthernNomadPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatTask extends BukkitRunnable {

    public static ConcurrentHashMap<UUID,Long> INCOMBAT = new ConcurrentHashMap<>();

    @Override
    public void run() {
        for (UUID uuid : INCOMBAT.keySet()) {
            System.out.println("CHECKING...");
            if (System.currentTimeMillis() - INCOMBAT.get(uuid) > 5000) {
                INCOMBAT.remove(uuid);
                System.out.println("1");
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) continue;
                System.out.println("2");
                PlayerData playerData = PlayerData.map.getOrDefault(player.getUniqueId(),null);
                if (playerData == null) continue;
                System.out.println("3");
                if (playerData.hasPet() && playerData.getPet() instanceof NorthernNomadPet pet) {
                    pet.updateSpeed(player, false);
                }
                System.out.println("4");
                if (playerData.hasPet()) {
                    playerData.getPet().getBalloon().getTask().toggleAllHide();
                    System.out.println("5");
                }
            }
        }
    }
}
