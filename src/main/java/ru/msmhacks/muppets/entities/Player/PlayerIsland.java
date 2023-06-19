package ru.msmhacks.muppets.entities.Player;

import com.alexfu.sqlitequerybuilder.api.Column;
import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import ru.msmhacks.muppets.managers.PlayerDatabaseManager;
import ru.msmhacks.muppets.managers.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static ru.msmhacks.muppets.managers.PlayerDatabaseManager.stmt;

public class PlayerIsland {
    public static HashMap<Long, PlayerIsland> islands = new HashMap<>();

    public long user_island_id = 0;
    public long user = 1000;
    public long upgrading_until = 0;
    public long upgrade_started = 0;

    public int likes = 0;
    public int level = 1;
    public int backdrop_id = 0;
    public int lighting_id = 0;
    public int island = 0;

    public SFSArray monsters = new SFSArray();

    public static PlayerIsland createNewIsland(int bbb_id, int island_id) {
        Utils.log("Creating new island");
        PlayerIsland island = new PlayerIsland();
        island.user = bbb_id;
        island.user_island_id = islands.size();
        island.island = island_id;
        islands.put(island.user_island_id, island);
        try {island.importToDB();} catch (SQLException ignored) {}

        PlayerStructure.createNewStructure(island.user_island_id, 1, 25, 25,0, 1.0F);
        return island;
    }

    public static PlayerIsland getIsland(long user_island_id) {
        if (islands.containsKey(user_island_id)) {
            return islands.get(user_island_id);
        }
        return null;
    }

    public SFSObject toSFSObject() {
        SFSObject island = new SFSObject();

        island.putLong("user_island_id", user_island_id);
        island.putLong("user", user);
        island.putLong("upgrading_until", upgrading_until);
        island.putLong("upgrade_started", upgrade_started);

        island.putInt("likes", likes);
        island.putInt("level", level);
        island.putInt("backdrop_id", backdrop_id);
        island.putInt("lighting_id", lighting_id);
        island.putInt("island", this.island);

        SFSArray structures = new SFSArray();
        for (PlayerStructure structure : PlayerStructure.getStructuresOnIsland(user_island_id)) {
            structures.addSFSObject(structure.toSFSObject());
        }
        island.putSFSArray("structures", structures);

        island.putSFSArray("monsters", monsters);
        island.putSFSArray("structures", structures);

        return island;
    }

    public static void dropPlayerIslandsDatabase() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS player_islands");
        String sql = SQLiteQueryBuilder.create()
                .table("player_islands")
                .ifNotExists()
                .column(new Column("user_island_id", ColumnType.INTEGER))
                .column(new Column("user", ColumnType.INTEGER))
                .column(new Column("upgrading_until", ColumnType.INTEGER))
                .column(new Column("upgrade_started", ColumnType.INTEGER))
                .column(new Column("likes", ColumnType.INTEGER))
                .column(new Column("level", ColumnType.INTEGER))
                .column(new Column("backdrop_id", ColumnType.INTEGER))
                .column(new Column("lighting_id", ColumnType.INTEGER))
                .column(new Column("island", ColumnType.INTEGER))
                .toString();
        stmt.executeUpdate(sql);
    }

    public void importToDB() throws SQLException {
        String sql = SQLiteQueryBuilder.insert()
                .into("player_islands")
                .columns("user_island_id", "user", "upgrading_until", "upgrade_started", "likes", "level",
                        "backdrop_id", "lighting_id", "island")
                .values(this.user_island_id, this.user, this.upgrading_until, this.upgrade_started, this.likes, this.level,
                        this.backdrop_id, this.lighting_id, this.island)
                .build();

        stmt.executeUpdate(sql);
    }

    public static void initPlayerIslandsDatabase() {
        islands = new HashMap<>();

        String sql = SQLiteQueryBuilder.select("*")
                .from("player_islands")
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                PlayerIsland pi = new PlayerIsland();
                pi.user_island_id = rs.getLong("user_island_id");
                pi.user = rs.getLong("user");
                pi.upgrading_until = rs.getLong("upgrading_until");
                pi.upgrade_started = rs.getLong("upgrade_started");
                pi.likes = rs.getInt("likes");
                pi.level = rs.getInt("level");
                pi.backdrop_id = rs.getInt("backdrop_id");
                pi.lighting_id = rs.getInt("lighting_id");
                pi.island = rs.getInt("island");
                islands.put(pi.user_island_id, pi);
            }
        } catch (SQLException e) {}
    }

    public static PlayerIsland[] getPlayerIslands(long bbb_id) {
        String sql = SQLiteQueryBuilder.select("*")
                .from("player_islands")
                .where("user = " + bbb_id)
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            ArrayList<PlayerIsland> player_islands = new ArrayList<PlayerIsland>();
            while (rs.next()) {
                player_islands.add(getIsland(rs.getLong("user_island_id")));
            }
            PlayerIsland[] playerIslands = new PlayerIsland[player_islands.size()];

            int index = 0;
            for (PlayerIsland player_island : player_islands) {
                playerIslands[index] = player_island;
                index++;
            }
            return playerIslands;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isPlayerHasIsland(long bbb_id, long user_island_id) {
        String sql = SQLiteQueryBuilder.select("*")
                .from("player_islands")
                .where("user = " + bbb_id + " AND user_island_id = " + user_island_id)
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isPlayerHasIslandType(long bbb_id, int island_id) {
        String sql = SQLiteQueryBuilder.select("*")
                .from("player_islands")
                .where("user = " + bbb_id + " AND island = " + island_id)
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
