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
import com.smartfoxserver.v2.util.IDisconnectionReason;
import ru.msmhacks.muppets.auth.AuthServer;
import ru.msmhacks.muppets.entities.*;
import ru.msmhacks.muppets.entities.Player.Player;
import ru.msmhacks.muppets.entities.Player.PlayerIsland;
import ru.msmhacks.muppets.entities.Player.PlayerMonster;
import ru.msmhacks.muppets.entities.Player.PlayerStructure;
import ru.msmhacks.muppets.managers.AuthDatabaseManager;
import ru.msmhacks.muppets.managers.PlayerDatabaseManager;
import ru.msmhacks.muppets.managers.StaticDatabaseManager;
import ru.msmhacks.muppets.managers.Utils;
import sfs2x.client.util.ClientDisconnectionReason;

import java.io.File;
import java.sql.SQLException;
import java.util.Random;

import static ru.msmhacks.muppets.auth.AuthServer.runAuthServer;

public class MuppetsExtension extends SFSExtension {

    public static MuppetsExtension extension;
    public static final boolean prod = false;
    public static String DBROOT = prod?"/root/server/mms/":"C:\\Users\\Zewsic\\SmartFoxServer_2X\\res\\json_db\\";

    @Override
    public void init() {
        extension = this;
        addEventListener(SFSEventType.USER_JOIN_ZONE, new PlayerJoinListener());

        try {
            runAuthServer(80, prod?"5.175.225.81":"192.168.0.100");
            AuthDatabaseManager.init(true);
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
            user.setProperty("client_version", AuthServer.ip2user.get(user.getIpAddress())[3]);

            user.setProperty("player", Player.getPlayer((String) user.getProperty("user_id"), Integer.parseInt((String) user.getProperty("bbb_id"))));
            //if (user.getProperty("client_version") == "1.1.6") {
                extension.send("gs_initialized", new SFSObject(), user);
            //} else {
            //    extension.send("gs_client_version_error", SFSObject.newFromJsonData("{\"urls\":[{\"platform\":\"ios\",\"url\":\"https://www.youtube.com/watch?v=xvFZjo5PgG0\"},{\"platform\":\"android\",\"url\":\"https://www.youtube.com/watch?v=xvFZjo5PgG0\"},{\"platform\":\"amazon\",\"url\":\"https://www.youtube.com/watch?v=xvFZjo5PgG0\"},{\"platform\":\"samsung\",\"url\":\"https://www.youtube.com/watch?v=xvFZjo5PgG0\"}],\"success\":false,\"message\":\"client version fail\"}"), user);
            //}
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

        SFSObject response = new SFSObject();
        response.putLong("server_time", System.currentTimeMillis());


        switch (cmd) {
            //Databases
            case "db_monster": {
                response = Monster.monsters_list;
                response.putLong("server_time", System.currentTimeMillis());
                send("db_monster", response, sender);
                break;
            }
            case "db_structure": {
                response = Structure.structures_list;
                response.putLong("server_time", System.currentTimeMillis());
                send("db_structure", response, sender);
                break;
            }
            case "db_island": {
                response = Island.islands_list;
                response.putLong("server_time", System.currentTimeMillis());
                send("db_island", response, sender);
                break;
            }
            case "db_level": {
                response = Level.levels_list;
                response.putLong("server_time", System.currentTimeMillis());
                send("db_level", response, sender);
                break;
            }
            case "db_breeding": {
                response = BreedingCombination.breeding_list;
                response.putLong("server_time", System.currentTimeMillis());
                send("db_breeding", response, sender);
                break;
            }
            case "db_lighting": {
                response = Light.lights_list;
                response.putLong("server_time", System.currentTimeMillis());
                send("db_lighting", response, sender);
                break;
            }
            case "db_backdrops": {
                response = Backdrop.backdrops_list;
                response.putLong("server_time", System.currentTimeMillis());
                send("db_backdrops", response, sender);
                break;
            }
            case "db_store": {
                response.putSFSArray("store_item_data", new SFSArray());
                response.putSFSArray("store_group_data", new SFSArray());
                response.putSFSArray("store_currency_data", new SFSArray());
                send("db_store", response, sender);
                break;
            }
            //Player datas
            case "gs_quest": {
                response = Utils.getSFSFromJson(new File(MuppetsExtension.DBROOT + "quest_data.json"));
                response.putLong("server_time", System.currentTimeMillis());
                send("gs_quest", response, sender);
                break;
            }
            case "gs_player": {
                response = player.toSFSObject();
                response.putLong("server_time", System.currentTimeMillis());
                send("gs_player", response, sender);
                break;
            }
            case "keep_alive": {
                response = (SFSObject) params;
                response.putLong("server_time", System.currentTimeMillis());
                send("keep_alive", response, sender);
                break;
            }
            //Islands
            case "gs_change_island": {
                long user_island_id = params.getLong("user_island_id");

                if (player.changeIsland(user_island_id)) {
                    response.putBool("success", true);
                    response.putLong("user_island_id", user_island_id);

                    SFSObject hidden_objects = new SFSObject();
                    hidden_objects.putSFSArray("objects", new SFSArray());
                    response.putSFSObject("hidden_objects", hidden_objects);
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "User don't have this island");
                }
                send("gs_change_island", response, sender);
                break;
            }
            case "gs_buy_island": {
                int island_id = params.getInt("island_id");

                if (!PlayerIsland.isPlayerHasIslandType(Long.parseLong(bbb_id), island_id)) {
                    long user_island_id = player.buyIsland(island_id);
                    if (user_island_id != -1) {
                        SFSObject new_island = PlayerIsland.getIsland(user_island_id).toSFSObject();

                        response.putBool("success", true);
                        response.putSFSObject("user_island", new_island);
                        response.putSFSArray("properties", player.getProperties());

                        send("gs_buy_island", response, sender);
                    } else {
                        response.putBool("success", false);
                        response.putUtfString("message", "Error buying island");
                    }
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "User already have this island");
                }
                send("gs_buy_island", response, sender);
                break;
            }
            //Structures
            case "gs_buy_structure": {
                int x = params.getInt("pos_x");
                int y = params.getInt("pos_y");
                int flip = params.getInt("flip");
                float scale = params.getDouble("scale").floatValue();
                int structure_id = params.getInt("structure_id");

                PlayerStructure newStructure = player.buyStructure(structure_id, x, y, flip, scale);
                if (newStructure != null) {
                    response.putBool("success", true);
                    response.putSFSArray("properties", player.getProperties());
                    response.putSFSObject("user_structure", newStructure.toSFSObject());
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error buying stricture");
                }
                send("gs_buy_structure", response, sender);
                break;
            }
            case "gs_move_structure": {
                int x = params.getInt("pos_x");
                int y = params.getInt("pos_y");
                float scale = params.getDouble("scale").floatValue();
                long user_structure_id = params.getLong("user_structure_id");

                PlayerStructure newStructure = player.moveStructure(user_structure_id, x, y, scale);
                if (newStructure != null) {
                    response.putBool("success", true);
                    response.putLong("user_structure_id", user_structure_id);
                    response.putSFSObject("user_structure", newStructure.toSFSObject());

                    SFSArray properties = player.getProperties();

                    SFSObject prop = new SFSObject();
                    prop.putInt("pos_x", newStructure.pos_x);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putInt("pos_y", newStructure.pos_y);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putDouble("scale", newStructure.scale);
                    properties.addSFSObject(prop);

                    response.putSFSArray("properties", properties);
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error move stricture");
                }
                send("gs_move_structure", response, sender);
                break;
            }
            case "gs_flip_structure": {
                long user_structure_id = params.getLong("user_structure_id");

                PlayerStructure newStructure = player.flipStructure(user_structure_id);
                if (newStructure != null) {
                    response.putBool("success", true);
                    response.putLong("user_structure_id", user_structure_id);
                    response.putSFSObject("user_structure", newStructure.toSFSObject());

                    SFSArray properties = new SFSArray();

                    SFSObject prop = new SFSObject();
                    prop.putInt("flip", newStructure.flip);
                    properties.addSFSObject(prop);

                    response.putSFSArray("properties", properties);

                    send("gs_flip_structure", SFSObject.newFromJsonData("{\"success\":true}"), sender);
                    send("gs_update_structure", response, sender);
                    break;
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error flip stricture");
                }
                send("gs_flip_structure", response, sender);
                break;
            }
            case "gs_mute_structure": {
                long user_structure_id = params.getLong("user_structure_id");

                PlayerStructure newStructure = player.muteStructure(user_structure_id);
                if (newStructure != null) {
                    response.putBool("success", true);
                    response.putLong("user_structure_id", user_structure_id);
                    response.putSFSObject("user_structure", newStructure.toSFSObject());

                    SFSArray properties = new SFSArray();

                    SFSObject prop = new SFSObject();
                    prop.putInt("muted", newStructure.muted);
                    properties.addSFSObject(prop);

                    response.putSFSArray("properties", properties);

                    send("gs_mute_structure", SFSObject.newFromJsonData("{\"success\":true}"), sender);
                    send("gs_update_structure", response, sender);
                    break;
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error mute stricture");
                }
                send("gs_mute_structure", response, sender);
                break;
            }
            case "gs_sell_structure": {
                long user_structure_id = params.getLong("user_structure_id");

                if (player.sellStructure(user_structure_id) != null) {
                    response.putBool("success", true);
                    response.putLong("user_structure_id", user_structure_id);
                    response.putSFSArray("properties", player.getProperties());
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error sell stricture");
                }
                send("gs_sell_structure", response, sender);
                break;
            }
            case "gs_start_obstacle": {
                long user_structure_id = params.getLong("user_structure_id");

                PlayerStructure newStructure = player.startObstacle(user_structure_id);
                if (newStructure != null) {
                    response.putBool("success", true);
                    response.putLong("user_structure_id", user_structure_id);
                    response.putSFSObject("user_structure", newStructure.toSFSObject());

                    SFSArray properties = player.getProperties();

                    SFSObject prop = new SFSObject();
                    prop.putLong("date_created", newStructure.date_created);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putLong("building_completed", newStructure.building_completed);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putLong("last_collection", newStructure.last_collection);
                    properties.addSFSObject(prop);

                    response.putSFSArray("properties", properties);

                    send("gs_start_obstacle", response, sender);
                    break;
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error start obstacle");
                }
                send("gs_start_obstacle", response, sender);
                break;
            }
            case "gs_speed_up_structure":
            case "gs_clear_obstacle_speed_up": {
                long user_structure_id = params.getLong("user_structure_id");
                PlayerStructure newStructure = player.speedupStrucutre(user_structure_id);
                if (newStructure != null) {
                    response.putBool("success", true);
                    response.putLong("user_structure_id", user_structure_id);
                    response.putInt("diamonds_used", 1);

                    SFSArray properties = player.getProperties();

                    SFSObject prop = new SFSObject();
                    prop.putLong("building_completed", newStructure.building_completed);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putLong("date_created", newStructure.date_created);
                    properties.addSFSObject(prop);

                    response.putSFSArray("properties", properties);

                    send(cmd, response, sender);
                    send("gs_update_structure", response, sender);
                    break;
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error start obstacle");
                }
                send(cmd, response, sender);
                break;
            }
            case "gs_clear_obstacle": {
                long user_structure_id = params.getLong("user_structure_id");

                if (player.clearObstacle(user_structure_id) != null) {
                    response.putBool("success", true);
                    response.putLong("user_structure_id", user_structure_id);
                    response.putSFSArray("properties", player.getProperties());
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error clear obstacle");
                }
                send("gs_clear_obstacle", response, sender);
                break;
            }
            case "gs_start_upgrade_structure": {
                long user_structure_id = params.getLong("user_structure_id");

                PlayerStructure structure = player.upgradeStructure(user_structure_id);
                if (structure != null) {
                    response.putBool("success", true);
                    response.putLong("user_structure_id", user_structure_id);

                    SFSArray properties = player.getProperties();

                    SFSObject prop = new SFSObject();
                    prop.putInt("is_complete", structure.is_complete);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putInt("is_upgrading", structure.is_upgrading);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putLong("date_created", structure.date_created);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putLong("building_completed", structure.building_completed);
                    properties.addSFSObject(prop);

                    response.putSFSArray("properties", properties);
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error upgrading stricture");
                }
                send("gs_update_structure", response, sender);
                //send("gs_start_upgrade_structure", response, sender);
                break;
            }
            case "gs_finish_upgrade_structure": {
                long user_structure_id = params.getLong("user_structure_id");

                PlayerStructure newStructure = player.finishUpdradingStructure(user_structure_id);
                if (newStructure != null) {
                    response.putBool("success", true);
                    response.putLong("user_structure_id", user_structure_id);
                    response.putSFSObject("user_structure", newStructure.toSFSObject());

                    SFSArray properties = player.getProperties();

                    SFSObject prop = new SFSObject();
                    prop.putInt("structure", newStructure.structure);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putLong("building_completed", newStructure.building_completed);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putInt("is_complete", newStructure.is_complete);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putInt("is_upgrading", newStructure.is_upgrading);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putLong("date_created", newStructure.date_created);
                    properties.addSFSObject(prop);

                    response.putSFSArray("properties", properties);
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error move stricture");
                }
                send("gs_finish_upgrade_structure", response, sender);
                break;
            }
            //Social
            case "gs_referral_request": {
                String code = String.valueOf(params.getLong("referring_bbb_id"));
                switch (code) {
                    case "880080088":
                        player.coins = 999999999;
                        player.food = 999999999;
                        player.diamonds = 999999999;
                        player.level = 100;
                        player.xp = 999999999;

                        response.putBool("success", true);
                        response.putSFSArray("properties", player.getProperties());

                        send("gs_update_properties", response, sender);
                        break;
                    case "777000777":
                        player.coins = 5000;
                        player.food = 2500;
                        player.diamonds = 20;
                        player.level = 5;
                        player.xp = 550;

                        response.putBool("success", true);
                        response.putSFSArray("properties", player.getProperties());

                        send("gs_update_properties", response, sender);
                        break;
                    case "600666006":
                        //int newbbb_id = player.bbb_id;
                        //player.removePlayer();
                        //Player.createNewPlayer(player.player_id, newbbb_id);

                        //break;
                    }

                sender.disconnect(new IDisconnectionReason() {
                    @Override
                    public int getValue() {
                        return 0;
                    }

                    @Override
                    public byte getByteValue() {
                        return 0;
                    }
                });
            }
            //Backdrops | Lightings
            case "gs_request_backdrop_change": {
                response.putBool("success", true);
                response.putInt("backdrop_id", params.getInt("backdrop_id"));
                response.putBool("purshared", true);
                response.putSFSArray("properties", player.getProperties());

                send("gs_request_backdrop_change", response, sender);
                break;
            }
            case "gs_request_lighting_change": {
                response.putBool("success", true);
                response.putInt("lighting_id", params.getInt("lighting_id"));
                response.putBool("purshared", true);
                response.putSFSArray("properties", player.getProperties());

                send("gs_request_lighting_change", response, sender);
                break;
            }
            //Breeding | Hatching | Eggs
            case "gs_buy_egg": {
                int monster_id = params.getInt("monster_id");

                Long date_end = player.buyEgg(monster_id);
                if (date_end != null) {
                    SFSObject egg = new SFSObject();
                    egg.putLong("obj_end", date_end);
                    egg.putInt("obj_data", monster_id);

                    response.putSFSObject("user_egg", egg);
                    response.putSFSArray("properties", player.getProperties());
                    response.putBool("success", true);
                    response.putBool("remove_buyback", false);
                    send("gs_buy_egg", response, sender);
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error");
                    send("gs_buy_egg", response, sender);
                }
                break;
            }
            case "gs_speed_up_hatching": {
                long user_structure_id = params.getLong("user_structure_id");

                if (player.speedUpEgg(user_structure_id)) {
                    PlayerStructure playerStructure = PlayerStructure.getStructure(user_structure_id);

                    SFSObject egg = new SFSObject();
                    egg.putLong("obj_end", playerStructure.obj_end);
                    egg.putInt("obj_data", playerStructure.obj_data);

                    response.putSFSObject("user_egg", egg);
                    response.putSFSArray("properties", player.getProperties());
                    response.putBool("success", true);
                    send("gs_speed_up_hatching", response, sender);
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error");
                    send("gs_speed_up_hatching", response, sender);
                }
                break;
            }
            case "gs_sell_egg": {
                long user_structure_id = params.getLong("user_structure_id");

                if (player.sellEgg(user_structure_id)) {
                    response.putSFSArray("properties", player.getProperties());
                    response.putBool("success", true);
                    send("gs_sell_egg", response, sender);
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error");
                    send("gs_sell_egg", response, sender);
                }
                break;
            }
            case "gs_hatch_egg": {
                long user_structure_id = params.getLong("user_structure_id");
                int x = params.getInt("pos_x");
                int y = params.getInt("pos_y");
                boolean flip = params.getBool("flip");

                PlayerMonster newMonster = player.hatchEgg(user_structure_id, x, y, flip);
                if (newMonster != null) {
                    response.putBool("success", true);
                    response.putLong("user_monster_id", newMonster.user_monster_id);
                    response.putLong("user_structure_id", user_structure_id);
                    response.putSFSObject("monster", newMonster.toSFSObject());
                    response.putSFSObject("structure", PlayerStructure.getStructure(user_structure_id).toSFSObject());
                    response.putSFSArray("properties", player.getProperties());
                    send("gs_hatch_egg", response, sender);
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error");
                    send("gs_hatch_egg", response, sender);
                }
                break;
            }
            //Monsters
            case "gs_move_monster": {
                int x = params.getInt("pos_x");
                int y = params.getInt("pos_y");
                float volume = 1.0F;
                long user_monster_id = params.getLong("user_monster_id");

                PlayerMonster newMonster = player.moveMonster(user_monster_id, x, y, volume);
                if (newMonster != null) {
                    response.putBool("success", true);
                    response.putLong("user_monster_id", user_monster_id);
                    response.putSFSObject("monster", newMonster.toSFSObject());

                    SFSArray properties = new SFSArray();

                    SFSObject prop = new SFSObject();
                    prop.putInt("pos_x", newMonster.pos_x);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putInt("pos_y", newMonster.pos_y);
                    properties.addSFSObject(prop);

                    prop = new SFSObject();
                    prop.putDouble("volume", newMonster.volume);
                    properties.addSFSObject(prop);

                    response.putSFSArray("properties", properties);
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error move monster");
                }
                send("gs_move_monster", response, sender);
                break;
            }
            case "gs_flip_monster": {
                long user_monster_id = params.getLong("user_monster_id");

                PlayerMonster newMonster = player.flipMonster(user_monster_id);
                if (newMonster != null) {
                    response.putBool("success", true);
                    response.putLong("user_monster_id", user_monster_id);
                    response.putSFSObject("monster", newMonster.toSFSObject());

                    SFSArray properties = new SFSArray();

                    SFSObject prop = new SFSObject();
                    prop.putInt("flip", newMonster.flip);
                    properties.addSFSObject(prop);

                    response.putSFSArray("properties", properties);
                    send("gs_flip_monster", SFSObject.newFromJsonData("{\"success\":true}"), sender);
                    send("gs_update_monster", response, sender);
                    break;
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error flip monster");
                    send("gs_flip_monster", response, sender);
                }
                break;
            }
            case "gs_mute_monster": {
                long user_monster_id = params.getLong("user_monster_id");

                PlayerMonster newMonster = player.muteMonster(user_monster_id);
                if (newMonster != null) {
                    response.putBool("success", true);
                    response.putLong("user_monster_id", user_monster_id);
                    response.putSFSObject("monster", newMonster.toSFSObject());

                    SFSArray properties = new SFSArray();

                    SFSObject prop = new SFSObject();
                    prop.putInt("muted", newMonster.muted);
                    properties.addSFSObject(prop);

                    response.putSFSArray("properties", properties);
                    send("gs_mute_monster", SFSObject.newFromJsonData("{\"success\":true}"), sender);
                    send("gs_update_monster", response, sender);
                    break;
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error mute monster");
                    send("gs_mute_monster", response, sender);
                }
                break;
            }
            case "gs_sell_monster": {
                long user_monster_id = params.getLong("user_monster_id");

                if (player.sellMonster(user_monster_id) != null) {
                    response.putBool("success", true);
                    response.putLong("user_monster_id", user_monster_id);
                    response.putSFSArray("properties", player.getProperties());
                } else {
                    response.putBool("success", false);
                    response.putUtfString("message", "Error sell monster");
                }
                send("gs_sell_monster", response, sender);
                break;
            }
            default:
                params.putBool("success", false);
                send(cmd, params, sender);
        }
    }

}


