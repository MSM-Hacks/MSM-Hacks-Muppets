package ru.msmhacks.muppets.managers;

import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import ru.msmhacks.muppets.MuppetsExtension;
import ru.msmhacks.muppets.entities.*;
import ru.msmhacks.muppets.entities.Player.Player;
import ru.msmhacks.muppets.entities.Player.PlayerIsland;
import ru.msmhacks.muppets.entities.Player.PlayerMonster;
import ru.msmhacks.muppets.entities.Player.PlayerStructure;

import java.io.File;
import java.sql.*;

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
            PlayerMonster.dropPlayerMonstersDatabase();
        }

        log("[DB Loader] Loading players databases...");

        Player.initPlayersDatabase();
        log("[DB Loader] Loaded " + Player.players.size() + " players");

        PlayerIsland.initPlayerIslandsDatabase();
        log("[DB Loader] Loaded " + PlayerIsland.islands.size() + " player islands");

        PlayerStructure.initPlayerStructuresDatabase();
        log("[DB Loader] Loaded " + PlayerStructure.structures.size() + " player structures");

        PlayerMonster.initPlayerMonstersDatabase();
        log("[DB Loader] Loaded " +  PlayerMonster.monsters.size() + " player monsters");
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

    public static Object getMaximumData(String table, String column) {
        String sql = SQLiteQueryBuilder.select("MAX("+column+")")
                .from(table)
                .build();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
