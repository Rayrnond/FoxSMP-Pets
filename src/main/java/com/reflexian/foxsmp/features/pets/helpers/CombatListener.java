package com.reflexian.foxsmp.features.pets.helpers;

import com.reflexian.foxsmp.features.pets.list.NorthernNomadPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import nl.marido.deluxecombat.api.DeluxeCombatAPI;
import nl.marido.deluxecombat.events.CombatStateChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatListener implements Listener {

    /*
    @EventHandler
    public void onDamageByEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            PlayerData playerData = PlayerData.map.getOrDefault(event.getDamager().getUniqueId(),null);
            if (playerData == null) return;
            if (CombatTask.INCOMBAT.containsKey(playerData.getUuid())) return;
            if (playerData.hasPet() && playerData.getPet().getBalloon().getTask().getTempList().isEmpty()) {
                playerData.getPet().getBalloon().getTask().toggleAllHide();
            }
            CombatTask.INCOMBAT.put(playerData.getUuid(),System.currentTimeMillis());
            if (playerData.hasPet() && playerData.getPet() instanceof NorthernNomadPet pet) {
                pet.updateSpeed((Player) event.getDamager(), true);
            }
        }
    }
     */

    @EventHandler
    public void onCombatStateChange(CombatStateChangeEvent event) {
        PlayerData playerData = PlayerData.map.getOrDefault(event.getPlayer().getUniqueId(),null);
        if (playerData==null || !playerData.hasPet()) return;

        CombatStateChangeEvent.CombatState state = event.getState();
        if (state == CombatStateChangeEvent.CombatState.TAGGED) {
            if (playerData.getPet().getBalloon().getTask().isHidden()) return; // pet is HIDDEN

            if (playerData.hasPet() && playerData.getPet() instanceof NorthernNomadPet pet) {
                pet.updateSpeed(event.getPlayer(), true);
            }
            playerData.getPet().getBalloon().getTask().toggleAllHide();
        } else {
            if (!playerData.getPet().getBalloon().getTask().isHidden()) return;
            if (playerData.hasPet() && playerData.getPet() instanceof NorthernNomadPet pet) {
                pet.updateSpeed(event.getPlayer(), false);
            }
            playerData.getPet().getBalloon().getTask().toggleAllHide();
        }

    }

}
