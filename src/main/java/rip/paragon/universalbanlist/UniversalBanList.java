/*
 * Copyright (C) Paragon Games 2020. All rights reserved.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package rip.paragon.universalbanlist;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import rip.paragon.universalbanlist.ban.BanEntry;
import rip.paragon.universalbanlist.ban.BanListService;
import rip.paragon.universalbanlist.ban.listener.BanLoginListener;
import rip.paragon.universalbanlist.command.VersionCommand;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Levi Taylor
 * @since September 11, 2020
 * Represents the plugin's main class.
 */
public class UniversalBanList extends JavaPlugin {

    public static final String DEFAULT_BAN_LIST_URL = "https://docs.google.com/spreadsheet/ccc?key=0AjACyg1Jc3_GdEhqWU5PTEVHZDVLYWphd2JfaEZXd2c&output=csv";

    public static UniversalBanList INSTANCE;

    public UniversalBanList() {
        // Make sure JavaPlugin's constructor gets called
        super();

        if (INSTANCE != null) {
            throw new IllegalStateException("Can't re-initialize singleton");
        }

        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        super.saveDefaultConfig();

        // Call this now in case the URL is malformed
        this.getBanListURL();

        // Log some stats
        BanListService.INSTANCE.retrieve().thenAccept(entries -> {
            super.getLogger().info(String.format("There are %d total bans on the Universal Ban List.", entries.size()));

            super.getLogger().info(String.format(
                    "%d of those bans are active.",
                    entries.stream().filter(BanEntry::isActive).count()
            ));
        }).exceptionally(throwable -> {
            super.getLogger().severe("Couldn't fetch ban entries");
            throwable.printStackTrace();

            return null;
        });

        // Register the login listener
        super.getServer().getPluginManager().registerEvents(new BanLoginListener(), this);

        // Register commands
        super.getCommand("ublversion").setExecutor(new VersionCommand());
    }

    /**
     * Gets the ban list URL from the plugin's configuration file.
     * @return The ban list URL
     */
    public @NotNull URL getBanListURL() {
        String url = super.getConfig().getString("url");

        if (url == null) {
            url = DEFAULT_BAN_LIST_URL;
        }

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            super.getLogger().severe("The URL in the plugin's configuration is malformed. Please check it and restart the server or reload the plugin.");
            Bukkit.getPluginManager().disablePlugin(this);

            throw new RuntimeException("Malformed URL", e);
        }
    }

}
