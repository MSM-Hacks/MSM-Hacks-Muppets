package ru.msmhacks.muppets.entities;

import com.alexfu.sqlitequerybuilder.api.Column;
import com.alexfu.sqlitequerybuilder.api.ColumnConstraint;
import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static ru.msmhacks.muppets.managers.StaticDatabaseManager.stmt;

public class Monster {

    public static HashMap<Integer, Monster> monsters_fastdb;
    public static SFSObject monsters_list = new SFSObject();

    public int beds = 0;
    public int build_time = 0;
    public int cost_coins = 0;
    public int cost_diamonds = 0;
    public int entity_id = 0;
    public int hide_friends = 0;
    public int level = 0;
    public int monster_id = 0;
    public int movable = 0;
    public int size_x = 0;
    public int size_y = 0;
    public int sticker_offset = 0;
    public int tier = 0;
    public int view_in_market = 0;
    public int xp = 0;
    public int y_offset = 0;

    public String description = "";
    public String entity_type = "";
    public String fb_object_id = "";
    public String genes = "";
    public String hatch_sound = "";
    public String min_server_version = "";
    public String name = "";

    public SFSObject graphic = new SFSObject();

    public SFSArray happiness = new SFSArray();
    public SFSArray levels = new SFSArray();
    public SFSArray requirements = new SFSArray();

    public static Monster initWithSFSObject(SFSObject monster) {
        Monster ms = new Monster();
        ms.beds = monster.getInt("beds");
        ms.build_time = monster.getInt("build_time");
        ms.cost_coins = monster.getInt("cost_coins");
        ms.cost_diamonds = monster.getInt("cost_diamonds");
        ms.entity_id = monster.getInt("entity_id");
        try { ms.hide_friends = monster.getInt("hide_friends");} catch (Exception ignored) {}
        ms.level = monster.getInt("level");
        ms.monster_id = monster.getInt("monster_id");
        ms.movable = monster.getInt("movable");
        ms.size_x = monster.getInt("size_x");
        ms.size_y = monster.getInt("size_y");
        ms.sticker_offset = monster.getInt("sticker_offset");
        ms.tier = monster.getInt("tier");
        ms.view_in_market = monster.getInt("view_in_market");
        ms.xp = monster.getInt("xp");
        ms.y_offset = monster.getInt("y_offset");

        ms.description = monster.getUtfString("description");
        ms.entity_type = monster.getUtfString("entity_type");
        ms.fb_object_id = monster.getUtfString("fb_object_id");
        ms.genes = monster.getUtfString("genes");
        ms.hatch_sound = monster.getUtfString("hatch_sound");
        ms.min_server_version = monster.getUtfString("min_server_version");
        ms.name = monster.getUtfString("name");

        ms.graphic = (SFSObject) monster.getSFSObject("graphic");

        ms.happiness = (SFSArray) monster.getSFSArray("happiness");
        ms.levels = (SFSArray) monster.getSFSArray("levels");
        ms.requirements = (SFSArray) monster.getSFSArray("requirements");

        return ms;
    }

    public SFSObject toSFSObject() {
        SFSObject monster = new SFSObject();
        monster.putInt("beds", this.beds);
        monster.putInt("build_time", this.build_time);
        monster.putInt("cost_coins", this.cost_coins);
        monster.putInt("cost_diamonds", this.cost_diamonds);
        monster.putInt("entity_id", this.entity_id);
        monster.putInt("hide_friends", this.hide_friends);
        monster.putInt("level", this.level);
        monster.putInt("monster_id", this.monster_id);
        monster.putInt("movable", this.movable);
        monster.putInt("size_x", this.size_x);
        monster.putInt("size_y", this.size_y);
        monster.putInt("sticker_offset", this.sticker_offset);
        monster.putInt("tier", this.tier);
        monster.putInt("view_in_market", this.view_in_market);
        monster.putInt("xp", this.xp);
        monster.putInt("y_offset", this.y_offset);

        monster.putUtfString("description", this.description);
        monster.putUtfString("entity_type", this.entity_type);
        monster.putUtfString("fb_object_id", this.fb_object_id);
        monster.putUtfString("genes", this.genes);
        monster.putUtfString("hatch_sound", this.hatch_sound);
        monster.putUtfString("min_server_version", this.min_server_version);
        monster.putUtfString("name", this.name);

        monster.putSFSObject("graphic", this.graphic);

        monster.putSFSArray("happiness", this.happiness);
        monster.putSFSArray("levels", this.levels);
        monster.putSFSArray("requirements", this.requirements);

        return monster;
    }

