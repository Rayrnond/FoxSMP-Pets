package com.reflexian.foxsmp.features.pets.list;

import com.reflexian.foxsmp.features.balloons.Skin;
import com.reflexian.foxsmp.features.pets.SMPPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;
import com.reflexian.foxsmp.utilities.objects.HeadData;

public class GlacialGuardianPet extends SMPPet {

    public GlacialGuardianPet(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String getName() {
        return "glacial_guardian";
    }

    @Override
    public String getNiceName() {
        return "Glacial Guardian";
    }

}
