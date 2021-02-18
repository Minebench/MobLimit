package de.minebench.moblimit;

/*
 * MobLimit
 * Copyright (c) 2020 Max Lee aka Phoenix616 (max@themoep.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MobLimitCommand implements CommandExecutor {
    private MobLimit plugin;

    public MobLimitCommand(MobLimit plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if ("reload".equalsIgnoreCase(args[0]) && sender.hasPermission("moblimit.command.reload")) {
                plugin.loadConfig();
                sender.sendMessage("Reloaded!");
                return true;
            } else if ("set".equalsIgnoreCase(args[0]) && sender.hasPermission("moblimit.command.set")) {
                if (args.length < 2) {
                    usage(sender);
                    return true;
                }

                try {
                    int value = Integer.parseInt(args[1]);

                    // TODO: Language
                    //set limits
                    if (args[0].equals("chunk")) {
                        plugin.setChunkLimit(value);
                        sender.sendMessage("Set chunk limit to " + value);
                    } else if (args[0].equals("global")) {
                        plugin.setGlobalLimit(value);
                        sender.sendMessage("Set global limit to " + value);
                    } else {
                        usage(sender);
                        return true;
                    }
                } catch (NumberFormatException e) {
                    if (args[0].equals("purge")) {
                        plugin.setPurging("true".equals(args[1]));
                        sender.sendMessage("Set purging to " + plugin.isPurging());
                    } else {
                        usage(sender);
                    }
                }

                return true;
            }
        }
        sender.sendMessage(plugin.getCurrentStateMessage().toArray(new String[0]));

        return true;
    }

    private void usage(CommandSender sender) {
        // TODO: Language
        sender.sendMessage(new String[] {
                "Usage: /moblimit reload",
                "Usage: /moblimit set chunk|global <value>",
                "Usage: /moblimit set purge false|true",
                "Note: chunklimit counts all entities in a chunk!",
                "Note: purge will remove hostile/ambient mobs on chunkload"
        });
    }
}
