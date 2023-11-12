package com.reflexian.foxsmp.features.balloons;

import org.bukkit.entity.Player;

public interface Balloon {

    Player getOwner();
    void kill();
    void showFor(Player player);
    void hideFor(Player player);

}
