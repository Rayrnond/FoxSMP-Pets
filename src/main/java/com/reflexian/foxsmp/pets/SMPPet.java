package com.reflexian.foxsmp.pets;

import com.reflexian.foxsmp.utilities.objects.HeadData;

import java.util.UUID;

public abstract class SMPPet {

    private UUID uuid;
    private UUID owner;
    private int level;


    public abstract String getName(); // used for config and saving
    public abstract HeadData getHeadData();

}
