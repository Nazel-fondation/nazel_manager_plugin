package fr.thibault.manager.commands;

import fr.thibault.manager.api.ServerAction;
import fr.thibault.manager.sounds.PlaySound;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ChangeOperator implements CommandExecutor {

    private final JavaPlugin plugin;

    public ChangeOperator(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player){
            PlaySound playSound = new PlaySound();
            Player player = (Player) commandSender;
            if (player.getName().equals(plugin.getConfig().getString("ownerPseudo"))){
                if (strings.length != 1){
                    playSound.error(player);
                    player.sendMessage("§4§l[ERREUR] : §r§cSyntaxe incorrect /changeOwner <pseudo>");
                    return false;
                }
                String newOwnerPlayerName = strings[0];
                if (Objects.equals(newOwnerPlayerName, player.getName())){
                    playSound.error(player);
                    player.sendMessage("§4§l[ERREUR] : §r§cVous ne pouvez pas vous définir vous même opérateur");
                    return false;
                }
                if (Bukkit.getPlayer(newOwnerPlayerName) != null){
                    Player newOwnerPLayer = Bukkit.getPlayer(newOwnerPlayerName);
                    ServerAction serverAction = new ServerAction(plugin);
                    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                    assert newOwnerPLayer != null;
                    Bukkit.dispatchCommand(console, "lp user " + newOwnerPLayer.getName() + " group add operator");
                    Bukkit.dispatchCommand(console, "lp user " + plugin.getConfig().getString("ownerPseudo") + " group add operator");
                    boolean isSuccess = serverAction.changeServerOwner(plugin.getConfig().getString("serverName"), newOwnerPlayerName);
                    if (isSuccess){
                        playSound.success(player);
                        player.sendMessage("§2§l[SUCCES] : §r§avous n'êtes plus l'opérateur du serveur");
                        playSound.success(newOwnerPLayer);
                        newOwnerPLayer.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("ownerMessage")));
                        return true;
                    }else{
                        playSound.serverProblem(player);
                        player.sendMessage("§4§l[DANGER] : §r§4Le serveur a rencontré un problème durant le changement de l'opérateur une instabilité des permissions est potentielle !");
                        return false;
                    }
                }else{
                    playSound.error(player);
                    player.sendMessage("§4§l[ERREUR] : §r§cJoueur introuvable");
                    return false;
                }
            }else{
                playSound.error(player);
                player.sendMessage("§4§l[ERREUR] : §r§4cous n'êtes pas autorisé à executer cette commande");
                return false;
            }
        }

        return false;
    }
}
