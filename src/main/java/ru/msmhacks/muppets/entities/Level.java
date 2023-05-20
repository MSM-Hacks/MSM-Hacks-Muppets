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

public class Level {

    public static HashMap<Integer, Level> levels_fastdb;
    public static SFSObject levels_list = new SFSObject();

    public int coins_conversion = 0;
    public int diamonds_conversion = 0;
    public int diamond_reward = 0;
    public int level = 0;
    public int xp = 0;
    public int max_bakeries;

    public SFSArray daily_rewards = new SFSArray();


    public static Level initWithSFSObject(SFSObject level) {
        Level lvl = new Level();
        lvl.coins_conversion = level.getSFSObject("currency_conversion").getInt("coins");
        lvl.diamonds_conversion = level.getSFSObject("currency_conversion").getInt("diamonds");
        lvl.diamond_reward = level.getInt("diamond_reward");
        lvl.level = level.getInt("level");
        lvl.xp = level.getInt("xp");
        lvl.max_bakeries = level.getInt("max_bakeries");
        lvl.daily_rewards = (SFSArray) level.getSFSArray("daily_rewards");

        return lvl;
    }

    public SFSObject toSFSObject() {
        SFSObject level = new SFSObject();

        SFSObject currency_conversion = new SFSObject();
        currency_conversion.putInt("coins", this.coins_conversion);
        currency_conversion.putInt("diamonds", this.diamonds_conversion);
        level.putSFSObject("currency_conversion", currency_conversion);

        level.putInt("level", this.level);
        level.putInt("diamond_reward", this.diamond_reward);
        level.putInt("xp", this.xp);
        level.putInt("max_bakeries", this.max_bakeries);

        level.putSFSArray("daily_rewards", daily_rewards);

        return level;
    }

    public static void dropLevelsDatabase() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS levels");

        String sql = SQLiteQueryBuilder.create()
                .table("levels")
                .ifNotExists()
                .column(new Column("coins_conversion", ColumnType.INTEGER))
                .column(new Column("diamonds_conversion", ColumnType.INTEGER))
                .column(new Column("level", ColumnType.INTEGER))
                .column(new Column("diamond_reward", ColumnType.INTEGER))
                .column(new Column("xp", ColumnType.INTEGER))
                .column(new Column("max_bakeries", ColumnType.INTEGER))
                .column(new Column("daily_rewards", ColumnType.TEXT))
                .toString();

        stmt.executeUpdate(sql);
    }

    public void importToDB() throws SQLException {
        String sql = SQLiteQueryBuilder.insert()
                .into("levels")
                .columns("coins_conversion", "diamonds_conversion", "level", "diamond_reward", "xp",
                        "max_bakeries", "daily_rewards")
                .values(this.coins_conversion, this.diamonds_conversion, this.level, this.diamond_reward, this.xp,
                        this.max_bakeries, this.daily_rewards.toJson())
                .build();

        stmt.executeUpdate(sql);
    }

    public static Level getLevelByID(int level) {
        return levels_fastdb.get(level);
    }

    public static void initLevelsDatabase() {
        levels_fastdb = new HashMap<>();
        SFSArray lvl_list = new SFSArray();

        String sql = SQLiteQueryBuilder.select("*")
                .from("levels")
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Level lvl = new Level();
                lvl.coins_conversion = rs.getInt("coins_conversion");
                lvl.diamonds_conversion = rs.getInt("diamonds_conversion");
                lvl.diamond_reward = rs.getInt("diamond_reward");
                lvl.level = rs.getInt("level");
                lvl.xp = rs.getInt("xp");
                lvl.max_bakeries = rs.getInt("max_bakeries");
                lvl.daily_rewards = SFSArray.newFromJsonData(rs.getString("daily_rewards"));
                levels_fastdb.put(lvl.level, lvl);

                lvl_list.addSFSObject(lvl.toSFSObject());
            }
        } catch (SQLException e) {}

        levels_list.putSFSArray("level_data", lvl_list);
    }
}
