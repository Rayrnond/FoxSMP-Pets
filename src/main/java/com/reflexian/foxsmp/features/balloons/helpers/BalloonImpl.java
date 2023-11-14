package com.reflexian.foxsmp.features.balloons.helpers;

import com.reflexian.foxsmp.FoxSMP;
import com.reflexian.foxsmp.features.balloons.Balloon;
import com.reflexian.foxsmp.features.balloons.Skin;
import com.reflexian.foxsmp.features.pets.SMPPet;
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
    private final SMPPet pet;
    private final BalloonHoverTask task;

    public BalloonImpl(UUID owner, Skin skin, SMPPet pet) {
        this.owner = Bukkit.getPlayer(owner);
        if (this.owner == null) throw new NullPointerException("Owner cannot be null!");
        this.skin = skin;
        this.pet = pet;
        this.task = new BalloonHoverTask(this);
    }

    public void startTask(){
        try {
            if(!this.task.isCancelled()) this.task.cancel();
        }catch (IllegalStateException ignored){}
        this.task.runTaskTimerAsynchronously(FoxSMP.getInstance(), 0L, 2L);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            task.addShownTo(onlinePlayer);
        }
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

