package com.reflexian.foxsmp.utilities.balloons;

import com.comphenix.protocol.PacketType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Balloon {

    public Location getFutureLocation();
    public Player getOwner();
    public void kill();
    public void showFor(PacketType.Play player);
    public void refresh();

}
