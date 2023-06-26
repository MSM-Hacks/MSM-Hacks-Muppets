package ru.msmhacks.muppets.entities.Player;

import com.alexfu.sqlitequerybuilder.api.Column;
import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.smartfoxserver.v2.entities.data.SFSObject;
import ru.msmhacks.muppets.entities.Structure;
import ru.msmhacks.muppets.managers.PlayerDatabaseManager;
import ru.msmhacks.muppets.managers.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static ru.msmhacks.muppets.managers.PlayerDatabaseManager.getMaximumData;
import static ru.msmhacks.muppets.managers.PlayerDatabaseManager.stmt;
import static ru.msmhacks.muppets.managers.Utils.log;

public class PlayerStructure {
    public static HashMap<Long, PlayerStructure> structures = new HashMap<>();

    public long user_structure_id = 0;
    public long user_island_id = 0;

    public int pos_x = 0;
    public int pos_y = 0;
    public int flip = 0;
    public int muted = 0;
    public int is_complete = 1;
    public int is_upgrading = 0;
    public int structure = 0;
    public float scale = 1.0F;

    public Long building_completed = 0L;
    public Long date_created = 0L;
    public Long last_collection = 0L;

    public Integer obj_data = null;
    public Long obj_end = null;


    public static PlayerStructure createNewStructure(long user_island_id, int structure_id, int x, int y, int flip, float scale) {
        Utils.log("Creating new structure");
        PlayerStructure structure = new PlayerStructure();
        structure.user_island_id = user_island_id;
        structure.user_structure_id = getMaximumData("player_structures", "user_structure_id")==null?0:(long)getMaximumData("player_structures", "user_structure_id")+1;
        structure.structure = structure_id;
        structure.pos_x = x;
        structure.pos_y = y;
        structure.flip = flip;
        structure.scale = scale;

        structures.put(structure.user_structure_id, structure);
        try {structure.importToDB();} catch (SQLException ignored) {}
        return structure;
    }

    public static PlayerStructure getStructure(long user_structure_id) {
        if (structures.containsKey(user_structure_id)) {
            return structures.get(user_structure_id);
        }
        return null;
    }

    public SFSObject toSFSObject() {
        SFSObject structure = new SFSObject();

        structure.putLong("user_structure_id", user_structure_id);
        structure.putLong("user_island_id", user_island_id);

        structure.putInt("pos_x", pos_x);
        structure.putInt("pos_y", pos_y);
        structure.putInt("flip", flip);
        structure.putInt("muted", muted);
        structure.putInt("is_complete", is_complete);
        structure.putInt("is_upgrading", is_upgrading);
        structure.putInt("structure", this.structure);

        structure.putFloat("scale", scale);

        if (building_completed!=null) structure.putLong("building_completed", building_completed);
        if (date_created!=null) structure.putLong("date_created", date_created);
        if (last_collection!=null) structure.putLong("last_collection", last_collection);

        if (obj_data!=null) structure.putInt("obj_data", obj_data);
        if (obj_end!=null) structure.putLong("obj_end", obj_end);

        return structure;
    }

