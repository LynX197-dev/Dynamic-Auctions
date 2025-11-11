package com.trailblazer.auctionmaster.gui;

import com.trailblazer.auctionmaster.DynamicAuctions;
import com.trailblazer.auctionmaster.managers.Auction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AuctionGUI implements InventoryHolder {

    private Inventory inventory;
    private Player player;
    private int page;

    public AuctionGUI(Player player, int page) {
        this.player = player;
        this.page = page;
        this.inventory = Bukkit.createInventory(this, 54, "Auction House - Page " + (page + 1));

        loadAuctions();
    }

    private void loadAuctions() {
        List<Auction> auctions = new ArrayList<>(DynamicAuctions.getInstance().getAuctionManager().getAuctions());
        int start = page * 45;
        for (int i = 0; i < 45 && start + i < auctions.size(); i++) {
            Auction auction = auctions.get(start + i);
            ItemStack item = auction.getItem().clone();
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();
                if (lore == null) lore = new ArrayList<>();
                lore.add("Auction ID: " + auction.getId());
                lore.add("Current Bid: $" + auction.getCurrentBid());
                lore.add("Time Left: " + ((auction.getEndTime() - System.currentTimeMillis()) / 1000) + "s");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inventory.setItem(i, item);
        }

        // Navigation buttons
        ItemStack prev = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName("Previous Page");
        prev.setItemMeta(prevMeta);
        inventory.setItem(45, prev);

        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName("Next Page");
        next.setItemMeta(nextMeta);
        inventory.setItem(53, next);
    }

    public void open() {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}