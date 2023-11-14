package com.reflexian.foxsmp.features.pets.list;

import com.reflexian.foxsmp.features.pets.SMPPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import org.bukkit.entity.Player;

public class NorthernNomadPet extends SMPPet {

    public NorthernNomadPet(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String getName() {
        return "northern_nomad";
    }

    @Override
    public String getNiceName() {
        return "Northern Nomad";
    }

    public void updateSpeed(Player player, boolean combat) {
        if (combat) {
            player.setWalkSpeed(0.2f);
            return;
        }
        //0.2 * (50/100) + 0.15  [75%]
        player.setWalkSpeed((float) ((0.15 * (getLevel()/100))) + 0.2f);
    }

}
