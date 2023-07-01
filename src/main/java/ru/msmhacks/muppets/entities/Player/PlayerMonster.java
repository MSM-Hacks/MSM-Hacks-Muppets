package ru.msmhacks.muppets.entities.Player;

import com.alexfu.sqlitequerybuilder.api.Column;
import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.smartfoxserver.v2.entities.data.SFSObject;
import ru.msmhacks.muppets.entities.Monster;
import ru.msmhacks.muppets.managers.PlayerDatabaseManager;
import ru.msmhacks.muppets.managers.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static ru.msmhacks.muppets.managers.PlayerDatabaseManager.getMaximumData;
import static ru.msmhacks.muppets.managers.PlayerDatabaseManager.stmt;
import static ru.msmhacks.muppets.managers.Utils.log;

public class PlayerMonster {
    public static HashMap<Long, PlayerMonster> monsters = new HashMap<>();

    public long user_monster_id = 0;
    public long user_island_id = 0;

    public int pos_x = 0;
    public int pos_y = 0;
    public int flip = 0;
    public int muted = 0;
    public int level = 1;
    public int happiness = 0;
    public int monster = 0;
    public Integer collected_coins = 0;
    public int times_fed = 0;
    public float volume = 1.0F;


    public Long date_created = 0L;
    public Long last_collection = 0L;


    public static PlayerMonster createNewMonster(long user_island_id, int monster_id, int x, int y, int flip, float volume) {
        Utils.log("Creating new monster");
        PlayerMonster monster = new PlayerMonster();
        monster.user_island_id = user_island_id;
        monster.user_monster_id = getMaximumData("player_monsters", "user_monster_id")==null?0:(long)getMaximumData("player_monsters", "user_monster_id")+1;;
        monster.monster = monster_id;
        monster.pos_x = x;
        monster.pos_y = y;
        monster.flip = flip;
        monster.volume = volume;
        monster.date_created = System.currentTimeMillis();
        monster.last_collection = System.currentTimeMillis();

        monsters.put(monster.user_monster_id, monster);
        try {monster.importToDB();} catch (SQLException ignored) {}
        return monster;
    }

    public static PlayerMonster getMonster(long user_monster_id) {
        if (monsters.containsKey(user_monster_id)) {
            return monsters.get(user_monster_id);
        }
        return null;
    }

    public SFSObject toSFSObject() {
        SFSObject monster = new SFSObject();

        monster.putLong("user_monster_id", user_monster_id);
        monster.putLong("user_island_id", user_island_id);

        monster.putInt("pos_x", pos_x);
        monster.putInt("pos_y", pos_y);
        monster.putInt("flip", flip);
        monster.putInt("muted", muted);
        monster.putInt("level", level);
        monster.putInt("happiness", happiness);
        monster.putInt("monster", this.monster);
        if (collected_coins!=null) monster.putInt("collected_coins", collected_coins);
        monster.putInt("times_fed", times_fed);

        monster.putFloat("volume", volume);

        monster.putLong("date_created", date_created);
        monster.putLong("last_collection", last_collection);

        return monster;
    }

