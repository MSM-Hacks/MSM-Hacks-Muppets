package ru.msmhacks.muppets;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.ISFSEventListener;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;
import ru.msmhacks.muppets.entities.*;
import ru.msmhacks.muppets.managers.StaticDatabaseManager;
import ru.msmhacks.muppets.managers.Utils;

import java.io.File;

import static ru.msmhacks.muppets.auth.AuthServer.runAuthServer;

public class MuppetsExtension extends SFSExtension {

    public static MuppetsExtension extension;
    static String ROOT = "C:\\Users\\Zewsic\\SmartFoxServer_2X\\res\\json_db\\";

    @Override
    public void init() {
        extension = this;
        addEventListener(SFSEventType.USER_JOIN_ZONE, new PlayerJoinListener());

        try {
            runAuthServer(80, "192.168.47.73");
            StaticDatabaseManager.initAllDatabases(true);
        } catch (Exception e) {
            trace(e.getStackTrace());
        }
    }

    public static class PlayerJoinListener implements ISFSEventListener {

        @Override
        public void handleServerEvent(ISFSEvent isfsEvent) {
            User user = (User) isfsEvent.getParameter(SFSEventParam.USER);
            extension.send("gs_initialized", new SFSObject(), user);
            extension.send("db_monster", Utils.getSFSFromJson(new File(ROOT + "monster_data.json")), user);
            extension.send("db_structure", Utils.getSFSFromJson(new File(ROOT + "structure_data.json")), user);
            extension.send("db_island", Utils.getSFSFromJson(new File(ROOT + "island_data.json")), user);
            extension.send("db_level", Utils.getSFSFromJson(new File(ROOT + "level_data.json")), user);
            extension.send("db_breeding", Utils.getSFSFromJson(new File(ROOT + "breeding_data.json")), user);
            extension.send("db_lighting", Utils.getSFSFromJson(new File(ROOT + "lighting_data.json")), user);
            extension.send("db_backdrops", Utils.getSFSFromJson(new File(ROOT + "backdrop_data.json")),user);
            extension.send("db_store", Utils.getSFSFromJson(new File(ROOT + "store_data.json")), user);
            extension.send("gs_quest", Utils.getSFSFromJson(new File(ROOT + "quest_data.json")), user);
            extension.send("gs_player", Utils.getSFSFromJson(new File(ROOT + "player_data.json")), user);
        }
    }

    @Override
    public void handleClientRequest(String cmd, User sender, ISFSObject params) {
        handleRequest(cmd, sender, params);
    }

    public void handleRequest(String cmd, User sender, ISFSObject params) {
        String user_id = sender.getName();
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
                send("db_lighting", Utils.getSFSFromJson(new File(ROOT + "lighting_data.json")), sender);
                break;
            case "db_backdrops":
                send("db_backdrops", Utils.getSFSFromJson(new File(ROOT + "backdrop_data.json")), sender);
                break;
            case "db_store":
                SFSObject storeObject = new SFSObject();
                storeObject.putSFSArray("store_item_data", new SFSArray());
                storeObject.putSFSArray("store_group_data", new SFSArray());
                storeObject.putSFSArray("store_currency_data", new SFSArray());
                send("db_store", storeObject, sender);
                break;
            case "gs_quest":
                send("gs_quest", Utils.getSFSFromJson(new File(ROOT + "quest_data.json")), sender);
                break;
            case "gs_player":
                send("gs_player", Utils.getSFSFromJson(new File(ROOT + "player_data.json")), sender);
                break;
            case "keep_alive":
                send("keep_alive", params, sender);
                break;
            default:
                params.putBool("success", false);
                send(cmd, params, sender);
        }
    }

}


