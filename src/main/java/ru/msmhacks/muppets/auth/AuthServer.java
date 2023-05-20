package ru.msmhacks.muppets.auth;


import flak.App;
import flak.Flak;
import flak.Query;
import flak.annotations.Route;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.msmhacks.muppets.MuppetsExtension;

import java.util.Arrays;
import java.util.Objects;

public class AuthServer {

    public static String game_server_ip;

    @Route("/auth.php")
    public String auth(Query q) {
        MuppetsExtension.extension.trace("GET /auth.php: " + q.parameters());

        String username = q.get("u");
        String password = q.get("p");
        String login_type = q.get("t");
        String bbb_id = q.get("bbb_id");

        String lang = q.get("lang");
        String client_version = q.get("client_version");
        String mac_address = q.get("mac");
        String platform = q.get("platform");
        String device_id = q.get("devid");
        String application_id = q.get("aid");

        JSONObject response = new JSONObject();

        if ("anon".equals(login_type)) {
            if (Objects.equals(username, "")) {
                String[] login_data = AuthMethods.createNewAnonAccount(lang, client_version, mac_address,
                        platform, device_id, application_id);

                username = login_data[0];
                password = login_data[1];
                bbb_id = login_data[2];


                response.put("ok", true);
                response.put("login_type", login_type);
                response.put("anon_name", username);
                response.put("anon_pass", password);
                response.put("anon_bbb_id", bbb_id);
            }
        } else {
            response.put("ok", false);
            response.put("error", 2);
            response.put("message", "Unsupported login type!");
        }
        response.put("serverIp", game_server_ip);
        return response.toString();
    }

    @Route("/content/:ver/files.json")
    public String get_updates(Query q, String ver) {
        MuppetsExtension.extension.trace("GET /content/" + ver + "/files.json: " + q.parameters());

        JSONArray response = new JSONArray();
        //TODO: make updates

        return response.toString();
    }

    @Route("/check_user.php")
    public String check_user(Query q) {
        MuppetsExtension.extension.trace("GET /check_user.php: " + q.parameters());

        String username = q.get("u");
        String password = q.get("p");

        JSONObject response = new JSONObject();

        String[] result = AuthMethods.checkUser(username, password);
        if (Objects.equals(result[0], "false")) {
            response.put("ok", false);
            response.put("message", result[1]);
        } else {
            response.put("ok", true);
        }
        return response.toString();
    }



    public static void runAuthServer(int port, String game_server_id) throws Exception {
        AuthServer.game_server_ip = game_server_id;

        DatabaseManager.init();

        App app = Flak.createHttpApp(port);
        app.scan(new AuthServer());
        app.start();
    }

    public static void main(String[] args) throws Exception {
        runAuthServer(80, "192.168.212.73");
    }
}
