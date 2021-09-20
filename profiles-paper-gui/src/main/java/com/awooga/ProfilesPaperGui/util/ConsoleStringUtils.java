package com.awooga.ProfilesPaperGui.util;

import com.awooga.ProfilesPaperGui.ProfilesPaperGui;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ConsoleStringUtils {

    ProfilesPaperGui main;
    public final List<Color> colors = new ArrayList<>();

    static {
        colors.add(Color.AQUA);
        colors.add(Color.BLUE);
        colors.add(Color.FUCHSIA);
        colors.add(Color.GREEN);
        colors.add(Color.LIME);
        colors.add(Color.ORANGE);
        colors.add(Color.WHITE);
        colors.add(Color.YELLOW);
    }

    public String stripColor(String msg) {
        return msg != null ? ChatColor.stripColor(msg) : null;
    }

    @Nullable
    public String color(@Nullable String msg) {
        return color(msg, '&');
    }

    @Nullable
    public String color(@Nullable String msg, char colorChar) {
        return msg == null ? null : ChatColor.translateAlternateColorCodes(colorChar, msg);
    }

}
