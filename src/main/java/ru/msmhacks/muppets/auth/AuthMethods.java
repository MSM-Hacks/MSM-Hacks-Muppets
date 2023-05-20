package ru.msmhacks.muppets.auth;

import java.util.Base64;

import static ru.msmhacks.muppets.auth.AuthUtils.stringCharSum;

public class AuthMethods {
    public static String[] createNewAnonAccount(String lang, String client_version, String mac_address, String platform,
                                                String device_id, String application_id) {
        int random = (int) ((Math.random() * ((9999999 - 1000000) + 1)) + 1000000);
        String username = Base64.getEncoder().encodeToString(String.valueOf(stringCharSum(device_id+mac_address)).getBytes()).replace("=", "!");
        String password = Base64.getEncoder().encodeToString(String.valueOf(stringCharSum(device_id+mac_address+random)).getBytes()).replace("=", "!");

        if (DatabaseManager.isUserExitsByUsername(username)) {
            String[] login_data = new String[3];
            login_data[0] = username;
            login_data[1] = password;
            login_data[2] = String.valueOf(DatabaseManager.getBBBIDbyUsername(username));
            return login_data;
        }

        String[] login_data = new String[3];
        login_data[0] = username;
        login_data[1] = password;
        login_data[2] = String.valueOf(DatabaseManager.createNewUser(username, password, "anon", lang,
                client_version, mac_address, platform, device_id, application_id));
        return login_data;
    }

    public static String[] checkUser(String username, String password) {
        if (!DatabaseManager.isUserExitsByUsername(username)) {
            return new String[]{"false", "User doesn't exits!"};
        }
        if (!DatabaseManager.isUserPasswordCorrect(username, password)) {
            return new String[]{"false", "Incorrect password!"};
        }
        return new String[]{"true", "All OK!"};
    }
}
