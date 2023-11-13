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
            if (System.currentTimeMillis() - INCOMBAT.get(uuid) > 5000) {
                INCOMBAT.remove(uuid);

                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) continue;
                PlayerData playerData = PlayerData.map.getOrDefault(player.getUniqueId(),null);
                if (playerData == null) return;
                if (playerData.hasPet() && playerData.getPet() instanceof NorthernNomadPet pet) {
                    pet.updateSpeed(player, false);
                }
                if (playerData.hasPet()) {
                    playerData.getPet().getBalloon().getTask().toggleAllHide();
                }
            }
        }
    }
}
