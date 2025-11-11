package com.trailblazer.auctionmaster.managers;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Auction {

    private int id;
    private UUID seller;
    private ItemStack item;
    private double startBid;
    private double currentBid;
    private UUID bidder;
    private long endTime;

    public Auction(int id, UUID seller, ItemStack item, double startBid, long endTime) {
        this.id = id;
        this.seller = seller;
        this.item = item;
        this.startBid = startBid;
        this.currentBid = startBid;
        this.endTime = endTime;
    }

    // Getters and setters
    public int getId() { return id; }
    public UUID getSeller() { return seller; }
    public ItemStack getItem() { return item; }
    public double getStartBid() { return startBid; }
    public double getCurrentBid() { return currentBid; }
    public void setCurrentBid(double currentBid) { this.currentBid = currentBid; }
    public UUID getBidder() { return bidder; }
    public void setBidder(UUID bidder) { this.bidder = bidder; }
    public long getEndTime() { return endTime; }
}