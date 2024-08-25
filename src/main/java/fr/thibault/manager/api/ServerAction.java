package fr.thibault.manager.api;


import kong.unirest.*;
import kong.unirest.json.JSONObject;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;


public class ServerAction {
    private final JavaPlugin plugin;

    public ServerAction(JavaPlugin plugin){
        this.plugin = plugin;
    }

    //For security reason remove server isn't allowed by springBootApi
    public boolean removeSever() throws UnirestException, IOException, InterruptedException {
        ServerData serverData = new ServerData(plugin);
        HttpResponse<JsonNode> response = Unirest.delete("http://" + plugin.getConfig().getString("PTERODACTYL_API_IP") + "/api/application/servers/" + serverData.getServerId(plugin.getConfig().getString("serverName")) + "/force")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + plugin.getConfig().getString("PTERODACTYL_API_TOKEN"))
                .asJson();

        int status = response.getStatus();
        return status == 204;
    }

    public boolean changeServerOwner(String serverName, String newOwnerPseudo){
        try {
            HttpResponse<JsonNode> response = Unirest.post("http://" + plugin.getConfig().getString("SPRING_BOOT_IP") + "/minigames/changeowner")
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body("{\"serverName\": \"" + serverName + "\", \"newOwnerPseudo\" : \"" + newOwnerPseudo + "\"}")
                    .asJson();

            if (response.getStatus() != 200) {
                plugin.getLogger().warning("Erreur lors du changement de propriétaire : " + response.getStatusText());
                return false;
            }

            JSONObject responseBody = response.getBody().getObject();
            String newServerName = responseBody.getString("serverName");
            plugin.getConfig().set("serverName", newServerName);
            plugin.getConfig().set("ownerPseudo", newOwnerPseudo);
            plugin.saveConfig();

            return true;

        } catch (UnirestException e) {
            plugin.getLogger().severe("Erreur de connexion au serveur : " + e.getMessage());
            return false;
        } catch (Exception e) {
            plugin.getLogger().severe("Une erreur inattendue s'est produite : " + e.getMessage());
            return false;
        }
    }
}
