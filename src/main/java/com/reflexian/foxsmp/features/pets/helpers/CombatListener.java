package com.reflexian.foxsmp.features.pets.helpers;

import com.reflexian.foxsmp.features.pets.list.NorthernNomadPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import me.lucko.helper.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class CombatListener {
    public CombatListener(){
        Events.subscribe(EntityDamageByEntityEvent.class)
                .handler(event -> {
                    if (event.getEntity() instanceof Player) {
                        PlayerData playerData = PlayerData.map.getOrDefault(event.getDamager().getUniqueId(),null);
                        if (playerData == null) return;
                        if (playerData.hasPet() && playerData.getPet() instanceof NorthernNomadPet) {
                            NorthernNomadPet pet = (NorthernNomadPet) playerData.getPet();
                            pet.updateSpeed((Player) event.getDamager(), true);
                        }
                    }
                });
    }
}
