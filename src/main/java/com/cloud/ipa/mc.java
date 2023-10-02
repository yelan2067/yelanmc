package com.cloud.ipa;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class mc extends JavaPlugin implements Listener {

    private Logger logger;

    @Override
    public void onEnable() {
        logger = getLogger();
        getLogger().info("--------------");
        getLogger().info("IPPlugin插件加载中...");
        getLogger().info("IPPlugin插件加载完成！");
        getLogger().info("IPPlugin插件 版本：V1.0 作者：YeLan");
        getLogger().info("--------------");
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    public void onDisable() {
        getLogger().info("--------------");
        getLogger().info("插件已卸载！");
        getLogger().info("感谢您的使用!");
        getLogger().info("--------------");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 获取玩家IP地址
        if (player.getAddress() == null) {
            return; // 如果玩家没有分配IP地址，则退出方法
        }
        String ipAddress = player.getAddress().getAddress().getHostAddress();

        // 使用GET方法向API发送请求
        String json = sendGetRequest("https://api.vore.top/api/IPdata?ip=" + ipAddress);

        if (json != null) {
            try {
                // 解析JSON数据
                Gson gson = new Gson();
                ApiResponse response = gson.fromJson(json, ApiResponse.class);

                // 检查API返回的code字段
                if (response.code == 200) {
                    // 获取城市名
                    String cityName = response.adcode.n;

                    // 在Tab栏对应的玩家名前显示城市名
                    player.setPlayerListName(ChatColor.GREEN + "[" + cityName + "] " +ChatColor.GOLD + player.getName());
                } else {
                    // 如果code字段不是200，则在Tab栏对应的玩家名上显示[未知]
                    player.setPlayerListName(ChatColor.DARK_RED + "[未知] " + player.getName());
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "[IPPlugin]解析JSON数据时发生错误: " + e.getMessage());
            }
        } else {
            logger.warning("[IPPlugin]获取API响应失败");
        }
    }

    private String sendGetRequest(String url) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "[IPPlugin]发送GET请求时发生错误: " + e.getMessage());
            return null;
        }
    }

    private static class ApiResponse {
        int code;
        Adcode adcode;
    }

    private static class Adcode {
        String n;
    }
}
