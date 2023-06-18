package ru.msmhacks.muppets.managers;

import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import ru.msmhacks.muppets.MuppetsExtension;
import ru.msmhacks.muppets.entities.*;
import ru.msmhacks.muppets.entities.Player.Player;
import ru.msmhacks.muppets.entities.Player.PlayerIsland;
import ru.msmhacks.muppets.entities.Player.PlayerStructure;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static ru.msmhacks.muppets.managers.Utils.log;

public class PlayerDatabaseManager {
    public static Connection c = null;
    public static Statement stmt = null;

    public static void initAllDatabases(boolean drop) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:player_dbs.db");
        stmt = c.createStatement();

        if (drop) {
            Player.dropPlayersDatabase();
            PlayerIsland.dropPlayerIslandsDatabase();
            PlayerStructure.dropPlayerStructuresDatabase();
        }

        log("[DB Loader] Loading players databases...");

        Player.initPlayersDatabase();
        log("[DB Loader] Loaded " + Player.players.size() + " players");

        PlayerIsland.initPlayerIslandsDatabase();
        log("[DB Loader] Loaded " + PlayerIsland.islands.size() + " player islands");

        PlayerStructure.initPlayerStructuresDatabase();
        log("[DB Loader] Loaded " + PlayerStructure.structures.size() + " player structures");
    }


    public static void main(String[] args) {
        try {
            initAllDatabases(false);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeVoid(String sql, Object[] args) {
        String[] formatted_args = new String[args.length];
        int a = 0;
        for (Object arg: args) {
            String sarg = arg.toString();
            sarg = sarg.replace("'", "''")
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f");
            formatted_args[a] = "'" + sarg + "'";
            a++;
        }
        try {
            stmt.executeUpdate(String.format(sql, (Object[]) formatted_args));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
