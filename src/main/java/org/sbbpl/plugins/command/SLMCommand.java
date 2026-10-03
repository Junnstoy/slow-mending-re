package org.sbbpl.plugins.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.sbbpl.plugins.MendCount;
import org.sbbpl.plugins.MendingItem;
import org.sbbpl.plugins.Slow_mending_re;
import org.sbbpl.plugins.command.commands.com_give;
import org.sbbpl.plugins.loadPL;

import java.io.File;
import java.util.Locale;
import java.util.logging.Level;

public class SLMCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("slowmending.command")) {
            sender.sendMessage("§c你没有权限！");
            return true;
        }
        String sub = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);
        Slow_mending_re plugin = Slow_mending_re.getSLM();
        try {
            switch (sub) {
                case "reload" -> {
                    require(args.length == 1, "/slmend reload");
                    try {
                        plugin.ensureResources();
                        loadPL.loadPlugins();
                        plugin.reloadConfig();
                        sender.sendMessage("§b配置已重载。");
                    } catch (Exception e) {
                        plugin.getLogger().log(Level.WARNING, "重载失败，保留之前的有效配置。", e);
                        sender.sendMessage("§c重载失败，保留之前的有效配置：" + e.getMessage());
                    }
                }
                case "version" -> sender.sendMessage("§bSlow Mending Re " + plugin.getPluginMeta().getVersion()
                        + " | Paper 26.3 | 原作者 super_boy_520");
                case "set", "add", "info" -> edit(sender, sub, args);
                case "givecard" -> {
                    require(args.length == 5, "/slmend givecard <player> <quantity> <frequency> <set|add>");
                    Player target = player(args[1]);
                    int quantity = Integer.parseInt(args[2]);
                    int frequency = Integer.parseInt(args[3]);
                    require(args[4].equalsIgnoreCase("set") || args[4].equalsIgnoreCase("add"), "模式必须为 set 或 add。");
                    com_give.give(target, frequency, args[4].equalsIgnoreCase("set"), quantity);
                    sender.sendMessage("§b已给予 " + target.getName() + " " + quantity + " 张拓展卡。");
                }
                default -> {
                    var config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "command.yml"));
                    for (String line : config.getStringList("Command.text.help")) sender.sendMessage(line);
                    // Include this for existing command.yml files, which are intentionally not overwritten.
                    sender.sendMessage("§b/slmend info <player> [main|off] - 查看剩余次数（-1 无限，-2 无限且不减速，-3 禁止）。");
                }
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§c数量和次数必须为 32 位整数。");
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§c" + e.getMessage());
        }
        return true;
    }

    private void edit(CommandSender sender, String mode, String[] args) {
        boolean info = mode.equals("info");
        int base = info ? 2 : 3;
        require(args.length == base || args.length == base + 1,
                "/slmend " + mode + " <player> " + (info ? "" : "<num> ") + "[main|off]");
        Player target = player(args[1]);
        String hand = args.length > base ? args[base].toLowerCase(Locale.ROOT) : "main";
        require(hand.equals("main") || hand.equals("off"), "手持位置必须为 main 或 off。");
        ItemStack item = hand.equals("main") ? target.getInventory().getItemInMainHand() : target.getInventory().getItemInOffHand();
        var meta = item.getItemMeta();
        require(meta instanceof Damageable, "目标手中必须是具有耐久度的装备。");
        String prefix = Slow_mending_re.getMend_Frequency_Lore_Name();
        var oldNames = Slow_mending_re.getOld_Mend_Frequency_Lore_Name();
        int value;
        if (mode.equals("set")) {
            value = MendCount.validate(Integer.parseInt(args[2]));
        } else {
            try {
                value = MendingItem.getRemainderMendFrequency(meta, prefix, oldNames);
            } catch (NoSuchFieldException e) {
                value = Slow_mending_re.getMax_Mend_Limit_Number();
            }
            if (!info) value = MendCount.add(value, Integer.parseInt(args[2]));
        }
        if (!info) {
            MendingItem.setRemainderMendFrequency(meta, prefix, value, oldNames);
            item.setItemMeta(meta);
            if (hand.equals("main")) target.getInventory().setItemInMainHand(item);
            else target.getInventory().setItemInOffHand(item);
        }
        sender.sendMessage("§b" + target.getName() + " " + hand + " 剩余修补次数：" + value);
    }

    private static Player player(String name) {
        Player player = Bukkit.getPlayerExact(name);
        require(player != null, "未找到在线玩家：" + name);
        return player;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
}
