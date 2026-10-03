package org.sbbpl.plugins.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.sbbpl.plugins.Slow_mending_re;

import java.util.List;
import java.util.Locale;

public class CommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("slowmending.command") || args.length == 0) return List.of();
        String sub = args[0].toLowerCase(Locale.ROOT);
        List<String> choices = List.of();
        if (args.length == 1) choices = List.of("help", "version", "set", "add", "info", "reload", "givecard");
        else if (args.length == 2 && List.of("set", "add", "info", "givecard").contains(sub)) {
            choices = Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).toList();
        } else if ((args.length == 3 && sub.equals("info"))
                || (args.length == 4 && List.of("set", "add").contains(sub))) choices = List.of("main", "off");
        else if (args.length == 5 && sub.equals("givecard")) choices = List.of("set", "add");
        else if (args.length == 3 && sub.equals("givecard")) choices = List.of("1", "16", "64");
        else if ((args.length == 3 && List.of("set", "add").contains(sub))
                || (args.length == 4 && sub.equals("givecard"))) {
            choices = List.of("0", "1", "-1", "-2", "-3", String.valueOf(Slow_mending_re.getMax_Mend_Limit_Number()));
        }
        String prefix = args[args.length - 1].toLowerCase(Locale.ROOT);
        return choices.stream().filter(s -> s.toLowerCase(Locale.ROOT).startsWith(prefix)).distinct().toList();
    }
}
