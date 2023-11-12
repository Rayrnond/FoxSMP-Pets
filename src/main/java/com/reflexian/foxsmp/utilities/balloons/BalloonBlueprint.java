package com.reflexian.foxsmp.utilities.balloons;

import org.bukkit.inventory.ItemStack;

import java.awt.*;

public interface BalloonBlueprint {

    public Skin getSkin();
    public Color getTrailColor();
    public ItemStack getBalloonItem();

}
