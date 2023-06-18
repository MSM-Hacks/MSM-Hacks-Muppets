package ru.msmhacks.muppets;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.ISFSEventListener;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.LoginData;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import ru.msmhacks.muppets.auth.AuthServer;
import ru.msmhacks.muppets.entities.*;
import ru.msmhacks.muppets.entities.Player.Player;
import ru.msmhacks.muppets.entities.Player.PlayerIsland;
import ru.msmhacks.muppets.entities.Player.PlayerStructure;
import ru.msmhacks.muppets.managers.PlayerDatabaseManager;
import ru.msmhacks.muppets.managers.StaticDatabaseManager;
import ru.msmhacks.muppets.managers.Utils;

import java.io.File;
import java.sql.SQLException;
import java.util.Random;

import static ru.msmhacks.muppets.auth.AuthServer.runAuthServer;

public class MuppetsExtension extends SFSExtension {

    public static MuppetsExtension extension;
    public static String ROOT = "C:\\Users\\Zewsic\\SmartFoxServer_2X\\res\\json_db\\";
    //public static String ROOT = "/sdcard/msmhacks/res/";
    public static String DBROOT = ROOT;//"/home/uberbot/mms_dbs/";

    @Override
    public void init() {
        extension = this;
        addEventListener(SFSEventType.USER_JOIN_ZONE, new PlayerJoinListener());

        try {
            runAuthServer(80, "192.168.0.100");
            StaticDatabaseManager.initAllDatabases(false);
            PlayerDatabaseManager.initAllDatabases(false);
        } catch (Exception e) {
            trace(e.getStackTrace());
        }
    }

    public static class PlayerJoinListener implements ISFSEventListener {

        @Override
        public void handleServerEvent(ISFSEvent isfsEvent) {
            User user = (User) isfsEvent.getParameter(SFSEventParam.USER);
            user.setProperty("user_id", AuthServer.ip2user.get(user.getIpAddress())[0]);
            user.setProperty("bbb_id", AuthServer.ip2user.get(user.getIpAddress())[2]);

            if (false) {
                user.setProperty("player", Player.getPlayer((String) user.getProperty("user_id"), Integer.parseInt((String) user.getProperty("bbb_id"))));
                extension.send("gs_initialized", new SFSObject(), user);
            } else {
                extension.send("gs_user_messages", SFSObject.newFromJsonData("{\"urls\":[{\"platform\":\"ios\",\"url\":\"https://discord.gg/msm-hacks\"},{\"platform\":\"android\",\"url\":\"https://discord.gg/msm-hacks\"},{\"platform\":\"amazon\",\"url\":\"https://discord.gg/msm-hacks\"},{\"platform\":\"samsung\",\"url\":\"https://discord.gg/msm-hacks\"}],\"success\":false,\"message\":\"You need to Activate your account\"}"), user);
            }
        }
    }

    @Override
    public void handleClientRequest(String cmd, User sender, ISFSObject params) {
        handleRequest(cmd, sender, params);
    }

