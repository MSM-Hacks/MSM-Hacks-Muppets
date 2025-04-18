package ru.msmhacks.muppets.auth;

import ru.msmhacks.muppets.managers.AuthDatabaseManager;

import java.util.Base64;

import static ru.msmhacks.muppets.auth.AuthUtils.stringCharSum;

public class AuthMethods {
    public static String[] createNewAnonAccount(String lang, String client_version, String mac_address, String platform,
                                                String device_id, String application_id, String IP) {
        int random = 5643756;
        String username = Base64.getEncoder().encodeToString(String.valueOf(stringCharSum(device_id+mac_address)).getBytes()).replace("=", "!");
        String password = Base64.getEncoder().encodeToString(String.valueOf(stringCharSum(device_id+mac_address+random)).getBytes()).replace("=", "!");

        if (AuthDatabaseManager.isUserExitsByUsername(username)) {
            String[] login_data = new String[4];
            login_data[0] = username;
            login_data[1] = password;
            login_data[2] = String.valueOf(AuthDatabaseManager.getBBBIDbyUsername(username));
            login_data[3] = client_version;
            return login_data;
        }

        String[] login_data = new String[4];
        login_data[0] = username;
        login_data[1] = password;
        login_data[2] = String.valueOf(AuthDatabaseManager.createNewUser(username, password, "anon", lang,
                client_version, mac_address, platform, device_id, application_id, IP));
        login_data[3] = client_version;
        return login_data;
    }

    public static String[] checkUser(String username, String password) {
        if (!AuthDatabaseManager.isUserExitsByUsername(username)) {
            return new String[]{"false", "User doesn't exits!"};
        }
        if (!AuthDatabaseManager.isUserPasswordCorrect(username, password)) {
            return new String[]{"false", "Incorrect password!"};
        }
        return new String[]{"true", "All OK!"};
    }
}
