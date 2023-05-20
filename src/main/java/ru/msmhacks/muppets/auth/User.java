package ru.msmhacks.muppets.auth;

public class User {
    public String username;
    public String password;
    public String login_type;
    public String bbb_id;
    public String lang;
    public String client_version;
    public String mac_address;
    public String platform;
    public String device_id;
    public String application_id;

    public User(String username, String password, String login_type, String bbb_id,
                String lang, String client_version, String mac_address, String platform,
                String device_id, String application_id) {

        this.username = username;
        this.password = password;
        this.login_type = login_type;
        this.bbb_id = bbb_id;
        this.lang = lang;
        this.client_version = client_version;
        this.mac_address = mac_address;
        this.platform = platform;
        this.device_id = device_id;
        this.application_id = application_id;
    }



}
