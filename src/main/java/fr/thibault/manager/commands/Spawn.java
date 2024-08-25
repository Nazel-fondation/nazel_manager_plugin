package fr.thibault.manager.commands;

import fr.thibault.manager.sounds.PlaySound;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Spawn implements CommandExecutor {

    private final JavaPlugin plugin;

    public Spawn(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player){
            PlaySound playSound = new PlaySound();
            Player player = (Player) commandSender;
            if (player.hasPermission("manager.setSpawn")){
                if (strings.length != 1){
                    playSound.error(player);
                    player.sendMessage("§4§l[ERREUR] : §r§4syntaxe incorect /setspawn <set/disable>");
                    return false;
                }
                String param = strings[0];
                switch (param){
                    case "set":
                        Location location = player.getLocation();
                        plugin.getConfig().set("tpSpawnWhenJoin", true);
                        plugin.getConfig().set("spawn_x", location.getX());
                        plugin.getConfig().set("spawn_y", location.getY());
                        plugin.getConfig().set("spawn_z", location.getZ());
                        plugin.saveConfig();
                        playSound.success(player);
                        player.sendMessage("§2§l[SUCCES] : §r§aspawn défini avec succès");
                        break;
                    case "disable":
                        plugin.getConfig().set("tpSpawnWhenJoin", false);
                        plugin.saveConfig();
                        playSound.success(player);
                        player.sendMessage("§2§l[SUCCES] : §r§aspawn désactivé avec succès");
                    default:
                        playSound.error(player);
                        player.sendMessage("§4§l[ERREUR] : §r§4syntaxe incorect /setspawn <set/disable>");
                }
            }else{
                playSound.error(player);
                player.sendMessage("§4§l[ERREUR] : §r§4vous n'êtes pas autorisé à executer cette commande");
                return false;
            }
        }

        return false;
    }
}
