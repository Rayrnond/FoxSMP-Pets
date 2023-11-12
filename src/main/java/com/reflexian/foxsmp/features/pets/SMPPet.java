package com.reflexian.foxsmp.features.pets;

import com.reflexian.foxsmp.features.balloons.helpers.BalloonImpl;
import com.reflexian.foxsmp.utilities.objects.HeadData;
import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class SMPPet {

    private BalloonImpl balloon;// = new BalloonImpl(owner,getHeadData());

    private UUID uuid;
    private UUID owner;
    private double xp;


    public abstract String getName(); // used for config and saving
    public abstract HeadData getHeadData();

}
