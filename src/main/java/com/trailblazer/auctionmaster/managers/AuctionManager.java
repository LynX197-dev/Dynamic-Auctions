package com.trailblazer.auctionmaster.managers;

import com.trailblazer.auctionmaster.DynamicAuctions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AuctionManager {

    private Map<Integer, Auction> auctions = new HashMap<>();
    private int nextId = 1;

    public AuctionManager() {
        loadAuctions();
        startAuctionTimer();
    }

    public void createAuction(Player seller, ItemStack item, double startBid, int timeMinutes) {
        Auction auction = new Auction(nextId++, seller.getUniqueId(), item, startBid, System.currentTimeMillis() + timeMinutes * 60 * 1000);
        auctions.put(auction.getId(), auction);
        seller.sendMessage("Auction created with ID: " + auction.getId());
        saveAuctions();
    }

    public void bidOnAuction(Player bidder, int id, double amount) {
        Auction auction = auctions.get(id);
        if (auction == null) {
            bidder.sendMessage("Auction not found!");
            return;
        }
        if (auction.getSeller().equals(bidder.getUniqueId())) {
            bidder.sendMessage("You can't bid on your own auction!");
            return;
        }
        if (amount <= auction.getCurrentBid()) {
            bidder.sendMessage("Bid must be higher than current bid!");
            return;
        }
        if (!DynamicAuctions.getInstance().getEconomy().has(bidder, amount)) {
            bidder.sendMessage("You don't have enough money!");
            return;
        }
        // Refund previous bidder
        if (auction.getBidder() != null) {
            Player prevBidder = Bukkit.getPlayer(auction.getBidder());
            if (prevBidder != null) {
                DynamicAuctions.getInstance().getEconomy().depositPlayer(prevBidder, auction.getCurrentBid());
                prevBidder.sendMessage("Your bid was outbid!");
            }
        }
        auction.setBidder(bidder.getUniqueId());
        auction.setCurrentBid(amount);
        bidder.sendMessage("Bid placed!");
        saveAuctions();
    }

    public void cancelAuction(Player player, int id) {
        Auction auction = auctions.get(id);
        if (auction == null) {
            player.sendMessage("Auction not found!");
            return;
        }
        if (!auction.getSeller().equals(player.getUniqueId()) && !player.hasPermission("auctionmaster.admin")) {
            player.sendMessage("You can't cancel this auction!");
            return;
        }
        auctions.remove(id);
        Player seller = Bukkit.getPlayer(auction.getSeller());
        if (seller != null) {
            seller.getInventory().addItem(auction.getItem());
            seller.sendMessage("Auction cancelled.");
        }
        saveAuctions();
    }

    public Collection<Auction> getAuctions() {
        return auctions.values();
    }

    private void startAuctionTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                List<Integer> toRemove = new ArrayList<>();
                for (Auction auction : auctions.values()) {
                    if (now >= auction.getEndTime()) {
                        endAuction(auction);
                        toRemove.add(auction.getId());
                    }
                }
                for (int id : toRemove) {
                    auctions.remove(id);
                }
                if (!toRemove.isEmpty()) {
                    saveAuctions();
                }
            }
        }.runTaskTimer(DynamicAuctions.getInstance(), 20L, 20L); // Every second
    }

    private void endAuction(Auction auction) {
        Player seller = Bukkit.getPlayer(auction.getSeller());
        Player bidder = auction.getBidder() != null ? Bukkit.getPlayer(auction.getBidder()) : null;
        if (bidder != null) {
            DynamicAuctions.getInstance().getEconomy().withdrawPlayer(bidder, auction.getCurrentBid());
            DynamicAuctions.getInstance().getEconomy().depositPlayer(seller, auction.getCurrentBid());
            bidder.getInventory().addItem(auction.getItem());
            bidder.sendMessage("You won the auction!");
            if (seller != null) {
                seller.sendMessage("Your auction sold for $" + auction.getCurrentBid());
            }
        } else {
            if (seller != null) {
                seller.getInventory().addItem(auction.getItem());
                seller.sendMessage("Auction ended with no bids.");
            }
        }
    }

    private void loadAuctions() {
        // Load from config or file, simplified for now
    }

    public void saveAuctions() {
        // Save to config or file, simplified for now
    }
}