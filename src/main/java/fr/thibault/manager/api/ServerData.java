package fr.thibault.manager.api;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

public class ServerData {

    private final JavaPlugin plugin;

    public ServerData(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public int getServerId(String userId) throws IOException, InterruptedException {
        HttpResponse<JsonNode> response = Unirest.get("http://" + plugin.getConfig().getString("PTERODACTYL_API_IP") + "/api/application/servers")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + plugin.getConfig().getString("PTERODACTYL_API_TOKEN"))
                .asJson();

        JSONObject responseBody = response.getBody().getObject();
        JSONArray serversData = responseBody.getJSONArray("data");  // Correction ici

        for (int i = 0; i < serversData.length(); i++) {
            JSONObject serverData = serversData.getJSONObject(i);
            JSONObject attributes = serverData.getJSONObject("attributes");  // Correction ici
            String serverName = attributes.getString("name");
            if (Objects.equals(serverName, userId)) {
                return attributes.getInt("id");
            }
        }

        return -1;
    }
}
