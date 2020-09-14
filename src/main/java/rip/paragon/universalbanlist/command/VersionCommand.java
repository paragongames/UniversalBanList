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

package rip.paragon.universalbanlist.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import rip.paragon.universalbanlist.UniversalBanList;

/**
 * @author Levi Taylor
 * @since September 12, 2020
 * Command used to obtain the plugin's version and other information.
 */
public class VersionCommand implements CommandExecutor {

    private static final String[] RESPONSE = {
            String.format(
                    "Running %s version %s created by %s",
                    UniversalBanList.INSTANCE.getName(),
                    UniversalBanList.INSTANCE.getDescription().getVersion(),
                    UniversalBanList.INSTANCE.getDescription().getAuthors().get(0)
            ),

            UniversalBanList.INSTANCE.getDescription().getWebsite()
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        sender.sendMessage(RESPONSE);
        return true;
    }

}
