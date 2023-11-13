package com.reflexian.foxsmp.features.journeycrystal;

import com.reflexian.foxsmp.FoxSMP;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;

public class GiveJourneyCrystalCommand extends CommandAPICommand {

    public GiveJourneyCrystalCommand() {
        super("givejourneycrystal");
        withPermission("foxsmp.givejourneycrystal");
        withArguments(new PlayerArgument("player"), new IntegerArgument("amount", 0));
        executesPlayer((player, arguments)-> {
            FoxSMP.getInstance().getJourneyCrystalItem().givePlayerCrystal(player, (int) arguments.getOrDefault("amount", 1));
        });
    }

}
