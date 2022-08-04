package com.sucy.tunnel;

import com.sucy.skill.SkillAPI;
import com.sucy.tunnel.book.BookLoader;
import com.sucy.tunnel.book.internals.BookButton;
import com.sucy.tunnel.vault.CosmeticGui;
import com.sucy.tunnel.vault.VaultGui;
import com.sucy.tunnel.vault.VaultManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String s, @NotNull String[] arg) {
        if (arg.length>0 && arg[0].equals("reload"))
            return reload();
        if (arg.length==1 && arg[0].equals("vault")) {
            new VaultGui((OfflinePlayer) sender, 0).open((Player) sender);
            return true;
        }
        if (arg.length>1 && arg[0].equals("vault")) {
            new VaultManager((OfflinePlayer) sender).setSize(Integer.parseInt(arg[1]));
            return true;
        }
        if (arg.length>1 && arg[0].equals("cosmetic") && sender instanceof Player) {
            new CosmeticGui(((Player) sender).getPlayer());
            return true;
        }
        if (arg.length>1 && arg[0].equals("open") && sender instanceof Player)
            return open(((Player) sender).getPlayer(), arg[1]);
        if (arg.length>1 && arg[0].equals("ocb") && sender instanceof Player)
            return BookButton.execute((Player) sender,arg[1]);
        if (arg.length>1 && arg[0].equals("test") && sender instanceof Player)
            return test((Player) sender,arg);
        return skillList((Player) sender);
    }

    private boolean test(Player sender, String[] arg) {
        return false;
    }

    private boolean reload() {
        bookLoader.reload(SkillAPI.inst());
        return true;
    }

    private boolean skillList(Player p){
        YamlConfiguration data = new YamlConfiguration();
        List<String> skills = new ArrayList<>();
        EasyAPI.getSkills(p).forEach(s->skills.add("&a- "+s+" lv."+ SkillAPI.getPlayerData(p).getSkill(s).getLevel()));
        data.set("1",skills);
        p.openBook(BookLoader.parse(p,data));
        p.sendMessage("opened");//todo
        return true;
    }
    public static final BookLoader bookLoader = new BookLoader(SkillAPI.inst());
    private boolean open(Player p,String name){
        return bookLoader.open(p,name);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
