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

public class BreedingCombination {

    public static HashMap<Integer, BreedingCombination> breeding_fastdb;
    public static SFSObject breeding_list = new SFSObject();

    public int breeding_combination_id = 0;
    public double modifier = 0;
    public int monster_1 = 0;
    public int monster_2;
    public int probability;
    public int result;


    public static BreedingCombination initWithSFSObject(SFSObject comb) {
        BreedingCombination breedingCombination = new BreedingCombination();
        breedingCombination.breeding_combination_id = comb.getInt("breeding_combination_id");
        breedingCombination.modifier = comb.getDouble("modifier");
        breedingCombination.monster_1 = comb.getInt("monster_1");
        breedingCombination.monster_2 = comb.getInt("monster_2");
        breedingCombination.probability = comb.getInt("probability");
        breedingCombination.result = comb.getInt("result");

        return breedingCombination;
    }

    public SFSObject toSFSObject() {
        SFSObject breedingCombination = new SFSObject();
        breedingCombination.putInt("breeding_combination_id", this.breeding_combination_id);
        breedingCombination.putInt("monster_1", this.monster_1);
        breedingCombination.putInt("monster_2", this.monster_2);
        breedingCombination.putInt("result", this.result);
        breedingCombination.putDouble("modifier", this.modifier);
        breedingCombination.putInt("probability", this.probability);
        return breedingCombination;
    }

    public static void dropBreedingDatabase() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS breeding");
    }

    public void importToDB() throws SQLException {
        String sql = SQLiteQueryBuilder.insert()
                .into("breeding")
                .columns("breeding_combination_id", "monster_1", "monster_2", "result", "modifier", "probability")
                .values(this.breeding_combination_id, this.monster_1, this.monster_2, this.result, this.modifier,
                        this.probability)
                .build();

        stmt.executeUpdate(sql);
    }

    public static BreedingCombination getCombByID(int comb) {
        return breeding_fastdb.get(comb);
    }

    public static void initBreedingDatabase() throws SQLException {
        breeding_fastdb = new HashMap<>();
        SFSArray comb_list = new SFSArray();

        String sql = SQLiteQueryBuilder.create()
                .table("breeding")
                .ifNotExists()
                .column(new Column("breeding_combination_id", ColumnType.INTEGER))
                .column(new Column("monster_1", ColumnType.INTEGER))
                .column(new Column("monster_2", ColumnType.INTEGER))
                .column(new Column("result", ColumnType.INTEGER))
                .column(new Column("modifier", ColumnType.REAL))
                .column(new Column("probability", ColumnType.INTEGER))
                .toString();

        stmt.executeUpdate(sql);

        sql = SQLiteQueryBuilder.select("*")
                .from("breeding")
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                BreedingCombination comb = new BreedingCombination();
                comb.breeding_combination_id = rs.getInt("breeding_combination_id");
                comb.modifier = rs.getDouble("modifier");
                comb.monster_1 = rs.getInt("monster_1");
                comb.monster_2 = rs.getInt("monster_2");
                comb.probability = rs.getInt("probability");
                comb.result = rs.getInt("result");
                breeding_fastdb.put(comb.breeding_combination_id, comb);
                comb_list.addSFSObject(comb.toSFSObject());
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        breeding_list.putSFSArray("breedingcombo_data", comb_list);
    }
}
