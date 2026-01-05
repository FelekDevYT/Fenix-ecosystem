package me.felek.fenix.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PParser {
    public static String getString(String path, String name) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(path)) {
            properties.load(input);

            return properties.getProperty(name);
        } catch (IOException exc) {
            return null;
        }
    }
}
