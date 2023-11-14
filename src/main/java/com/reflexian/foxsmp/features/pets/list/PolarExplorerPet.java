package com.reflexian.foxsmp.features.pets.list;

import com.reflexian.foxsmp.features.balloons.Skin;
import com.reflexian.foxsmp.features.pets.SMPPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;

public class PolarExplorerPet extends SMPPet {

    public PolarExplorerPet(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String getName() {
        return "polar_explorer";
    }

    @Override
    public String getNiceName() {
        return "Polar Explorer";
    }

}
