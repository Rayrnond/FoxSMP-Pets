package com.reflexian.foxsmp.features.pets;

import com.reflexian.foxsmp.features.balloons.Skin;
import com.reflexian.foxsmp.features.balloons.helpers.BalloonImpl;
import com.reflexian.foxsmp.utilities.objects.HeadData;
import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class SMPPet {

    private transient BalloonImpl balloon; // transient so it doesn't get saved

    private UUID uuid;
    private UUID owner;
    private double xp;

    public SMPPet() {
        balloon = new BalloonImpl(owner,getSkin());
    }


    public abstract String getName(); // used for config and saving
    public abstract Skin getSkin();

}
