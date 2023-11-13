package com.reflexian.foxsmp.features.inventories;

import dev.jorel.commandapi.CommandAPICommand;

public class JourneyCrystalCommand extends CommandAPICommand {

    public JourneyCrystalCommand() {
        super("openjourneycrystal");
        withPermission("foxsmp.openjourneycrystal");
        executesPlayer((player, arguments)-> {
            new JourneyCrystalGUI().init(player);
        });
    }

}
