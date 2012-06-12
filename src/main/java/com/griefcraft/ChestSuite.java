package com.griefcraft;
import java.io.*;
public class ChestSuite extends org.bukkit.plugin.java.JavaPlugin implements org.bukkit.event.Listener {
    private java.util.Map<Integer, String> protections = new java.util.HashMap<Integer, String>();
    private File dbfile = new File("plugins/ChestSuite/chestsuite.db");
    @Override public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getDataFolder().mkdir();
        try {
            if (dbfile.exists()) {
                ObjectInputStream is = new ObjectInputStream(new FileInputStream(dbfile));
                protections = (java.util.Map<Integer, String>) is.readObject();
                is.close();
            } else dbfile.createNewFile();
        } catch (Exception e) { e.printStackTrace(); }
    }
    @Override public void onDisable() {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(dbfile));
            os.writeObject(protections);
            os.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
    @org.bukkit.event.EventHandler public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent event) {
        if (!canAccess(event.getBlock().getLocation().hashCode(), event.getPlayer().getName())) {
            event.setCancelled(true);
        } else {
            if (event.getBlock().getType() == org.bukkit.Material.CHEST) {
                protections.put(event.getBlock().getLocation().hashCode(), event.getPlayer().getName());
                event.getPlayer().sendMessage("Protected!");
            }
        }
    }
    @org.bukkit.event.EventHandler public void onBlockInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        if (!canAccess(event.getClickedBlock().getLocation().hashCode(), event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(org.bukkit.ChatColor.DARK_RED + "That chest is locked by a magical spell.");
        }
    }
    @org.bukkit.event.EventHandler public void onBlockDamage(org.bukkit.event.block.BlockDamageEvent event) {
        if (!canAccess(event.getBlock().getLocation().hashCode(), event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(org.bukkit.ChatColor.DARK_RED + "That chest is locked by a magical spell.");
        }
    }
    private boolean canAccess(int hash, String player) {
        String owner = protections.get(hash);
        return owner == null || player.equals(owner);
    }
}