    public static void dropPlayerMonstersDatabase() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS player_monsters");
    }

    public void importToDB() throws SQLException {
        String sql = SQLiteQueryBuilder.insert()
                .into("player_monsters")
                .columns("user_monster_id", "user_island_id", "pos_x", "pos_y", "flip", "muted",
                        "level", "happiness", "monster", "volume", "collected_coins",
                        "date_created", "last_collection", "times_fed")
                .values(this.user_monster_id, this.user_island_id, this.pos_x, this.pos_y, this.flip, this.muted,
                        this.level, this.happiness, this.monster, this.volume, this.collected_coins,
                        this.date_created, this.last_collection, this.times_fed)
                .build();

        stmt.executeUpdate(sql);
    }

    public static void initPlayerMonstersDatabase() throws SQLException {
        monsters = new HashMap<>();

        String sql = SQLiteQueryBuilder.create()
                .table("player_monsters")
                .ifNotExists()
                .column(new Column("user_monster_id", ColumnType.INTEGER))
                .column(new Column("user_island_id", ColumnType.INTEGER))
                .column(new Column("pos_x", ColumnType.INTEGER))
                .column(new Column("pos_y", ColumnType.INTEGER))
                .column(new Column("flip", ColumnType.INTEGER))
                .column(new Column("muted", ColumnType.INTEGER))
                .column(new Column("level", ColumnType.INTEGER))
                .column(new Column("times_fed", ColumnType.INTEGER))
                .column(new Column("happiness", ColumnType.INTEGER))
                .column(new Column("monster", ColumnType.INTEGER))
                .column(new Column("volume", ColumnType.REAL))
                .column(new Column("collected_coins", ColumnType.INTEGER))
                .column(new Column("date_created", ColumnType.INTEGER))
                .column(new Column("last_collection", ColumnType.INTEGER))
                .toString();
        stmt.executeUpdate(sql);

        sql = SQLiteQueryBuilder.select("*")
                .from("player_monsters")
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                PlayerMonster pi = new PlayerMonster();
                pi.user_monster_id = rs.getLong("user_monster_id");
                pi.user_island_id = rs.getLong("user_island_id");
                pi.pos_x = rs.getInt("pos_x");
                pi.pos_y = rs.getInt("pos_y");
                pi.flip = rs.getInt("flip");
                pi.muted = rs.getInt("muted");
                pi.level = rs.getInt("level");
                pi.times_fed = rs.getInt("times_fed");
                pi.happiness = rs.getInt("happiness");
                pi.monster = rs.getInt("monster");
                pi.volume = rs.getFloat("volume");
                pi.collected_coins = rs.getInt("collected_coins");
                pi.date_created = rs.getLong("date_created");
                pi.last_collection = rs.getLong("last_collection");

                monsters.put(pi.user_monster_id, pi);
            }
        } catch (SQLException e) {}
    }

    public static PlayerMonster[] getMonstersOnIsland(long user_island_id) {
        String sql = SQLiteQueryBuilder.select("*")
                .from("player_monsters")
                .where("user_island_id = " + user_island_id)
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            ArrayList<PlayerMonster> monsters_on_island = new ArrayList<PlayerMonster>();
            while (rs.next()) {
                monsters_on_island.add(getMonster(rs.getLong("user_monster_id")));
            }
            PlayerMonster[] monstersOnIsland = new PlayerMonster[monsters_on_island.size()];

            int index = 0;
            for (PlayerMonster monsterOnIsland : monsters_on_island) {
                monstersOnIsland[index] = monsterOnIsland;
                index++;
            }
            return monstersOnIsland;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isIslandHasMonster(long user_island_id, long user_monster_id) {
        String sql = SQLiteQueryBuilder.select("*")
                .from("player_monsters")
                .where("user_island_id = " + user_island_id + " AND user_monster_id = " + user_monster_id)
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void moveMonster(long user_monster_id, int x, int y, float volume) {
        getMonster(user_monster_id).pos_x = x;
        getMonster(user_monster_id).pos_y = y;
        getMonster(user_monster_id).volume = volume;

        PlayerDatabaseManager.executeVoid("UPDATE player_monsters SET pos_x = %s, pos_y = %s, volume = %s WHERE user_monster_id = %s;",
                new Object[]{x, y, volume, user_monster_id});
    }
    
    public static void flipMonster(long user_monster_id) {
        int flip = getMonster(user_monster_id).flip;
        int newFlip = flip==0?1:0;
        getMonster(user_monster_id).flip = newFlip;

        PlayerDatabaseManager.executeVoid("UPDATE player_monsters SET flip = %s WHERE user_monster_id = %s;",
                new Object[]{newFlip, user_monster_id});
    }

    public static void muteMonster(long user_monster_id) {
        int muted = getMonster(user_monster_id).muted;
        int newMuted = muted==0?1:0;
        getMonster(user_monster_id).muted = newMuted;

        PlayerDatabaseManager.executeVoid("UPDATE player_monsters SET muted = %s WHERE user_monster_id = %s;",
                new Object[]{newMuted, user_monster_id});
    }

    public static void removeMonster(long user_monster_id) {
        monsters.remove(user_monster_id);

        PlayerDatabaseManager.executeVoid("DELETE FROM player_monsters WHERE user_monster_id = %s;",
                new Object[]{user_monster_id});
    }

    public static void feedMonster(long user_monster_id) {
        PlayerMonster playerMonster = getMonster(user_monster_id);
        playerMonster.times_fed += 1;
        if (playerMonster.times_fed >= 4) {
            playerMonster.times_fed = 0;
            playerMonster.level += 1;
        }

        PlayerDatabaseManager.executeVoid("UPDATE player_monsters SET times_fed = %s, level = %s WHERE user_monster_id = %s;",
                new Object[]{playerMonster.times_fed, playerMonster.level, user_monster_id});
    }

    public static void collectFromMonster(long user_monster_id) {
        PlayerMonster playerMonster = getMonster(user_monster_id);
        playerMonster.last_collection = System.currentTimeMillis();
        playerMonster.collected_coins = null;

        PlayerDatabaseManager.executeVoid("UPDATE player_monsters SET last_collection = %s, collected_coins = %s WHERE user_monster_id = %s;",
                new Object[]{playerMonster.last_collection, 0, user_monster_id});
    }
}
