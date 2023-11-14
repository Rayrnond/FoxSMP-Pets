package com.reflexian.foxsmp.features.pets.list;

import com.reflexian.foxsmp.features.pets.SMPPet;

public class NonePet extends SMPPet {

    @Override
    public String getName() {
        return "none";
    }

    @Override
    public String getNiceName() {
        return "null";
    }

}
