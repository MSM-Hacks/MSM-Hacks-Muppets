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

public class Structure {

    public static HashMap<Integer, Structure> structures_fastdb;
    public static SFSObject structures_list = new SFSObject();


    public int build_time = 0;
    public int cost_coins = 0;
    public int cost_diamonds = 0;
    public int entity_id = 0;
    public int level = 0;
    public int structure_id = 0;
    public int upgrades_to = 0;
    public int movable = 0;
    public int size_x = 0;
    public int size_y = 0;
    public int sticker_offset = 0;
    public int view_in_market = 0;
    public int xp = 0;
    public int y_offset = 0;

    public String description = "";
    public String entity_type = "";
    public String structure_type = "";
    public String min_server_version = "";
    public String name = "";

    public SFSObject graphic = new SFSObject();
    public SFSObject extra = new SFSObject();

    public SFSArray requirements = new SFSArray();

    public static Structure initWithSFSObject(SFSObject structure) {
        Structure st = new Structure();
        st.structure_id = structure.getInt("structure_id");
        st.build_time = structure.getInt("build_time");
        st.cost_coins = structure.getInt("cost_coins");
        st.cost_diamonds = structure.getInt("cost_diamonds");
        st.entity_id = structure.getInt("entity_id");
        st.upgrades_to = structure.getInt("upgrades_to");
        st.level = structure.getInt("level");
        st.movable = structure.getInt("movable");
        st.size_x = structure.getInt("size_x");
        st.size_y = structure.getInt("size_y");
        st.sticker_offset = structure.getInt("sticker_offset");
        st.view_in_market = structure.getInt("view_in_market");
        st.xp = structure.getInt("xp");
        st.y_offset = structure.getInt("y_offset");

        st.description = structure.getUtfString("description");
        st.entity_type = structure.getUtfString("entity_type");
        st.structure_type = structure.getUtfString("structure_type");
        st.min_server_version = structure.getUtfString("min_server_version");
        st.name = structure.getUtfString("name");

        st.graphic = (SFSObject) structure.getSFSObject("graphic");
        st.extra = (SFSObject) structure.getSFSObject("extra");

        st.requirements = (SFSArray) structure.getSFSArray("requirements");

        return st;
    }

    public SFSObject toSFSObject() {
        SFSObject structure = new SFSObject();
        structure.putInt("structure_id", this.structure_id);
        structure.putInt("build_time", this.build_time);
        structure.putInt("cost_coins", this.cost_coins);
        structure.putInt("cost_diamonds", this.cost_diamonds);
        structure.putInt("entity_id", this.entity_id);
        structure.putInt("level", this.level);
        structure.putInt("upgrades_to", this.upgrades_to);
        structure.putInt("movable", this.movable);
        structure.putInt("size_x", this.size_x);
        structure.putInt("size_y", this.size_y);
        structure.putInt("sticker_offset", this.sticker_offset);
        structure.putInt("view_in_market", this.view_in_market);
        structure.putInt("xp", this.xp);
        structure.putInt("y_offset", this.y_offset);

        structure.putUtfString("description", this.description);
        structure.putUtfString("entity_type", this.entity_type);
        structure.putUtfString("structure_type", this.structure_type);
        structure.putUtfString("min_server_version", this.min_server_version);
        structure.putUtfString("name", this.name);

        structure.putSFSObject("graphic", this.graphic);
        structure.putSFSObject("extra", this.extra);

        structure.putSFSArray("requirements", this.requirements);

        return structure;
    }

    public static void dropStructuresDatabase() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS structures");

