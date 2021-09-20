package com.awooga.ProfilesPaperGui.chestgui;

import com.awooga.ProfilesPaperGui.ProfilesPaperGui;
import com.awooga.ProfilesPaperGui.util.HiddenStringUtil;
import lombok.Builder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Builder
public class ChestGui<S> {

    private static ProfilesPaperGui plugin = ProfilesPaperGui.getInstance();


    StatefulItemStackSupplier<S> supplier;
    String configKey;

    public Inventory getInventory(Player player) {
        List<String> slotConfig = plugin.getConfig().getStringList(this.configKey+".slots");

        List<StatefulItemStack<S>> storedState = new ArrayList<>();
        AtomicReference<Integer> i = new AtomicReference<>(0);
        slotConfig.forEach(row -> {
            Arrays.asList(row.split(",")).forEach(slotChar -> {
                String legendName = plugin.getConfig().getString(this.configKey+".legend."+slotChar);
                StatefulItemStack<S> stack = supplier.get(i.get(), legendName, this::getText)
                    .toBuilder()
                    .legendName(legendName)
                .build();
                storedState.add(stack);
                i.set(i.get() + 1);
            });
        });

        String windowTitle = this.getWindowTitle(player);
        Inventory inv = Bukkit.createInventory(player, storedState.size(), windowTitle);
        i.set(0);
        storedState.forEach(stack -> {
            ItemStack itemStack = stack.getItemStack();
            S state = stack.getState();
            HiddenStringUtil.addLore(itemStack, state);

            inv.setItem(i.get(), itemStack);
            i.set(i.get()+1);
        });

        return inv;
    }

    public String getWindowTitle(Player player) {
        return this.getText(player,"windowTitle");
    }

    public String getLegendNameBySlot(Integer i) {
        List<String> slotConfig = plugin.getConfig().getStringList(this.configKey+".slots");
        Integer j = 0;
        for(String row: slotConfig) {
            List<String> cells = Arrays.asList(row.split(","));
            if(j + cells.size() > i) {
                String slotChar = cells.get(i - j);
                return plugin.getConfig().getString(this.configKey+".legend."+slotChar);
            }
            j += cells.size();
        }
        return null;
    }

    public String getText(OfflinePlayer player, String key) {
        return getTextRaw(player, this.configKey + ".text."+ key);
    }

    String getTextRaw(OfflinePlayer player, String key) {
        String unexpandedText = plugin.getConfig().getString(key);
        try {
            return PlaceholderAPI.setPlaceholders((OfflinePlayer) player, unexpandedText == null ? "" : unexpandedText);
        } catch(Exception e) {
            Bukkit.getLogger().warning("Caught exception when generating placeholder for /profiles: "+e);
            e.printStackTrace();
            return "Exception filling placeholders: "+unexpandedText;
        }
    }
}
