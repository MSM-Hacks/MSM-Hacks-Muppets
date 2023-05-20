package ru.msmhacks.muppets.managers;

import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import ru.msmhacks.muppets.entities.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static ru.msmhacks.muppets.managers.Utils.log;

public class StaticDatabaseManager {

    public static Connection c = null;
    public static Statement stmt = null;

    public static void initAllDatabases(boolean drop) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:static_dbs.db");
        stmt = c.createStatement();

        if (drop) {
            SFSArray st;
            Monster.dropMonstersDatabase();
            st = (SFSArray) Utils.getSFSFromJson(new File("C:\\Users\\Zewsic\\SmartFoxServer_2X\\res\\json_db\\monster_data.json")).getSFSArray("monsters_data");
            for (int y=0;y<st.size();y++) {
                Monster x = Monster.initWithSFSObject((SFSObject) st.getSFSObject(y));
                x.importToDB();
            }

            Structure.dropStructuresDatabase();
            st = (SFSArray) Utils.getSFSFromJson(new File("C:\\Users\\Zewsic\\SmartFoxServer_2X\\res\\json_db\\structure_data.json")).getSFSArray("structures_data");
            for (int y=0;y<st.size();y++) {
                Structure x = Structure.initWithSFSObject((SFSObject) st.getSFSObject(y));
                x.importToDB();
            }

            Island.dropIslandsDatabase();
            st = (SFSArray) Utils.getSFSFromJson(new File("C:\\Users\\Zewsic\\SmartFoxServer_2X\\res\\json_db\\island_data.json")).getSFSArray("islands_data");
            for (int y=0;y<st.size();y++) {
                Island x = Island.initWithSFSObject((SFSObject) st.getSFSObject(y));
                x.importToDB();
            }

            Level.dropLevelsDatabase();
            st = (SFSArray) Utils.getSFSFromJson(new File("C:\\Users\\Zewsic\\SmartFoxServer_2X\\res\\json_db\\level_data.json")).getSFSArray("level_data");
            for (int y=0;y<st.size();y++) {
                Level x = Level.initWithSFSObject((SFSObject) st.getSFSObject(y));
                x.importToDB();
            }

            BreedingCombination.dropBreedingDatabase();
            st = (SFSArray) Utils.getSFSFromJson(new File("C:\\Users\\Zewsic\\SmartFoxServer_2X\\res\\json_db\\breeding_data.json")).getSFSArray("breedingcombo_data");
            for (int y=0;y<st.size();y++) {
                BreedingCombination x = BreedingCombination.initWithSFSObject((SFSObject) st.getSFSObject(y));
                x.importToDB();
            }
        }

        log("[DB Loader] Loading static databases...");

        Monster.initMonstersDatabase();
        log("[DB Loader] Loaded " + Monster.monsters_fastdb.size() + " monsters");

        Structure.initStructuresDatabase();
        log("[DB Loader] Loaded " + Structure.structures_fastdb.size() + " structures");

        Island.initIslandsDatabase();
        log("[DB Loader] Loaded " + Island.islands_fastdb.size() + " islands");

        Level.initLevelsDatabase();
        log("[DB Loader] Loaded " + Level.levels_fastdb.size() + " levels");

        BreedingCombination.initBreedingDatabase();
        log("[DB Loader] Loaded " + BreedingCombination.breeding_fastdb.size() + " breeding combinations");
    }


    public static void main(String[] args) {
        try {
            initAllDatabases(true);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