    public static void dropMonstersDatabase() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS monsters");
    }

    public void importToDB() throws SQLException {
        String sql = SQLiteQueryBuilder.insert()
                .into("monsters")
                .columns("monster_id", "beds", "build_time", "cost_coins", "cost_diamonds", "entity_id", "hide_friends",
                        "level", "monster_id", "movable", "size_x", "size_y", "sticker_offset", "tier", "view_in_market",
                        "xp", "y_offset", "description", "entity_type", "fb_object_id", "genes", "hatch_sound",
                        "min_server_version", "name", "graphic", "happiness", "levels", "requirements")
                .values(this.monster_id, this.beds, this.build_time, this.cost_coins, this.cost_diamonds, this.entity_id,
                        this.hide_friends, this.level, this.monster_id, this.movable, this.size_x, this.size_y,
                        this.sticker_offset, this.tier, this.view_in_market, this.xp, this.y_offset, this.description,
                        this.entity_type, this.fb_object_id, this.genes, this.hatch_sound, this.min_server_version,
                        this.name, this.graphic.toJson(), this.happiness.toJson(), this.levels.toJson(),
                        this.requirements.toJson())
                .build();

        stmt.executeUpdate(sql);
    }

    public static Monster getMonsterByID(int monster_id) {
        return monsters_fastdb.get(monster_id);
    }

    public static void initMonstersDatabase() throws SQLException {
        monsters_fastdb = new HashMap<>();
        SFSArray ms_list = new SFSArray();

        String sql = SQLiteQueryBuilder.create()
                .table("monsters")
                .ifNotExists()
                .column(new Column("monster_id", ColumnType.INTEGER))
                .column(new Column("beds", ColumnType.INTEGER))
                .column(new Column("build_time", ColumnType.INTEGER))
                .column(new Column("cost_coins", ColumnType.INTEGER))
                .column(new Column("cost_diamonds", ColumnType.INTEGER))
                .column(new Column("entity_id", ColumnType.INTEGER))
                .column(new Column("hide_friends", ColumnType.INTEGER))
                .column(new Column("level", ColumnType.INTEGER))
                .column(new Column("movable", ColumnType.INTEGER))
                .column(new Column("size_x", ColumnType.INTEGER))
                .column(new Column("size_y", ColumnType.INTEGER))
                .column(new Column("sticker_offset", ColumnType.INTEGER))
                .column(new Column("tier", ColumnType.INTEGER))
                .column(new Column("view_in_market", ColumnType.INTEGER))
                .column(new Column("xp", ColumnType.INTEGER))
                .column(new Column("y_offset", ColumnType.INTEGER))
                .column(new Column("description", ColumnType.TEXT))
                .column(new Column("entity_type", ColumnType.TEXT))
                .column(new Column("fb_object_id", ColumnType.TEXT))
                .column(new Column("genes", ColumnType.TEXT))
                .column(new Column("hatch_sound", ColumnType.TEXT))
                .column(new Column("min_server_version", ColumnType.TEXT))
                .column(new Column("name", ColumnType.TEXT))
                .column(new Column("graphic", ColumnType.TEXT))
                .column(new Column("happiness", ColumnType.TEXT))
                .column(new Column("levels", ColumnType.TEXT))
                .column(new Column("requirements", ColumnType.TEXT))
                .toString();

        stmt.executeUpdate(sql);

        sql = SQLiteQueryBuilder.select("*")
                .from("monsters")
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Monster ms = new Monster();
                ms.monster_id = rs.getInt("monster_id");
                ms.beds = rs.getInt("beds");
                ms.build_time = rs.getInt("build_time");
                ms.cost_coins = rs.getInt("cost_coins");
                ms.cost_diamonds = rs.getInt("cost_diamonds");
                ms.entity_id = rs.getInt("entity_id");
                ms.hide_friends = rs.getInt("hide_friends");
                ms.level = rs.getInt("level");
                ms.monster_id = rs.getInt("monster_id");
                ms.movable = rs.getInt("movable");
                ms.size_x = rs.getInt("size_x");
                ms.size_y = rs.getInt("size_y");
                ms.sticker_offset = rs.getInt("sticker_offset");
                ms.tier = rs.getInt("tier");
                ms.view_in_market = rs.getInt("view_in_market");
                ms.xp = rs.getInt("xp");
                ms.y_offset = rs.getInt("y_offset");
                ms.description = rs.getString("description");
                ms.entity_type = rs.getString("entity_type");
                ms.fb_object_id = rs.getString("fb_object_id");
                ms.genes = rs.getString("genes");
                ms.hatch_sound = rs.getString("hatch_sound");
                ms.min_server_version = rs.getString("min_server_version");
                ms.name = rs.getString("name");
                ms.graphic = (SFSObject) SFSObject.newFromJsonData(rs.getString("graphic"));
                ms.happiness = SFSArray.newFromJsonData(rs.getString("happiness"));
                ms.levels = SFSArray.newFromJsonData(rs.getString("levels"));
                ms.requirements = SFSArray.newFromJsonData(rs.getString("requirements"));
                monsters_fastdb.put(ms.monster_id, ms);

                ms_list.addSFSObject(ms.toSFSObject());
            }
        } catch (SQLException e) {}

        monsters_list.putSFSArray("monsters_data", ms_list);
    }
}
