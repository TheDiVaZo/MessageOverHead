package thedivazo.utils;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {
    private Configuration fileConfig;

    public ConfigUtils(Configuration fileConfig) {
        this.fileConfig = fileConfig;
    }

    public List<String> getAlwaysListString(String path) {
        return getAlwaysListString(path, fileConfig);
    }

    public List<String> getAlwaysListString(String path, ConfigurationSection section) {
        ArrayList<String> result = new ArrayList<>();
        if(section.isList(path)) result.addAll(section.getStringList(path));
        else result.add(section.getString(path));
        return result;
    }

    public void setConfig(FileConfiguration fileConfig) {
        this.fileConfig = fileConfig;
    }
}
