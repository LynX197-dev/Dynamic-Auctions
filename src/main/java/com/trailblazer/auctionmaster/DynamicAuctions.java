package com.trailblazer.auctionmaster;

import com.trailblazer.auctionmaster.commands.AuctionCommand;
import com.trailblazer.auctionmaster.managers.AuctionManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DynamicAuctions extends JavaPlugin {

    private static DynamicAuctions instance;
    private Economy economy;
    private AuctionManager auctionManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe("Vault economy not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        auctionManager = new AuctionManager();
        getCommand("auction").setExecutor(new AuctionCommand());
        getLogger().info("Dynamic Auctions enabled!");
    }

    @Override
    public void onDisable() {
        if (auctionManager != null) {
            auctionManager.saveAuctions();
        }
        getLogger().info("Dynamic Auctions disabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static DynamicAuctions getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }
}