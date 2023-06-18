package ru.msmhacks.muppets.entities;

import com.alexfu.sqlitequerybuilder.api.Column;
import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static ru.msmhacks.muppets.managers.StaticDatabaseManager.stmt;

public class Light {

    public static HashMap<Integer, Light> lights_fastdb;
    public static SFSObject lights_list = new SFSObject();

    public int lighting_id = 0;
    public int cost_coins = 0;
    public int cost_diamonds = 0;
    public int initial = 0;
    public int island_id = 0;
    public int level = 0;
    public int view_in_market = 0;

    public SFSObject graphic = new SFSObject();

    public String name = "";
    public String description = "";


    public static Light initWithSFSObject(SFSObject light) {
        Light new_light = new Light();
        new_light.lighting_id = light.getInt("lighting_id");
        new_light.cost_coins = light.getInt("cost_coins");
        new_light.cost_diamonds = light.getInt("cost_diamonds");
        new_light.level = light.getInt("level");
        new_light.initial = light.getInt("initial");
        new_light.island_id = light.getInt("island_id");
        new_light.view_in_market = light.getInt("view_in_market");
        new_light.graphic = (SFSObject) light.getSFSObject("graphic");
        new_light.name = light.getUtfString("name");
        new_light.description = light.getUtfString("description");

        return new_light;
    }

    public SFSObject toSFSObject() {
        SFSObject light = new SFSObject();
        light.putInt("lighting_id", lighting_id);

        light.putInt("level", this.level);
        light.putInt("cost_coins", cost_coins);
        light.putInt("cost_diamonds", cost_diamonds);
        light.putInt("initial", initial);
        light.putInt("island_id", island_id);
        light.putInt("view_in_market", view_in_market);

        light.putSFSObject("graphic", graphic);

        light.putUtfString("name", name);
        light.putUtfString("description", description);

        return light;
    }

    public static void dropLightsDatabase() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS lights");

        String sql = SQLiteQueryBuilder.create()
                .table("lights")
                .ifNotExists()
                .column(new Column("lighting_id", ColumnType.INTEGER))
                .column(new Column("cost_coins", ColumnType.INTEGER))
                .column(new Column("cost_diamonds", ColumnType.INTEGER))
                .column(new Column("level", ColumnType.INTEGER))
                .column(new Column("initial", ColumnType.INTEGER))
                .column(new Column("island_id", ColumnType.INTEGER))
                .column(new Column("view_in_market", ColumnType.INTEGER))
                .column(new Column("graphic", ColumnType.TEXT))
                .column(new Column("name", ColumnType.TEXT))
                .column(new Column("description", ColumnType.TEXT))
                .toString();

        stmt.executeUpdate(sql);
    }

    public void importToDB() throws SQLException {
        String sql = SQLiteQueryBuilder.insert()
                .into("lights")
                .columns("lighting_id", "cost_coins", "cost_diamonds", "level", "initial",
                        "island_id", "view_in_market", "graphic", "name", "description")
                .values(this.lighting_id, this.cost_coins, this.cost_diamonds, this.level, this.initial,
                        this.island_id, this.view_in_market, this.graphic.toJson(), this.name, this.description)
                .build();

        stmt.executeUpdate(sql);
    }

    public static Light getLightByID(int lighting_id) {
        return lights_fastdb.get(lighting_id);
    }

    public static void initLightsDatabase() {
        lights_fastdb = new HashMap<>();
        SFSArray light_list = new SFSArray();

        String sql = SQLiteQueryBuilder.select("*")
                .from("lights")
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Light new_light = new Light();
                new_light.lighting_id = rs.getInt("lighting_id");
                new_light.cost_coins = rs.getInt("cost_coins");
                new_light.cost_diamonds = rs.getInt("cost_diamonds");
                new_light.level = rs.getInt("level");
                new_light.initial = rs.getInt("initial");
                new_light.island_id = rs.getInt("island_id");
                new_light.view_in_market = rs.getInt("island_id");
                new_light.graphic = (SFSObject) SFSObject.newFromJsonData(rs.getString("graphic"));
                new_light.name = rs.getString("name");
                new_light.name = rs.getString("description");
                lights_fastdb.put(new_light.lighting_id, new_light);

                light_list.addSFSObject(new_light.toSFSObject());
            }
        } catch (SQLException e) {}

        lights_list.putSFSArray("lighting_data", light_list);
    }
}
