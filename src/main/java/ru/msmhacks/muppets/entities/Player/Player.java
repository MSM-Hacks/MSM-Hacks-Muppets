package ru.msmhacks.muppets.entities.Player;

import com.alexfu.sqlitequerybuilder.api.Column;
import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import ru.msmhacks.muppets.MuppetsExtension;
import ru.msmhacks.muppets.entities.Island;
import ru.msmhacks.muppets.entities.Level;
import ru.msmhacks.muppets.entities.Monster;
import ru.msmhacks.muppets.entities.Structure;
import ru.msmhacks.muppets.managers.PlayerDatabaseManager;
import ru.msmhacks.muppets.managers.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static ru.msmhacks.muppets.entities.Player.PlayerStructure.*;
import static ru.msmhacks.muppets.managers.PlayerDatabaseManager.stmt;
import static ru.msmhacks.muppets.managers.Utils.getSpeedupCost;
import static ru.msmhacks.muppets.managers.Utils.log;

public class Player {

    public static HashMap<String, Player> players = new HashMap<>();

    public String player_id;

    public String display_name = "New player";

    public int coins = 1200;
    public int diamonds = 17;
    public int food = 0;
    public int xp = 0;
    public int level = 1;
    public int bbb_id = 1000;
    public int daily_reward_level = 1;

    public long active_island = 101;

    public SFSArray backdrops = new SFSArray();
    public SFSArray lighting = new SFSArray();

    public static Player createNewPlayer(String player_id, int bbb_id) {
        log("Creating new player");
        Player player = new Player();
        player.player_id = player_id;
        player.bbb_id = bbb_id;

        PlayerIsland first_island = PlayerIsland.createNewIsland(bbb_id, 1);
        player.active_island = first_island.user_island_id;

        players.put(player_id, player);
        try {player.importToDB();} catch (SQLException ignored) {}
        return player;
    }

    public static Player getPlayer(String player_id, int bbb_id) {
        if (players.containsKey(player_id)) {
            return players.get(player_id);
        }
        Player player = createNewPlayer(player_id, bbb_id);
        return player;
    }

    public SFSObject toSFSObject() {
        SFSObject player = new SFSObject();
        SFSObject player_object = new SFSObject();
        player_object.putInt("coins", coins);
        player_object.putInt("diamonds", diamonds);
        player_object.putInt("food", food);
        player_object.putInt("xp", xp);
        player_object.putInt("level", level);
        player_object.putInt("bbb_id", bbb_id);
        player_object.putInt("user_id", bbb_id);
        player_object.putInt("daily_reward_level", daily_reward_level);
        player_object.putLong("active_island", active_island);

        player_object.putUtfString("display_name", display_name);

        player_object.putSFSArray("backdrops", new SFSArray());
        player_object.putSFSArray("lighting", new SFSArray());
        player_object.putSFSArray("hidden_objects", new SFSArray());

        SFSArray islands = new SFSArray();
        for (PlayerIsland pl_island : PlayerIsland.getPlayerIslands(bbb_id)) {
            islands.addSFSObject(pl_island.toSFSObject());
        }
        player_object.putSFSArray("islands", islands);

        player.putSFSObject("player_object", player_object);
        return player;
    }

    public static void main(String[] args) {
        log(getPlayer("dasasddas", 12).toSFSObject().toJson());
    }

