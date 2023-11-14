package com.reflexian.foxsmp.features.pets.list;

import com.reflexian.foxsmp.features.pets.SMPPet;
import com.reflexian.foxsmp.utilities.data.PlayerData;

public class AvalancheArtisanPet extends SMPPet {

    public AvalancheArtisanPet(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public String getName() {
        return "avalanche_artisan";
    }

    @Override
    public String getNiceName() {
        return "Avalanche Artisan";
    }

}
