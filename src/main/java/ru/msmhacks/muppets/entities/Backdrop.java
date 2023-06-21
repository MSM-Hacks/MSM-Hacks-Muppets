package ru.msmhacks.muppets.entities;

import com.alexfu.sqlitequerybuilder.api.Column;
import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import ru.msmhacks.muppets.managers.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static ru.msmhacks.muppets.managers.StaticDatabaseManager.stmt;

public class Backdrop {

    public static HashMap<Integer, Backdrop> backdrops_fastdb;
    public static SFSObject backdrops_list = new SFSObject();

    public int backdrop_id = 0;
    public int cost_coins = 0;
    public int cost_diamonds = 0;
    public int initial = 0;
    public int island_id = 0;
    public int level = 0;
    public int view_in_market = 0;

    public SFSObject graphic = new SFSObject();

    public String name = "";
    public String description = "";


    public static Backdrop initWithSFSObject(SFSObject backdrop) {
        Backdrop new_backdrop = new Backdrop();
        new_backdrop.backdrop_id = backdrop.getInt("backdrop_id");
        new_backdrop.cost_coins = backdrop.getInt("cost_coins");
        new_backdrop.cost_diamonds = backdrop.getInt("cost_diamonds");
        new_backdrop.level = backdrop.getInt("level");
        new_backdrop.initial = backdrop.getInt("initial");
        new_backdrop.island_id = backdrop.getInt("island_id");
        new_backdrop.view_in_market = backdrop.getInt("view_in_market");
        new_backdrop.graphic = (SFSObject) backdrop.getSFSObject("graphic");
        new_backdrop.name = backdrop.getUtfString("name");
        new_backdrop.description = backdrop.getUtfString("description");

        return new_backdrop;
    }

    public SFSObject toSFSObject() {
        SFSObject backdrop = new SFSObject();
        backdrop.putInt("backdrop_id", backdrop_id);

        backdrop.putInt("level", this.level);
        backdrop.putInt("cost_coins", cost_coins);
        backdrop.putInt("cost_diamonds", cost_diamonds);
        backdrop.putInt("initial", initial);
        backdrop.putInt("island_id", island_id);
        backdrop.putInt("view_in_market", view_in_market);

        backdrop.putSFSObject("graphic", graphic);

        backdrop.putUtfString("name", name);
        backdrop.putUtfString("description", description);

        return backdrop;
    }

    public static void dropBackdropsDatabase() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS backdrops");
    }

    public void importToDB() throws SQLException {
        String sql = SQLiteQueryBuilder.insert()
                .into("backdrops")
                .columns("backdrop_id", "cost_coins", "cost_diamonds", "level", "initial",
                        "island_id", "view_in_market", "graphic", "name", "description")
                .values(this.backdrop_id, this.cost_coins, this.cost_diamonds, this.level, this.initial,
                        this.island_id, this.view_in_market, this.graphic.toJson(), this.name, this.description)
                .build();

        stmt.executeUpdate(sql);
    }

    public static Backdrop getBackdropByID(int backdrop_id) {
        return backdrops_fastdb.get(backdrop_id);
    }

    public static void initBackdropsDatabase() throws SQLException {
        backdrops_fastdb = new HashMap<>();
        SFSArray backdrop_list = new SFSArray();

        String sql = SQLiteQueryBuilder.create()
                .table("backdrops")
                .ifNotExists()
                .column(new Column("backdrop_id", ColumnType.INTEGER))
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

        sql = SQLiteQueryBuilder.select("*")
                .from("backdrops")
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Backdrop new_light = new Backdrop();
                new_light.backdrop_id = rs.getInt("backdrop_id");
                new_light.cost_coins = rs.getInt("cost_coins");
                new_light.cost_diamonds = rs.getInt("cost_diamonds");
                new_light.level = rs.getInt("level");
                new_light.initial = rs.getInt("initial");
                new_light.island_id = rs.getInt("island_id");
                new_light.view_in_market = rs.getInt("island_id");
                new_light.graphic = (SFSObject) SFSObject.newFromJsonData(rs.getString("graphic"));
                new_light.name = rs.getString("name");
                new_light.name = rs.getString("description");
                backdrops_fastdb.put(new_light.backdrop_id, new_light);

                backdrop_list.addSFSObject(new_light.toSFSObject());
            }
        } catch (SQLException e) {
            Utils.log(e.toString());
        }

        backdrops_list.putSFSArray("backdrop_data", backdrop_list);
    }
}