    public static void dropPlayersDatabase() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS players");
    }

    public void importToDB() throws SQLException {
        String sql = SQLiteQueryBuilder.insert()
                .into("players")
                .columns("coins", "diamonds", "food", "xp", "level", "bbb_id", "user_id",
                        "daily_reward_level", "active_island", "display_name")
                .values(this.coins, this.diamonds, this.food, this.xp, this.level, this.bbb_id, this.player_id,
                        this.daily_reward_level, this.active_island, this.display_name)
                .build();

        stmt.executeUpdate(sql);
    }

    public static void initPlayersDatabase() throws SQLException {
        players = new HashMap<>();

        String sql = SQLiteQueryBuilder.create()
                .table("players")
                .ifNotExists()
                .column(new Column("coins", ColumnType.INTEGER))
                .column(new Column("diamonds", ColumnType.INTEGER))
                .column(new Column("food", ColumnType.INTEGER))
                .column(new Column("xp", ColumnType.INTEGER))
                .column(new Column("level", ColumnType.INTEGER))
                .column(new Column("bbb_id", ColumnType.INTEGER))
                .column(new Column("user_id", ColumnType.TEXT))
                .column(new Column("daily_reward_level", ColumnType.INTEGER))
                .column(new Column("active_island", ColumnType.INTEGER))
                .column(new Column("display_name", ColumnType.TEXT))
                .toString();
        stmt.executeUpdate(sql);

        sql = SQLiteQueryBuilder.select("*")
                .from("players")
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Player pl = new Player();
                pl.coins = rs.getInt("coins");
                pl.diamonds = rs.getInt("diamonds");
                pl.food = rs.getInt("food");
                pl.xp = rs.getInt("xp");
                pl.level = rs.getInt("level");
                pl.bbb_id = rs.getInt("bbb_id");
                pl.player_id = rs.getString("user_id");
                pl.daily_reward_level = rs.getInt("daily_reward_level");
                pl.active_island = rs.getLong("active_island");
                pl.display_name = rs.getString("display_name");
                players.put(pl.player_id, pl);
            }
        } catch (SQLException e) {}
    }

    public SFSArray getProperties() {
        SFSArray properties = new SFSArray();

        SFSObject tmp = new SFSObject();
        tmp.putInt("coins", coins);
        properties.addSFSObject(tmp);

        tmp = new SFSObject();
        tmp.putInt("diamonds", diamonds);
        properties.addSFSObject(tmp);

        tmp = new SFSObject();
        tmp.putInt("food", food);
        properties.addSFSObject(tmp);

        tmp = new SFSObject();
        tmp.putInt("xp", xp);
        properties.addSFSObject(tmp);

        tmp = new SFSObject();
        tmp.putInt("level", level);
        properties.addSFSObject(tmp);

        return properties;
    }

    public boolean addCoins(int delta) {
        if (coins+delta>0) {
            coins += delta;
            PlayerDatabaseManager.executeVoid("UPDATE players SET coins = %s WHERE user_id = %s;", new Object[]{coins, player_id});
            return true;
        } else {
            return false;
        }
    }

    public boolean addFood(int delta) {
        if (food+delta>0) {
            food += delta;
            PlayerDatabaseManager.executeVoid("UPDATE players SET food = %s WHERE user_id = %s;", new Object[]{food, player_id});
            return true;
        } else {
            return false;
        }
    }

    public boolean addDiamonds(int delta) {
        if (diamonds+delta>0) {
            diamonds += delta;
            PlayerDatabaseManager.executeVoid("UPDATE players SET diamonds = %s WHERE user_id = %s;", new Object[]{diamonds, player_id});
            return true;
        } else {
            return false;
        }
    }

    public boolean addXp(int delta) {
        if (xp+delta>0) {
            xp += delta;
            Level maxLvl = Level.getLevelByID(1);
            for (Level lvl: Level.levels_fastdb.values()) {
                if (xp > lvl.xp && lvl.level > maxLvl.level) {
                    maxLvl = lvl;
                }
            }
            if (maxLvl.level > level) {
                addDiamonds(maxLvl.diamond_reward);
            }
            level = maxLvl.level;
            PlayerDatabaseManager.executeVoid("UPDATE players SET xp = %s, level = %s WHERE user_id = %s;", new Object[]{xp, level, player_id});
            return true;
        } else {
            return false;
        }
    }

    public boolean addBalances(int coins, int diamonds, int food, int xp, boolean checkOnly) {
        if (this.diamonds + diamonds < 0) return false;
        if (this.coins + coins < 0) return false;
        if (this.food + food < 0) return false;
        if (this.xp + xp < 0) return false;

        if (!checkOnly) {
            addDiamonds(diamonds);
            addCoins(coins);
            addFood(food);
            addXp(xp);
        }

        return true;
    }

    public long buyIsland(int island_id) {
        Island island = Island.getIslandByID(island_id);
        if (PlayerIsland.isPlayerHasIslandType(bbb_id, island_id))
            return -1;
        if (addBalances(island.cost_coins*-1, island.cost_diamonds*-1, 0, 0, false)) {
            PlayerIsland playerIsland = PlayerIsland.createNewIsland(bbb_id, island_id);
            return playerIsland.user_island_id;
        }
        return -1;
    }

    public boolean changeIsland(long user_island_id) {
        if (PlayerIsland.isPlayerHasIsland(bbb_id, user_island_id)) {
            active_island = user_island_id;
            PlayerDatabaseManager.executeVoid("UPDATE players SET active_island = %s WHERE user_id = %s;", new Object[]{user_island_id, player_id});
            return true;
        } else {
            return false;
        }
    }

    public PlayerStructure buyStructure(int structure_id, int x, int y, int flip, float scale) {
        Structure structure = Structure.getStructureByID(structure_id);

        if (structure.structure_type != "decoration" && PlayerStructure.isIslandHasStructure(active_island, structure_id)) {
            return null;
        }
        if (addBalances(structure.cost_coins*-1, structure.cost_diamonds*-1, 0, structure.xp, false)) {
            PlayerStructure playerStructure = PlayerStructure.createNewStructure(active_island, structure_id, x, y, flip, scale);
            return playerStructure;
        }
        return null;
    }

    public PlayerStructure moveStructure(long user_structure_id, int x, int y, float scale) {
        if (PlayerStructure.isIslandHasStructure(active_island, user_structure_id)) {
            PlayerStructure.moveStructure(user_structure_id, x, y, scale);
            return getStructure(user_structure_id);
        }
        return null;
    }

    public PlayerStructure flipStructure(long user_structure_id) {
        if (PlayerStructure.isIslandHasStructure(active_island, user_structure_id)) {
            PlayerStructure.flipStructure(user_structure_id);
            return getStructure(user_structure_id);
        }
        return null;
    }

    public Boolean sellStructure(long user_structure_id) {
        if (PlayerStructure.isIslandHasStructure(active_island, user_structure_id)) {
            Structure structure = Structure.structures_fastdb.get(getStructure(user_structure_id).structure);
            if (getStructure(user_structure_id).is_complete==1) {
                addBalances(structure.cost_coins * -1, structure.cost_diamonds * -1, 0, 0, false);
                PlayerStructure.removeStructure(user_structure_id);
                return true;
            }
        }
        return null;
    }

    public PlayerStructure startObstacle(long user_structure_id) {
        if (PlayerStructure.isIslandHasStructure(active_island, user_structure_id)) {
            Structure structure = Structure.structures_fastdb.get(getStructure(user_structure_id).structure);
            if (addBalances(structure.cost_coins*-1, structure.cost_diamonds*-1, 0, 0, false)) {
                PlayerStructure.startClearingStructure(user_structure_id);
                return PlayerStructure.getStructure(user_structure_id);
            }
        }
        return null;
    }
    public PlayerStructure speedupObstacle(long user_structure_id) {
        if (PlayerStructure.isIslandHasStructure(active_island, user_structure_id)) {
            PlayerStructure playerStructure = PlayerStructure.getStructure(user_structure_id);
            Structure structure = Structure.structures_fastdb.get(getStructure(user_structure_id).structure);
            if (addBalances(0, getSpeedupCost(System.currentTimeMillis(), playerStructure.building_completed)*-1, 0, 0, false)) {
                PlayerStructure.speedupStructure(user_structure_id);
                log(PlayerStructure.getStructure(user_structure_id).toSFSObject().getDump());
                return PlayerStructure.getStructure(user_structure_id);
            }
        }
        return null;
    }
    public Boolean clearObstacle(long user_structure_id) {
        if (PlayerStructure.isIslandHasStructure(active_island, user_structure_id)) {
            Structure structure = Structure.structures_fastdb.get(getStructure(user_structure_id).structure);
            addBalances(0, 0, 0, structure.xp, false);
            PlayerStructure.removeStructure(user_structure_id);
            return true;
        }
        return null;
    }

    public PlayerStructure upgradeStructure(long user_structure_id) {
        if (PlayerStructure.isIslandHasStructure(active_island, user_structure_id)) {
            PlayerStructure playerStructure = getStructure(user_structure_id);
            Structure oldStructure = Structure.getStructureByID(playerStructure.structure);

            if (oldStructure.upgrades_to == 0 || playerStructure.is_upgrading == 1) {return null;}
            Structure newStructure = Structure.getStructureByID(oldStructure.upgrades_to);

            if (addBalances(newStructure.cost_coins, newStructure.cost_diamonds, 0, 0, false)) {
                startUpgradingStructure(user_structure_id);
                return playerStructure;
            }
        }
        return null;
    }

    public PlayerStructure finishUpdradingStructure(long user_structure_id) {
        if (PlayerStructure.isIslandHasStructure(active_island, user_structure_id) && PlayerStructure.finishUpgradingStructure(user_structure_id)) {
            Structure structure = Structure.structures_fastdb.get(getStructure(user_structure_id).structure);
            addBalances(0, 0, 0, structure.xp, false);
            return getStructure(user_structure_id);
        }
        return null;
    }
}
