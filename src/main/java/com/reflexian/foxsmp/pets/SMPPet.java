package com.reflexian.foxsmp.pets;

import com.reflexian.foxsmp.utilities.objects.HeadData;

import java.util.UUID;

public abstract class SMPPet {

    private boolean spawned;
    private boolean visible;

    private UUID uuid;
    private UUID owner;
    private double xp;


    public void spawn() {
        this.spawned = true;
    }
    public abstract String getName(); // used for config and saving
    public abstract HeadData getHeadData();

}
