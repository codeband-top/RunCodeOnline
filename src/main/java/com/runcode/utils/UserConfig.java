package com.runcode.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author RhettPeng
 */
public class UserConfig {
    public static String PASSWORD;
    public static Integer WS_PORT;
    public static Integer API_PORT;
    private static final String USER_SETTING_FILE_NAME = "config/user.properties";

    static{
        // 用户配置
        InputStream in = null;
        try {
            in = new FileInputStream(USER_SETTING_FILE_NAME);
            ResourceBundle rb = new PropertyResourceBundle(in);
            PASSWORD = rb.getString("password");
            WS_PORT = Integer.parseInt(rb.getString("ws_server_port"));
            API_PORT = Integer.parseInt(rb.getString("api_server_port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
