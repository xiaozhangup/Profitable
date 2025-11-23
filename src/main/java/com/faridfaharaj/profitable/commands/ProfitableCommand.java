package com.faridfaharaj.profitable.commands;

import com.faridfaharaj.profitable.util.MessagingUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * /profitable 主命令分发器，将原来的子命令路由到对应的老命令类，并提供 Tab 补全。
 */
public class ProfitableCommand implements CommandExecutor, TabCompleter {

    private final HelpCommand help = new HelpCommand();
    private final TransactCommand transact = new TransactCommand();
    private final AssetsCommand assets = new AssetsCommand();
    private final TopCommand top = new TopCommand();
    private final AccountCommand account = new AccountCommand();
    private final WalletCommand wallet = new WalletCommand();
    private final DeliveryCommand delivery = new DeliveryCommand();
    private final ClaimtagCommand claimtag = new ClaimtagCommand();
    private final OrdersCommand orders = new OrdersCommand();
    private final AdminCommand admin = new AdminCommand();

    private final List<String> subs = Arrays.asList(
            "help",
            "buy",
            "sell",
            "assets",
            "top",
            "account",
            "wallet",
            "delivery",
            "claimtag",
            "orders",
            "admin"
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessagingUtil.sendGenericCantConsole(sender);
            return true;
        }

        if (args.length == 0 || !sender.hasPermission("profitable.command.sub")) {
            return assets.onCommand(sender, command, "assets", args);
        }

        String sub = args[0].toLowerCase();
        String[] subArgs = new String[args.length - 1];
        if (args.length > 1) {
            System.arraycopy(args, 1, subArgs, 0, args.length - 1);
        }

        return switch (sub) {
            case "help" -> help.onCommand(sender, command, "help", subArgs);
            case "buy", "pbuy" -> transact.onCommand(sender, command, "pbuy", prepend("buy", subArgs));
            case "sell", "psell" -> transact.onCommand(sender, command, "psell", prepend("sell", subArgs));
            case "assets", "gui" -> assets.onCommand(sender, command, "assets", subArgs);
            case "top" -> top.onCommand(sender, command, "top", subArgs);
            case "account", "acc" -> account.onCommand(sender, command, "account", subArgs);
            case "wallet", "wal", "portfolio" -> wallet.onCommand(sender, command, "wallet", subArgs);
            case "delivery" -> delivery.onCommand(sender, command, "delivery", subArgs);
            case "claimtag", "claim" -> claimtag.onCommand(sender, command, "claimtag", subArgs);
            case "orders" -> orders.onCommand(sender, command, "orders", subArgs);
            case "admin" -> admin.onCommand(sender, command, "admin", subArgs);
            default -> {
                MessagingUtil.sendGenericInvalidSubCom(sender, sub);
                yield true;
            }
        };
    }

    private String[] prepend(String first, String[] rest) {
        String[] result = new String[rest.length + 1];
        result[0] = first;
        System.arraycopy(rest, 0, result, 1, rest.length);
        return result;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        // 只给玩家补全
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        if (!sender.hasPermission("profitable.command.sub")) {
            return new ArrayList<>();
        }

        // 第一个参数：子命令名
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> result = new ArrayList<>();
            for (String s : subs) {
                if (s.startsWith(prefix)) {
                    result.add(s);
                }
            }
            return result;
        }

        // 从第二个参数开始，把剩余参数交给对应子命令的 TabCompleter
        String sub = args[0].toLowerCase();
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, args.length - 1);

        return switch (sub) {
            case "buy", "pbuy" ->
                // TransactCommand 的 TabCompleter 期望完整 args（含 buy/sell），因此前面再拼一个标识
                    new TransactCommand.CommandTabCompleter().onTabComplete(sender, command, "pbuy", prepend("buy", subArgs));
            case "sell", "psell" ->
                    new TransactCommand.CommandTabCompleter().onTabComplete(sender, command, "psell", prepend("sell", subArgs));
            case "assets", "gui" ->
                    new AssetsCommand.CommandTabCompleter().onTabComplete(sender, command, "assets", subArgs);
            case "top" -> new TopCommand.CommandTabCompleter().onTabComplete(sender, command, "top", subArgs);
            case "account", "acc" ->
                    new AccountCommand.CommandTabCompleter().onTabComplete(sender, command, "account", subArgs);
            case "wallet", "wal", "portfolio" ->
                    new WalletCommand.CommandTabCompleter().onTabComplete(sender, command, "wallet", subArgs);
            case "delivery" ->
                    new DeliveryCommand.CommandTabCompleter().onTabComplete(sender, command, "delivery", subArgs);
            case "orders" -> new OrdersCommand.CommandTabCompleter().onTabComplete(sender, command, "orders", subArgs);
            case "admin" -> new AdminCommand.CommandTabCompleter().onTabComplete(sender, command, "admin", subArgs);
            default -> new ArrayList<>();
        };
    }
}