    public void handleRequest(String cmd, User sender, ISFSObject params) {
        String user_id = (String) sender.getProperty("user_id");
        String bbb_id = (String) sender.getProperty("bbb_id");
        Player player = (Player) sender.getProperty("player");
        trace(String.format("New request from %s: %s\n%s", user_id, cmd, params.getDump()));


        switch (cmd) {
            case "db_monster":
                send("db_monster", Monster.monsters_list, sender);
                break;
            case "db_structure":
                send("db_structure", Structure.structures_list, sender);
                break;
            case "db_island":
                send("db_island", Island.islands_list, sender);
                break;
            case "db_level":
                send("db_level", Level.levels_list, sender);
                break;
            case "db_breeding":
                send("db_breeding", BreedingCombination.breeding_list, sender);
                break;
            case "db_lighting":
                send("db_lighting", Light.lights_list, sender);
                break;
            case "db_backdrops":
                send("db_backdrops", Backdrop.backdrops_list, sender);
                break;
            case "db_store":
                SFSObject storeObject = new SFSObject();
                storeObject.putSFSArray("store_item_data", new SFSArray());
                storeObject.putSFSArray("store_group_data", new SFSArray());
                storeObject.putSFSArray("store_currency_data", new SFSArray());
                send("db_store", storeObject, sender);
                break;
            case "gs_quest":
                send("gs_quest", Utils.getSFSFromJson(new File(MuppetsExtension.DBROOT + "quest_data.json")), sender);
                break;
            case "gs_player":
                send("gs_player", player.toSFSObject(), sender);
                break;
            case "keep_alive":
                send("keep_alive", params, sender);
                break;
            case "gs_change_island": {
                long user_island_id = params.getLong("user_island_id");

                if (player.changeIsland(user_island_id)) {
                    SFSObject response = new SFSObject();
                    response.putBool("success", true);
                    response.putLong("user_island_id", user_island_id);

                    SFSObject hidden_objects = new SFSObject();
                    hidden_objects.putSFSArray("objects", new SFSArray());
                    response.putSFSObject("hidden_objects", hidden_objects);

                    send("gs_change_island", response, sender);
                } else {
                    SFSObject response = new SFSObject();
                    response.putBool("success", false);
                    response.putUtfString("message", "User don't have this island");

                    send("gs_change_island", response, sender);
                }
                break;
            }
            case "gs_buy_island": {
                int island_id = params.getInt("island_id");

                if (!PlayerIsland.isPlayerHasIslandType(Long.parseLong(bbb_id), island_id)) {
                    long user_island_id = player.buyIsland(island_id);
                    if (user_island_id != -1) {
                        SFSObject new_island = PlayerIsland.getIsland(user_island_id).toSFSObject();

                        SFSObject response = new SFSObject();
                        response.putBool("success", true);
                        response.putSFSObject("user_island", new_island);
                        response.putSFSArray("properties", player.getProperties());

                        send("gs_buy_island", response, sender);
                    } else {
                        SFSObject response = new SFSObject();
                        response.putBool("success", false);
                        response.putUtfString("message", "Error buying island");

                        send("gs_buy_island", response, sender);

                    }
                } else {
                    SFSObject response = new SFSObject();
                    response.putBool("success", false);
                    response.putUtfString("message", "User already have this island");

                    send("gs_buy_island", response, sender);
                }
                break;
            }
            case "gs_buy_structure": {
                int x = params.getInt("pos_x");
                int y = params.getInt("pos_y");
                int flip = params.getInt("flip");
                float scale = params.getDouble("scale").floatValue();
                int structure_id = params.getInt("structure_id");

                PlayerStructure newStructure = player.buyStructure(structure_id, x, y, flip, scale);
                if (newStructure != null) {
                    SFSObject response = new SFSObject();
                    response.putBool("success", true);

                    SFSObject structure = new SFSObject();
                    structure.putLong("island", player.active_island);
                    structure.putInt("pos_x", newStructure.pos_x);
                    structure.putInt("pos_y", newStructure.pos_y);
                    structure.putInt("flip", newStructure.flip);
                    structure.putInt("muted", newStructure.muted);
                    structure.putInt("is_upgrading", newStructure.is_upgrading);
                    structure.putInt("is_complete", newStructure.is_complete);
                    structure.putLong("user_structure_id", newStructure.user_structure_id);
                    structure.putLong("last_collection", newStructure.last_collection);
                    structure.putInt("structure", newStructure.structure);
                    structure.putDouble("scale", newStructure.scale);

                    response.putSFSArray("monster_happy_effects", new SFSArray());
                    response.putSFSArray("properties", player.getProperties());
                    response.putSFSObject("user_structure", structure);

                    send("gs_buy_structure", response, sender);
                } else {
                    SFSObject response = new SFSObject();
                    response.putBool("success", false);
                    response.putUtfString("message", "Error buying stricture");
                }
                break;
            }
            case "gs_get_random_visit_data": {
                player.coins = 999999999;
                player.food = 999999999;
                player.diamonds = 999999999;
                player.level = 100;
                player.xp = 999999999;

                SFSObject response = new SFSObject();
                response.putBool("success", true);
                response.putSFSArray("properties", player.getProperties());

                send("gs_referral_request", response, sender);
                break;
            }
            case "gs_request_backdrop_change": {
                SFSObject response = new SFSObject();
                response.putBool("success", true);
                response.putInt("backdrop_id", params.getInt("backdrop_id"));
                response.putBool("purshared", true);
                response.putSFSArray("properties", player.getProperties());

                send("gs_request_backdrop_change", response, sender);
                break;
            }
            case "gs_request_lighting_change": {

                SFSObject response = new SFSObject();
                response.putBool("success", true);
                response.putInt("lighting_id", params.getInt("lighting_id"));
                response.putBool("purshared", true);
                response.putSFSArray("properties", player.getProperties());

                send("gs_request_lighting_change", response, sender);
                break;
            }
            default:
                params.putBool("success", false);
                send(cmd, params, sender);
        }
    }

}


