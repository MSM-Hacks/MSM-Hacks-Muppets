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

public class Island {

    public static HashMap<Integer, Island> islands_fastdb;
    public static SFSObject islands_list = new SFSObject();

    public int cost_coins = 0;
    public int cost_diamonds = 0;
    public int level = 0;
    public int island_id = 0;

    public String description = "";
    public String fb_object_id = "";
    public String genes = "";
    public String min_server_version = "";
    public String name = "";
    public String status = "";
    public String midi = "";

    public SFSObject graphic = new SFSObject();

    public SFSArray monsters = new SFSArray();
    public SFSArray structures = new SFSArray();
    public SFSArray levels = new SFSArray();


    public static Island initWithSFSObject(SFSObject island) {
        Island isl = new Island();
        isl.cost_coins = island.getInt("cost_coins");
        isl.cost_diamonds = island.getInt("cost_diamonds");
        isl.level = island.getInt("level");
        isl.island_id = island.getInt("island_id");

        isl.description = island.getUtfString("description");
        isl.status = island.getUtfString("status");
        isl.fb_object_id = island.getUtfString("fb_object_id");
        isl.genes = island.getUtfString("genes");
        isl.midi = island.getUtfString("midi");
        isl.min_server_version = island.getUtfString("min_server_version");
        isl.name = island.getUtfString("name");

        isl.graphic = (SFSObject) island.getSFSObject("graphic");

        isl.monsters = (SFSArray) island.getSFSArray("monsters");
        isl.structures = (SFSArray) island.getSFSArray("structures");
        isl.levels = (SFSArray) island.getSFSObject("beds").getSFSArray("levels");

        return isl;
    }

    public SFSObject toSFSObject() {
        SFSObject island = new SFSObject();
        island.putInt("cost_coins", this.cost_coins);
        island.putInt("cost_diamonds", this.cost_diamonds);
        island.putInt("level", this.level);
        island.putInt("island_id", this.island_id);

        island.putUtfString("description", this.description);
        island.putUtfString("status", this.status);
        island.putUtfString("fb_object_id", this.fb_object_id);
        island.putUtfString("genes", this.genes);
        island.putUtfString("midi", this.midi);
        island.putUtfString("min_server_version", this.min_server_version);
        island.putUtfString("name", this.name);

        island.putSFSObject("graphic", this.graphic);

        island.putSFSArray("monsters", this.monsters);
        island.putSFSArray("structures", this.structures);

        SFSObject beds = new SFSObject();
        beds.putSFSArray("levels", levels);
        island.putSFSObject("beds", beds);

        return island;
    }

    public static void dropIslandsDatabase() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS islands");
    }

    public void importToDB() throws SQLException {
        String sql = SQLiteQueryBuilder.insert()
                .into("islands")
                .columns("island_id", "cost_coins", "cost_diamonds", "level", "description", "status", "fb_object_id", "genes",
                        "midi", "min_server_version", "name", "graphic", "monsters", "structures", "levels")
                .values(this.island_id, this.cost_coins, this.cost_diamonds, this.level, this.description, this.status,
                        this.fb_object_id, this.genes, this.midi, this.min_server_version, this.name, this.graphic.toJson(),
                        this.monsters.toJson(), this.structures.toJson(), this.levels.toJson())
                .build();

        stmt.executeUpdate(sql);
    }

    public static Island getIslandByID(int island_id) {
        return islands_fastdb.get(island_id);
    }

    public static void initIslandsDatabase() throws SQLException {
        islands_fastdb = new HashMap<>();
        SFSArray isl_list = new SFSArray();

        String sql = SQLiteQueryBuilder.create()
                .table("islands")
                .ifNotExists()
                .column(new Column("island_id", ColumnType.INTEGER))
                .column(new Column("cost_coins", ColumnType.INTEGER))
                .column(new Column("cost_diamonds", ColumnType.INTEGER))
                .column(new Column("level", ColumnType.INTEGER))
                .column(new Column("description", ColumnType.TEXT))
                .column(new Column("status", ColumnType.TEXT))
                .column(new Column("fb_object_id", ColumnType.TEXT))
                .column(new Column("genes", ColumnType.TEXT))
                .column(new Column("midi", ColumnType.TEXT))
                .column(new Column("min_server_version", ColumnType.TEXT))
                .column(new Column("name", ColumnType.TEXT))
                .column(new Column("graphic", ColumnType.TEXT))
                .column(new Column("monsters", ColumnType.TEXT))
                .column(new Column("structures", ColumnType.TEXT))
                .column(new Column("levels", ColumnType.TEXT))
                .toString();

        stmt.executeUpdate(sql);

        sql = SQLiteQueryBuilder.select("*")
                .from("islands")
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Island isl = new Island();
                isl.island_id = rs.getInt("island_id");
                isl.cost_coins = rs.getInt("cost_coins");
                isl.cost_diamonds = rs.getInt("cost_diamonds");
                isl.level = rs.getInt("level");
                isl.description = rs.getString("description");
                isl.status = rs.getString("status");
                isl.fb_object_id = rs.getString("fb_object_id");
                isl.genes = rs.getString("genes");
                isl.midi = rs.getString("midi");
                isl.min_server_version = rs.getString("min_server_version");
                isl.name = rs.getString("name");
                isl.graphic = (SFSObject) SFSObject.newFromJsonData(rs.getString("graphic"));
                isl.monsters = SFSArray.newFromJsonData(rs.getString("monsters"));
                isl.structures = SFSArray.newFromJsonData(rs.getString("structures"));
                isl.levels = SFSArray.newFromJsonData(rs.getString("levels"));
                islands_fastdb.put(isl.island_id, isl);

                isl_list.addSFSObject(isl.toSFSObject());
            }
        } catch (SQLException e) {}

        islands_list.putSFSArray("islands_data", isl_list);
    }
}
