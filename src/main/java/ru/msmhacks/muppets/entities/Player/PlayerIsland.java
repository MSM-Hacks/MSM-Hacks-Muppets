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
import static ru.msmhacks.muppets.managers.Utils.randInt;

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
    public int[] beds = {4,0};

    public SFSArray monsters = new SFSArray();

    public static PlayerIsland createNewIsland(int bbb_id, int island_id) {
        Utils.log("Creating new island");

        PlayerIsland island = new PlayerIsland();
        island.user = bbb_id;
        island.user_island_id = islands.size();
        island.island = island_id;
        islands.put(island.user_island_id, island);

        island.fillIsland();

        try {island.importToDB();} catch (SQLException ignored) {}
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
    public static void initPlayerIslandsDatabase() throws SQLException {
        islands = new HashMap<>();

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

        sql = SQLiteQueryBuilder.select("*")
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

    public void fillIsland() {
        int[][] places_3; //Dumpster, Crates, Piano, Heap, Bucket, Luggage
        int[][] places_2; //Bag 02
        int[][] places_1; //Bag 01, Pylon

        int[] objects_3;
        int[] objects_2;
        int[] objects_1;

        int[] nursery;
        int[] castle;

        switch (island) {
            case 1: {
                places_3 = new int[][]{{35,27},{31,27},{39,22},{39,19},{45,16},{34,4},{34,10},{31,10},{28,10}};
                places_2 = new int[][]{{41,11},{38,24},{45,13},{40,15},{38,13},{38,9},{32,4},{25,11},{22,15},{26,25}};
                places_1 = new int[][]{{33,24},{37,22},{38,22},{36,28},{33,28},{46,17},{41,9},{38,11},{40,13},{38,15},{39,16},{36,11},{37,7},{37,4},{34,1},{28,7},{22,12},{27,11},{30,11},{25,14},{27,8},{26,9},{36,7},{28,17},{35,19},{42,21}};

                objects_3 = new int[]{200, 206, 199, 203, 204, 205};
                objects_2 = new int[]{202};
                objects_1 = new int[]{205, 201};

                nursery = new int[]{10, 20};
                castle = new int[]{208, 33, 34};
                break;
            }
            case 2: {
                places_3 = new int[][]{{45,17},{32,5},{27,2}};
                places_2 = new int[][]{{46,20},{36,6},{25,8},{30,2},{41,24},{43,13},{32,2},{40,9},{30,9}};
                places_1 = new int[][]{{36,12},{41,22},{44,23},{49,19},{48,17},{46,23},{45,13},{38,18},{39,24},{30,5},{38,6},{26,4},{25,3},{26,5},{27,8},{42,9},{42,11},{44,16},{24,9},{39,7}};

                objects_3 = new int[]{198, 255, 197, 252, 253};
                objects_2 = new int[]{251};
                objects_1 = new int[]{254, 250};

                nursery = new int[]{10, 10};
                castle = new int[]{213,49,23};
                break;
            }
            case 3: {
                places_3 = new int[][]{};
                places_2 = new int[][]{};
                places_1 = new int[][]{};

                objects_3 = new int[]{188, 195, 189, 192, 193, 196};
                objects_2 = new int[]{191};
                objects_1 = new int[]{194, 190};

                nursery = new int[]{17, 13};
                castle = new int[]{218,48,28};
                break;
            }
            case 4: {
                places_3 = new int[][]{};
                places_2 = new int[][]{};
                places_1 = new int[][]{};

                objects_3 = new int[]{351, 352, 353, 355, 356};
                objects_2 = new int[]{354};
                objects_1 = new int[]{350};

                nursery = new int[]{25, 7};
                castle = new int[]{259,23,26};
                break;
            }
            case 5: {
                places_3 = new int[][]{};
                places_2 = new int[][]{};
                places_1 = new int[][]{};

                objects_3 = new int[]{359, 360, 362, 363};
                objects_2 = new int[]{361};
                objects_1 = new int[]{357, 358};

                nursery = new int[]{41, 26};
                castle = new int[]{265,40,39};
                break;
            }
            default: {
                places_3 = new int[][]{};
                places_2 = new int[][]{};
                places_1 = new int[][]{};

                objects_3 = new int[]{};
                objects_2 = new int[]{};
                objects_1 = new int[]{};

                nursery = new int[]{1,1,1};
                castle = new int[]{1,1,1};
                break;
            }
        }

        for (int[] place_3: places_3) {
                PlayerStructure st = PlayerStructure.createNewStructure(user_island_id, objects_3[randInt(0, objects_3.length - 1)],
                        place_3[0], place_3[1], randInt(0, 1), 1.0F);
                st.is_complete = 1;
                st.is_upgrading = 0;
                st.building_completed = null;
                st.date_created = null;
                st.last_collection = null;
        }

        for (int[] place_2: places_2) {
                PlayerStructure st = PlayerStructure.createNewStructure(user_island_id, objects_2[randInt(0, objects_2.length - 1)],
                        place_2[0], place_2[1], randInt(0, 1), 1.0F);
                st.is_complete = 1;
                st.is_upgrading = 0;
                st.building_completed = null;
                st.date_created = null;
                st.last_collection = null;
        }

        for (int[] place_1: places_1) {
                PlayerStructure st = PlayerStructure.createNewStructure(user_island_id, objects_1[randInt(0, objects_1.length - 1)],
                        place_1[0], place_1[1], randInt(0, 1), 1.0F);
                st.is_complete = 1;
                st.is_upgrading = 0;
                st.building_completed = null;
                st.date_created = null;
                st.last_collection = null;
        }

        PlayerStructure.createNewStructure(user_island_id, 1, nursery[0], nursery[1], 0, 1.0F);
        PlayerStructure.createNewStructure(user_island_id, castle[0], castle[1], castle[2], 0, 1.0F);
    }

    public static void main(String[] args) {
        PlayerIsland a = createNewIsland(1,1);
        PlayerStructure st = PlayerStructure.createNewStructure(a.user_island_id, 12,
                5, 5, randInt(0, 1), 1.0F);
        st.is_complete = 1;
        st.is_upgrading = 0;
        st.building_completed = null;
        st.date_created = null;
        st.last_collection = null;
        System.out.println(st.toSFSObject().getDump());
    }
}