    public static void dropPlayerStructuresDatabase() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS player_structures");
    }

    public void importToDB() throws SQLException {
        String sql = SQLiteQueryBuilder.insert()
                .into("player_structures")
                .columns("user_structure_id", "user_island_id", "pos_x", "pos_y", "flip", "muted",
                        "is_complete", "is_upgrading", "structure", "scale", "building_completed",
                        "date_created", "last_collection", "obj_data", "obj_end")
                .values(this.user_structure_id, this.user_island_id, this.pos_x, this.pos_y, this.flip, this.muted,
                        this.is_complete, this.is_upgrading, this.structure, this.scale, this.building_completed,
                        this.date_created, this.last_collection, obj_data, obj_end)
                .build();

        stmt.executeUpdate(sql);
    }

    public static void initPlayerStructuresDatabase() throws SQLException {
        structures = new HashMap<>();

        String sql = SQLiteQueryBuilder.create()
                .table("player_structures")
                .ifNotExists()
                .column(new Column("user_structure_id", ColumnType.INTEGER))
                .column(new Column("user_island_id", ColumnType.INTEGER))
                .column(new Column("pos_x", ColumnType.INTEGER))
                .column(new Column("pos_y", ColumnType.INTEGER))
                .column(new Column("flip", ColumnType.INTEGER))
                .column(new Column("muted", ColumnType.INTEGER))
                .column(new Column("is_complete", ColumnType.INTEGER))
                .column(new Column("is_upgrading", ColumnType.INTEGER))
                .column(new Column("structure", ColumnType.INTEGER))
                .column(new Column("scale", ColumnType.REAL))
                .column(new Column("building_completed", ColumnType.INTEGER))
                .column(new Column("date_created", ColumnType.INTEGER))
                .column(new Column("last_collection", ColumnType.INTEGER))
                .column(new Column("obj_data", ColumnType.INTEGER))
                .column(new Column("obj_end", ColumnType.INTEGER))
                .toString();
        stmt.executeUpdate(sql);

        sql = SQLiteQueryBuilder.select("*")
                .from("player_structures")
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                PlayerStructure pi = new PlayerStructure();
                pi.user_structure_id = rs.getLong("user_structure_id");
                pi.user_island_id = rs.getLong("user_island_id");
                pi.pos_x = rs.getInt("pos_x");
                pi.pos_y = rs.getInt("pos_y");
                pi.flip = rs.getInt("flip");
                pi.muted = rs.getInt("muted");
                pi.is_complete = rs.getInt("is_complete");
                pi.is_upgrading = rs.getInt("is_upgrading");
                pi.structure = rs.getInt("structure");
                pi.scale = rs.getFloat("scale");
                pi.building_completed = rs.getLong("building_completed")==0?null:rs.getLong("building_completed");
                pi.date_created = rs.getLong("date_created")==0?null:rs.getLong("date_created");
                pi.last_collection = rs.getLong("last_collection")==0?null:rs.getLong("last_collection");
                pi.obj_data = rs.getInt("obj_data")==0?null:rs.getInt("obj_data");
                pi.obj_end = rs.getInt("obj_end")==0?null:rs.getLong("obj_end");

                structures.put(pi.user_structure_id, pi);
            }
        } catch (SQLException e) {}

        if (!structures.containsKey(-1L)) {
            log("Creating service structure");
            PlayerStructure structure1 = new PlayerStructure();
            structure1.user_structure_id = -1;
            structure1.user_island_id = -1;

            structures.put(-1L, structure1);
            try {structure1.importToDB();} catch (SQLException ignored) {}
        }
    }

    public static PlayerStructure[] getStructuresOnIsland(long user_island_id) {
        String sql = SQLiteQueryBuilder.select("*")
                .from("player_structures")
                .where("user_island_id = " + user_island_id)
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            ArrayList<PlayerStructure> structures_on_island = new ArrayList<PlayerStructure>();
            while (rs.next()) {
                structures_on_island.add(getStructure(rs.getLong("user_structure_id")));
            }
            PlayerStructure[] structuresOnIsland = new PlayerStructure[structures_on_island.size()];

            int index = 0;
            for (PlayerStructure structureOnIsland : structures_on_island) {
                structuresOnIsland[index] = structureOnIsland;
                index++;
            }
            return structuresOnIsland;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isIslandHasStructure(long user_island_id, long user_structure_id) {
        String sql = SQLiteQueryBuilder.select("*")
                .from("player_structures")
                .where("user_island_id = " + user_island_id + " AND user_structure_id = " + user_structure_id)
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            rs.getLong("user_structure_id");
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isIslandHasStructureType(long user_island_id, int structure_id) {
        String sql = SQLiteQueryBuilder.select("*")
                .from("player_structures")
                .where("user_island_id = " + user_island_id + " AND structure = " + structure_id)
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Long getStructureOnIslandByStructureType(long user_island_id, int structure_id) {
        String sql = SQLiteQueryBuilder.select("*")
                .from("player_structures")
                .where("user_island_id = " + user_island_id + " AND structure = " + structure_id)
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            if (rs.next())
                return rs.getLong("user_structure_id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void moveStructure(long user_structure_id, int x, int y, float scale) {
        getStructure(user_structure_id).pos_x = x;
        getStructure(user_structure_id).pos_y = y;
        getStructure(user_structure_id).scale = scale;

        PlayerDatabaseManager.executeVoid("UPDATE player_structures SET pos_x = %s, pos_y = %s, scale = %s WHERE user_structure_id = %s;",
                new Object[]{x, y, scale, user_structure_id});
    }
    
    public static void flipStructure(long user_structure_id) {
        int flip = getStructure(user_structure_id).flip;
        int newFlip = flip==0?1:0;
        getStructure(user_structure_id).flip = newFlip;

        PlayerDatabaseManager.executeVoid("UPDATE player_structures SET flip = %s WHERE user_structure_id = %s;",
                new Object[]{newFlip, user_structure_id});
    }

    public static void muteStructure(long user_structure_id) {
        int muted = getStructure(user_structure_id).muted;
        int newMuted = muted==0?1:0;
        getStructure(user_structure_id).flip = newMuted;

        PlayerDatabaseManager.executeVoid("UPDATE player_structures SET muted = %s WHERE user_structure_id = %s;",
                new Object[]{newMuted, user_structure_id});
    }

    public static void startClearingStructure(long user_structure_id) {
        PlayerStructure playerStructure = getStructure(user_structure_id);
        Structure structure1 = Structure.getStructureByID(playerStructure.structure);
        playerStructure.date_created = System.currentTimeMillis();
        playerStructure.building_completed = System.currentTimeMillis() + structure1.build_time*1000;
        playerStructure.last_collection = 0L;

        PlayerDatabaseManager.executeVoid("UPDATE player_structures SET date_created = %s, building_completed = %s, last_collection = %s WHERE user_structure_id = %s;",
                new Object[]{playerStructure.date_created, playerStructure.building_completed, playerStructure.last_collection, user_structure_id});
    }

    public static void speedupStructure(long user_structure_id) {
        PlayerStructure playerStructure = getStructure(user_structure_id);
        playerStructure.building_completed = System.currentTimeMillis();

        PlayerDatabaseManager.executeVoid("UPDATE player_structures SET building_completed = %s WHERE user_structure_id = %s;",
                new Object[]{playerStructure.building_completed, user_structure_id});

    }

    public static void removeStructure(long user_structure_id) {
        structures.remove(user_structure_id);

        PlayerDatabaseManager.executeVoid("DELETE FROM player_structures WHERE user_structure_id = %s;",
                new Object[]{user_structure_id});
    }

    public static boolean startUpgradingStructure(long user_structure_id) {
        PlayerStructure playerStructure = getStructure(user_structure_id);
        Structure oldStructure = Structure.getStructureByID(playerStructure.structure);

        if (oldStructure.upgrades_to == 0 || playerStructure.is_upgrading == 1) {return false;}
        Structure newStructure = Structure.getStructureByID(oldStructure.upgrades_to);

        playerStructure.is_upgrading = 1;
        playerStructure.is_complete = 0;
        playerStructure.date_created = System.currentTimeMillis();
        playerStructure.building_completed = System.currentTimeMillis() + newStructure.build_time*1000;

        PlayerDatabaseManager.executeVoid("UPDATE player_structures SET is_upgrading = 1, is_complete = 0, date_created = %s," +
                        "building_completed = %s WHERE user_structure_id = %s;",
                new Object[]{playerStructure.date_created, playerStructure.building_completed, user_structure_id});

        return true;
    }

    public static boolean finishUpgradingStructure(long user_structure_id) {
        PlayerStructure playerStructure = getStructure(user_structure_id);
        Structure oldStructure = Structure.getStructureByID(playerStructure.structure);

        if (playerStructure.is_upgrading == 0) {return false;}
        Structure newStructure = Structure.getStructureByID(oldStructure.upgrades_to);

        playerStructure.structure = newStructure.structure_id;
        playerStructure.is_upgrading = 0;
        playerStructure.is_complete = 1;
        playerStructure.date_created = System.currentTimeMillis();
        playerStructure.building_completed = 0L;

        PlayerDatabaseManager.executeVoid("UPDATE player_structures SET is_upgrading = 0, is_complete = 1, date_created = %s," +
                        "building_completed = 0, structure = %s WHERE user_structure_id = %s;",
                new Object[]{playerStructure.date_created, playerStructure.structure, user_structure_id});

        return true;
    }

    public static void setupEgg(long user_structure_id, Integer obj_data, Long obj_end) {
        PlayerStructure structure = getStructure(user_structure_id);
        structure.obj_data = obj_data;
        structure.obj_end = obj_end;

        PlayerDatabaseManager.executeVoid("UPDATE player_structures SET obj_data = %s, obj_end = %s WHERE user_structure_id = %s;",
                new Object[]{obj_data==null?0:obj_data, obj_end==null?0:obj_end, user_structure_id});
    }
}
