package fr.thibault.manager.events;

import fr.thibault.manager.api.ServerAction;
import kong.unirest.UnirestException;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;


public class OnQuit implements Listener {
    private final JavaPlugin plugin;

    public OnQuit(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        String ownerPseudo = plugin.getConfig().getString("ownerPseudo");
        ServerAction serverAction = new ServerAction(plugin);

        //Scheduler is important because player actualisation take a small-time to be updated
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (Objects.equals(ownerPseudo, player.getName())){
                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                for (Player playerOnline : players){
                    if (!playerOnline.getName().equals(ownerPseudo)){
                        Bukkit.dispatchCommand(console, "lp user " + playerOnline.getName() + " group add operator");
                        boolean isSuccess =  serverAction.changeServerOwner(plugin.getConfig().getString("serverName"), playerOnline.getName());
                        if (!isSuccess){
                            plugin.getServer().broadcastMessage("§4§l[IMPORTANT] : §r§cLe serveur a rencontré des problèmes pour changer l\'opérateur du serveur !");
                        }else{
                            playerOnline.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("ownerMessage")));
                        }
                        return;
                    }
                }
                Bukkit.dispatchCommand(console, "lp user " + player.getName() + "parent remove operator");
            }

            int playerConnected = plugin.getServer().getOnlinePlayers().size();
            if (playerConnected == 0) {
                try {
                    boolean stopSuccess = serverAction.removeSever();
                    if (!stopSuccess){
                        Bukkit.getServer().shutdown();
                    }
                } catch (UnirestException | IOException | InterruptedException e) {
                    Bukkit.getServer().shutdown();
                    throw new RuntimeException(e);
                }
            }
        }, 1L);
    }
}
