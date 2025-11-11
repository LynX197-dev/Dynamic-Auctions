package com.trailblazer.auctionmaster.commands;

import com.trailblazer.auctionmaster.DynamicAuctions;
import com.trailblazer.auctionmaster.gui.AuctionGUI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AuctionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            new AuctionGUI(player, 0).open();
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "sell":
                if (args.length < 3) {
                    player.sendMessage("Usage: /ah sell <price> <time in minutes>");
                    return true;
                }
                try {
                    double price = Double.parseDouble(args[1]);
                    int time = Integer.parseInt(args[2]);
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        player.sendMessage("You must hold an item in your hand!");
                        return true;
                    }
                    DynamicAuctions.getInstance().getAuctionManager().createAuction(player, item, price, time);
                    player.getInventory().setItemInMainHand(null);
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid price or time!");
                }
                break;
            case "bid":
                if (args.length < 3) {
                    player.sendMessage("Usage: /ah bid <id> <amount>");
                    return true;
                }
                try {
                    int id = Integer.parseInt(args[1]);
                    double amount = Double.parseDouble(args[2]);
                    DynamicAuctions.getInstance().getAuctionManager().bidOnAuction(player, id, amount);
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid ID or amount!");
                }
                break;

            case "cancel":
                if (args.length < 2) {
                    player.sendMessage("Usage: /ah cancel <id>");
                    return true;
                }
                try {
                    int id = Integer.parseInt(args[1]);
                    DynamicAuctions.getInstance().getAuctionManager().cancelAuction(player, id);
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid ID!");
                }
                break;
            default:
                player.sendMessage("Unknown subcommand!");
                break;
        }
        return true;
    }
}
