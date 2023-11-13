package com.reflexian.foxsmp.features.pets.helpers;

import com.reflexian.foxsmp.features.pets.list.NorthernNomadPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import me.lucko.helper.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatListener {
    public CombatListener(){
        Events.subscribe(EntityDamageByEntityEvent.class)
                .handler(event -> {
                    if (event.getEntity() instanceof Player) {
                        PlayerData playerData = PlayerData.map.getOrDefault(event.getDamager().getUniqueId(),null);
                        if (playerData == null) return;
                        if (CombatTask.INCOMBAT.containsKey(playerData.getUuid())) return;
                        if (playerData.hasPet()) {
                            playerData.getPet().getBalloon().getTask().toggleAllHide();
                        }
                        CombatTask.INCOMBAT.put(playerData.getUuid(),System.currentTimeMillis());
                        if (playerData.hasPet() && playerData.getPet() instanceof NorthernNomadPet pet) {
                            pet.updateSpeed((Player) event.getDamager(), true);
                        }
                    }
                });
    }
}
