package thedivazo.messageoverhead.metrics;


import org.bstats.bukkit.Metrics;
import org.bstats.charts.DrilldownPie;
import thedivazo.messageoverhead.MessageOverHead;

import java.util.HashMap;
import java.util.Map;

public class MetricsManager {

    private final MessageOverHead plugin;

    public MetricsManager(MessageOverHead plugin) {
        this.plugin = plugin;
        enableMetrics();
    }


    private void enableMetrics() {
        Metrics metrics = new Metrics(plugin, 14530);
        metrics.addCustomChart(new DrilldownPie("java_version", () -> {
            HashMap<String, Map<String, Integer>> map = new HashMap<>();
            String javaVersion = System.getProperty("java.version");
            HashMap<String, Integer> entry = new HashMap<>();
            entry.put(javaVersion, 1);
            if (javaVersion.startsWith("1.13")) {
                map.put("Java 1.13", entry);
            } else if (javaVersion.startsWith("1.14")) {
                map.put("Java 1.14", entry);
            } else if (javaVersion.startsWith("1.15")) {
                map.put("Java 1.15", entry);
            } else if (javaVersion.startsWith("1.16")) {
                map.put("Java 1.16", entry);
            } else if (javaVersion.startsWith("1.17")) {
                map.put("Java 1.17", entry);
            } else if (javaVersion.startsWith("1.18")) {
                map.put("Java 1.18", entry);
            } else if (javaVersion.startsWith("1.19")) {
                map.put("Java 1.19", entry);
            } else {
                map.put("Other", entry);
            }
            return map;
        }));
    }

}
