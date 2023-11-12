package com.reflexian.foxsmp.features.balloons;

import org.bukkit.entity.Player;

public interface Balloon {

    public Player getOwner();
    public void kill();
    public void showFor(Player player);
    public void hideFor(Player player);

}
