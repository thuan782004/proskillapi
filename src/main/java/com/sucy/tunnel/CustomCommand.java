package com.sucy.tunnel;

import com.sucy.skill.SkillAPI;
import com.sucy.tunnel.book.BookLoader;
import com.sucy.tunnel.vault.VaultGui;
import com.sucy.tunnel.vault.VaultManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandCollection;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;

public class CustomCommand {
    public void active(Plugin plugin){
        ArgType<YamlConfiguration> book = new ArgType<>("book",BookLoader.ins.data::get)
                .tabStream(c -> BookLoader.ins.data.keySet().stream());
        CommandCollection collection = new CommandParser(plugin.getResource("command.rdcml"))
                .setArgTypes(book)
                .parse();
        collection.register(plugin,"Tunnel",this);
    }

    @CommandHook("tunnel_reload")
    public void reload(CommandSender sender){
        BookLoader.ins.reload(SkillAPI.inst());
        sender.sendMessage("§2Reload hoàn tất");
    }
    @CommandHook("vault_open_self")
    public void open(Player sender){
        new VaultGui(sender,0).open(sender);
    }
    @CommandHook("vault_open_other")
    public void open_other(Player sender,Player target){
        new VaultGui(sender,0).open(target);
    }
    @CommandHook("vault_expand")
    public void expand(CommandSender sender,Player target, int amount){
        VaultManager manager = new VaultManager(target);
        manager.setSize(manager.getSize()+amount);
        sender.sendMessage("§2Hoàn tất. Kích thước kho đồ của "+target.getName()+" hiện tại là "+manager.getSize()+amount);
    }
    @CommandHook("vault_set")
    public void open_other(CommandSender sender,Player target, int amount){
        new VaultManager(target).setSize(amount);
        sender.sendMessage("§2Hoàn tất. Kích thước kho đồ của "+target.getName()+" hiện tại là "+amount);
    }
    @CommandHook("open_book")
    public void open_book(Player sender,Player target, YamlConfiguration book){
        sender.openBook(BookLoader.parse(target,book));
    }
}
