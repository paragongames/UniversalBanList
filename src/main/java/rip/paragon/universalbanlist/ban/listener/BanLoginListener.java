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

package rip.paragon.universalbanlist.ban.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import rip.paragon.universalbanlist.UniversalBanList;
import rip.paragon.universalbanlist.ban.BanEntry;
import rip.paragon.universalbanlist.ban.BanListService;
import rip.paragon.universalbanlist.util.TimeFormatters;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Levi Taylor
 * @since September 14, 2020
 * Prevents banned players from logging in.
 */
public class BanLoginListener implements Listener {

    // Handle this second-lowest so other plugins can modify the result before us
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void handleLogin(AsyncPlayerPreLoginEvent event) {
        try {
            List<BanEntry> bans = BanListService.INSTANCE.getBanEntries(event.getUniqueId()).get();

            for (BanEntry ban : bans) {
                if (ban.isActive()) {
                    // The StringBuilder to build the final disconnection message with
                    StringBuilder builder = new StringBuilder();

                    // The disconnection message template to use
                    List<String> template = UniversalBanList.INSTANCE.getConfig().getStringList(String.format(
                            "kickMessages.%s",
                            ban.isPermanent() ? "permanent" : "temporary"
                    ));

                    // Build the disconnection message
                    Iterator<String> iterator = template.iterator();

                    while (iterator.hasNext()) {
                        String line = iterator.next()
                                .replace("{reason}", ban.reason)
                                .replace("{banDate}", ban.banTime.toString(TimeFormatters.DATE_FORMATTER))
                                .replace("{banLength}", ban.banLength == null ? "Forever" : ban.banLength.toString(TimeFormatters.DURATION_FORMATTER))
                                .replace("{expireDate}", ban.expireDate == null ? "Never" : ban.expireDate.toString(TimeFormatters.DATE_FORMATTER))
                                .replace("{case}", ban.caseURL)
                                ;

                        builder.append(ChatColor.translateAlternateColorCodes('&', line));

                        if (iterator.hasNext()) {
                            builder.append("\n");
                        }
                    }

                    // Set the disconnection message
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, builder.toString());

                    // Stop here - no need to continue on after finding an active ban
                    break;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            UniversalBanList.INSTANCE.getLogger().severe(String.format("Couldn't retrieve bans for %s, skipping", event.getUniqueId()));
            e.printStackTrace();
        }
    }

}
