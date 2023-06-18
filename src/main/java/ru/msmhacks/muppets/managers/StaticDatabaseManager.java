package ru.msmhacks.muppets.managers;

import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import ru.msmhacks.muppets.MuppetsExtension;
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
            st = (SFSArray) Utils.getSFSFromJson(new File(MuppetsExtension.DBROOT + "monster_data.json")).getSFSArray("monsters_data");
            for (int y=0;y<st.size();y++) {
                Monster x = Monster.initWithSFSObject((SFSObject) st.getSFSObject(y));
                x.importToDB();
            }

            Structure.dropStructuresDatabase();
            st = (SFSArray) Utils.getSFSFromJson(new File(MuppetsExtension.DBROOT + "structure_data.json")).getSFSArray("structures_data");
            for (int y=0;y<st.size();y++) {
                Structure x = Structure.initWithSFSObject((SFSObject) st.getSFSObject(y));
                x.importToDB();
            }

            Island.dropIslandsDatabase();
            st = (SFSArray) Utils.getSFSFromJson(new File(MuppetsExtension.DBROOT + "island_data.json")).getSFSArray("islands_data");
            for (int y=0;y<st.size();y++) {
                Island x = Island.initWithSFSObject((SFSObject) st.getSFSObject(y));
                x.importToDB();
            }

            Level.dropLevelsDatabase();
            st = (SFSArray) Utils.getSFSFromJson(new File(MuppetsExtension.DBROOT + "level_data.json")).getSFSArray("level_data");
            for (int y=0;y<st.size();y++) {
                Level x = Level.initWithSFSObject((SFSObject) st.getSFSObject(y));
                x.importToDB();
            }

            BreedingCombination.dropBreedingDatabase();
            st = (SFSArray) Utils.getSFSFromJson(new File(MuppetsExtension.DBROOT + "breeding_data.json")).getSFSArray("breedingcombo_data");
            for (int y=0;y<st.size();y++) {
                BreedingCombination x = BreedingCombination.initWithSFSObject((SFSObject) st.getSFSObject(y));
                x.importToDB();
            }

            Light.dropLightsDatabase();
            st = (SFSArray) Utils.getSFSFromJson(new File(MuppetsExtension.DBROOT + "lighting_data.json")).getSFSArray("lighting_data");
            for (int y=0;y<st.size();y++) {
                Light x = Light.initWithSFSObject((SFSObject) st.getSFSObject(y));
                x.importToDB();
            }

            Backdrop.dropBackdropsDatabase();
            st = (SFSArray) Utils.getSFSFromJson(new File(MuppetsExtension.DBROOT + "backdrop_data.json")).getSFSArray("backdrop_data");
            for (int y=0;y<st.size();y++) {
                Backdrop x = Backdrop.initWithSFSObject((SFSObject) st.getSFSObject(y));
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

        Light.initLightsDatabase();
        log("[DB Loader] Loaded " + Light.lights_fastdb.size() + " lights");

        Backdrop.initBackdropsDatabase();
        log("[DB Loader] Loaded " + Backdrop.backdrops_fastdb.size() + " backdrops");
    }


    public static void main(String[] args) {
        try {
            initAllDatabases(false);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
