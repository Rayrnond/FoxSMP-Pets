package com.reflexian.foxsmp.features.inventories;

import com.reflexian.foxsmp.FoxSMP;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;

public class JourneyCrystalCommand extends CommandAPICommand {

    public JourneyCrystalCommand() {
        super("openjourneycrystal");
        withPermission("foxsmp.openjourneycrystal");
        executesPlayer((player, arguments)-> {
            new JourneyCrystalGUI().init(player);
        });
    }

}
