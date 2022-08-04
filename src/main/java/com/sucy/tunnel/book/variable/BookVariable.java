package com.sucy.tunnel.book.variable;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.OfflinePlayer;

public abstract class BookVariable {
    private final OfflinePlayer player;
    public BookVariable(OfflinePlayer player){
        this.player = player;
    }
    public OfflinePlayer getPlayer(){
        return player;
    }
    public abstract String next();
    public abstract boolean isContinue();
}
