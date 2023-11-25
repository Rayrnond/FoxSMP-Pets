package com.reflexian.foxsmp.features.candy;

import com.reflexian.foxsmp.FoxSMP;
import de.tr7zw.nbtapi.NBT;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandExecutionInfo;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class GivePetCandyCommand extends CommandAPICommand {

    public GivePetCandyCommand() {
        super("givepetcandy");
        withPermission("foxsmp.givepetcandy");
        withArguments(new PlayerArgument("player"), new IntegerArgument("amount", 0));
        executes((sender, arguments)-> {
            FoxSMP.getInstance().getPetCandyItem().givePlayerCandy((Player) arguments.get("player"), (int) arguments.getOrDefault("amount", 1));
        });

        withSubcommand(new CommandAPICommand("debug")
                .withPermission("foxsmp.debug")
                .executesPlayer((player, arguments)-> {
                    NamespacedKey key = new NamespacedKey("foxsmp", "petcandy");
                    Recipe recipe = Bukkit.getRecipe(key);
                    if (recipe == null) {
                        player.sendMessage("Recipe not found");
                        return;
                    }
                    if (recipe instanceof ShapedRecipe shapedRecipe) {
                        player.sendMessage("Recipe is shaped: " + recipe.getResult());
                        shapedRecipe.getChoiceMap().forEach((key1, value) -> {
                            player.sendMessage(key1 + " -> " + value);
                        });
                    } else {
                        player.sendMessage("Recipe is not shaped: " + recipe.getResult());
                    }
                }));

        withSubcommand(new CommandAPICommand("debughand")
                .withPermission("foxsmp.debug")
                .executesPlayer((player, arguments)-> {
                    player.sendMessage("Item in hand: " + player.getInventory().getItemInMainHand());
                }));

    }

}
