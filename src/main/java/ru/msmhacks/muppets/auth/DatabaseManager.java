package ru.msmhacks.muppets.auth;

import java.sql.*;

import com.alexfu.sqlitequerybuilder.api.Column;
import com.alexfu.sqlitequerybuilder.api.ColumnConstraint;
import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;


public class DatabaseManager {

    public static Connection c = null;
    public static Statement stmt = null;

    public static void init() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:auth_data.db");
            stmt = c.createStatement();

            System.out.println("Opened database successfully");

            //stmt.executeUpdate("DROP TABLE users");


            String sql = SQLiteQueryBuilder.create()
                    .table("users")
                    .ifNotExists()
                    .column(new Column("bbb_id", ColumnType.INTEGER, ColumnConstraint.PRIMARY_KEY_AUTO_INCREMENT))
                    .column(new Column("username", ColumnType.TEXT))
                    .column(new Column("password", ColumnType.TEXT))
                    .column(new Column("login_type", ColumnType.TEXT))
                    .column(new Column("lang", ColumnType.TEXT))
                    .column(new Column("client_version", ColumnType.TEXT))
                    .column(new Column("mac_address", ColumnType.TEXT))
                    .column(new Column("platform", ColumnType.TEXT))
                    .column(new Column("device_id", ColumnType.TEXT))
                    .column(new Column("application_id", ColumnType.TEXT))
                    .toString();

            stmt.executeUpdate(sql);


        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

    }


    public static long createNewUser(String username, String password, String login_type,
                                    String lang, String client_version, String mac_address, String platform,
                                    String device_id, String application_id) {

        String sql = SQLiteQueryBuilder.insert()
                .into("users")
                .columns("username","password","login_type","lang","client_version","mac_address",
                        "platform","device_id","application_id")
                .values(username, password, login_type, lang, client_version, mac_address,
                        platform, device_id, application_id)
                .build();

        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            ResultSet rs = stmt.executeQuery("SELECT MAX(bbb_id) FROM users LIMIT 1;");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public static boolean isUserExitsByUsername(String username) {
        String sql = SQLiteQueryBuilder.select("*")
                .from("users")
                .where("username = '" + username + "'")
                .limit(1)
                .build();
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean isUserPasswordCorrect(String username, String password) {
        String sql = SQLiteQueryBuilder.select("*")
                .from("users")
                .where("username = '" + username + "'")
                .and("password = '" + password + "'")
                .build();
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static int getBBBIDbyUsername(String username) {
        String sql = SQLiteQueryBuilder.select("*")
                .from("users")
                .where("username = '" + username + "'")
                .limit(1)
                .build();
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getInt("bbb_id");
        } catch (SQLException e) {
            return -1;
        }
    }
}
