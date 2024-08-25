package fr.thibault.manager;

import fr.thibault.manager.api.ServerAction;
import fr.thibault.manager.commands.ChangeOperator;
import fr.thibault.manager.commands.ChangeOperatorTapCompleter;
import fr.thibault.manager.commands.Spawn;
import fr.thibault.manager.commands.SpawnTabCompleter;
import fr.thibault.manager.events.OnJoin;
import fr.thibault.manager.events.OnQuit;
import kong.unirest.UnirestException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;


public class Main extends JavaPlugin {
    //TODO vérifier si il y a pas moyen de recoder le système pour supprimer les serveurs avec le serverId

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("changeOperator").setExecutor(new ChangeOperator(this));
        getCommand("changeOperator").setTabCompleter(new ChangeOperatorTapCompleter());
        getCommand("spawn").setExecutor(new Spawn(this));
        getCommand("spawn").setTabCompleter(new SpawnTabCompleter());
        getServer().getPluginManager().registerEvents(new OnQuit(this), this);
        getServer().getPluginManager().registerEvents(new OnJoin(this), this);

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().isEmpty()) {
                    ServerAction serverAction = new ServerAction(Main.this);
                    try {
                        boolean stopSuccess = serverAction.removeSever();
                        if (!stopSuccess) {
                            Bukkit.getServer().shutdown();
                        }
                    } catch (UnirestException | IOException | InterruptedException e) {
                        Bukkit.getServer().shutdown();
                        throw new RuntimeException(e);
                    }
                }
            }
        }, 10 * 60 * 20L, 10 * 60 * 20L); // Délai initial 10 et intervalle de répétition de 10 minutes chacun
    }
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }
}