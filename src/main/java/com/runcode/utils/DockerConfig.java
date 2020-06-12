package com.runcode.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author RhettPeng
 */
public class DockerConfig {
    public static String DOCKER_HOST;
    public static String DOCKER_CERT_PATH;
    public static String REGISTRY_USER_NAME;
    public static String REGISTRY_PASSWORD;
    public static String REGISTRY_EMAIL;

    public static final String DOCKER_CONTAINER_WORK_DIR = "/usr/src/myapp";

    private static final String DOCKER_SETTING_FILE_NAME = "config/docker.properties";

    static{
        // 用户配置
        InputStream in = null;
        try {
            in = new FileInputStream(DOCKER_SETTING_FILE_NAME);
            ResourceBundle rb = new PropertyResourceBundle(in);
            DOCKER_HOST = rb.getString("dockerHost");
            DOCKER_CERT_PATH = rb.getString("dockerCertPath");
            REGISTRY_USER_NAME = rb.getString("registryUsername");
            REGISTRY_PASSWORD = rb.getString("registryPassword");
            REGISTRY_EMAIL = rb.getString("registryEmail");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