        String sql = SQLiteQueryBuilder.create()
                .table("structures")
                .ifNotExists()
                .column(new Column("structure_id", ColumnType.INTEGER))
                .column(new Column("build_time", ColumnType.INTEGER))
                .column(new Column("cost_coins", ColumnType.INTEGER))
                .column(new Column("cost_diamonds", ColumnType.INTEGER))
                .column(new Column("entity_id", ColumnType.INTEGER))
                .column(new Column("level", ColumnType.INTEGER))
                .column(new Column("movable", ColumnType.INTEGER))
                .column(new Column("size_x", ColumnType.INTEGER))
                .column(new Column("size_y", ColumnType.INTEGER))
                .column(new Column("sticker_offset", ColumnType.INTEGER))
                .column(new Column("view_in_market", ColumnType.INTEGER))
                .column(new Column("xp", ColumnType.INTEGER))
                .column(new Column("y_offset", ColumnType.INTEGER))
                .column(new Column("description", ColumnType.TEXT))
                .column(new Column("entity_type", ColumnType.TEXT))
                .column(new Column("structure_type", ColumnType.TEXT))
                .column(new Column("min_server_version", ColumnType.TEXT))
                .column(new Column("name", ColumnType.TEXT))
                .column(new Column("graphic", ColumnType.TEXT))
                .column(new Column("extra", ColumnType.TEXT))
                .column(new Column("requirements", ColumnType.TEXT))
                .column(new Column("upgrades_to", ColumnType.INTEGER))
                .toString();

        stmt.executeUpdate(sql);
    }

    public void importToDB() throws SQLException {
        String sql = SQLiteQueryBuilder.insert()
                .into("structures")
                .columns("structure_id", "build_time", "cost_coins", "cost_diamonds", "entity_id", "level", "movable",
                        "size_x", "size_y", "sticker_offset", "view_in_market", "xp", "y_offset", "upgrades_to", "description",
                        "entity_type", "structure_type", "min_server_version", "name", "graphic", "extra", "requirements")
                .values(this.structure_id, this.build_time, this.cost_coins, this.cost_diamonds, this.entity_id,
                        this.level, this.movable, this.size_x, this.size_y, this.sticker_offset, this.view_in_market,
                        this.xp, this.y_offset, this.upgrades_to, this.description, this.entity_type, this.structure_type,
                        this.min_server_version, this.name, this.graphic.toJson(), this.extra.toJson(),
                        this.requirements.toJson())
                .build();

        stmt.executeUpdate(sql);
    }

    public static Structure getStructureByID(int structure_id) {
        return structures_fastdb.get(structure_id);
    }

    public static void initStructuresDatabase() throws SQLException {
        structures_fastdb = new HashMap<>();
        SFSArray st_list = new SFSArray();

        String sql = SQLiteQueryBuilder.select("*")
                .from("structures")
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Structure s = new Structure();
                s.structure_id = rs.getInt("structure_id");
                s.build_time = rs.getInt("build_time");
                s.cost_coins = rs.getInt("cost_coins");
                s.cost_diamonds = rs.getInt("cost_diamonds");
                s.entity_id = rs.getInt("entity_id");
                s.level = rs.getInt("level");
                s.movable = rs.getInt("movable");
                s.size_x = rs.getInt("size_x");
                s.size_y = rs.getInt("size_y");
                s.sticker_offset = rs.getInt("sticker_offset");
                s.view_in_market = 1;//rs.getInt("view_in_market");
                s.xp = rs.getInt("xp");
                s.y_offset = rs.getInt("y_offset");
                s.description = rs.getString("description");
                s.entity_type = rs.getString("entity_type");
                s.structure_type = rs.getString("structure_type");
                s.min_server_version = rs.getString("min_server_version");
                s.name = rs.getString("name");
                s.graphic = (SFSObject) SFSObject.newFromJsonData(rs.getString("graphic"));
                s.extra = (SFSObject) SFSObject.newFromJsonData(rs.getString("extra"));
                s.requirements = (SFSArray) SFSArray.newFromJsonData(rs.getString("requirements"));
                s.upgrades_to = rs.getInt("upgrades_to");
                structures_fastdb.put(s.structure_id, s);

                st_list.addSFSObject(s.toSFSObject());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        structures_list.putSFSArray("structures_data", st_list);
    }
}
