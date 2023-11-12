package com.reflexian.foxsmp.features.balloons.helpers;

import com.reflexian.foxsmp.FoxSMP;
import com.reflexian.foxsmp.features.balloons.Balloon;
import com.reflexian.foxsmp.features.balloons.Skin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class BalloonImpl implements Balloon {

    public static Map<Player, BalloonImpl> balloons=new HashMap<>();

    private final Player owner;
    private final Skin skin;
    private final BalloonHoverTask task;

    public BalloonImpl(UUID owner, Skin skin) {
        this.owner = Bukkit.getPlayer(owner);
        this.skin = skin;
        this.task = new BalloonHoverTask(this);
    }

    public void startTask(){
        if(!this.task.isCancelled()) this.task.cancel();
        this.task.runTaskTimerAsynchronously(FoxSMP.getInstance(), 0L, 2L);
    }

    public static void setBalloon(Player owner, Skin skin) {
        BalloonImpl balloonImpl = balloons.getOrDefault(owner,null);
        if (balloonImpl!=null) balloonImpl.kill();
        balloonImpl = new BalloonImpl(owner.getUniqueId(), skin);
        balloons.put(owner, balloonImpl);
    }

    @Override
    public void kill() {
        task.kill();
    }

    @Override
    public void showFor(Player player) {
        task.addShownTo(player);
    }

    @Override
    public void hideFor(Player player) {
        task.removeShownTo(player);
    }
}

