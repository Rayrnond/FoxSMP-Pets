package com.reflexian.foxsmp.pets.list;

import com.reflexian.foxsmp.pets.SMPPet;
import com.reflexian.foxsmp.utilities.objects.HeadData;

public class GlacialGuardianPet extends SMPPet {

    @Override
    public String getName() {
        return "glacial_guardian";
    }

    @Override
    public HeadData getHeadData() {
        return HeadData.builder().name("").texture("").build();
    }


}
