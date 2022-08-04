package com.sucy.tunnel.book.variable;

import com.sucy.skill.SkillAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

public class ActiveSkillVar extends BookVariable {

    private int index = 0;
    private boolean continued = true;
    private final List<String> skills = new ArrayList<>();
    public static String patten = "    &2{name} lv.{level}";

    public ActiveSkillVar(OfflinePlayer player){
        super(player);
        SkillAPI.getPlayerData(getPlayer()).getSkills().forEach(sk -> {
            if (sk.getData().canCast()) skills.add(sk.getData().getName()+":"+sk.getLevel());
        });
    }

    @Override
    public String next() {
        if (!continued) return "";
        String sk = skills.get(index);
        sk = patten.replace("{name}",sk.split(":")[0]).replace("{level}",sk.split(":")[1]);
        index++;
        if (index==skills.size()) continued = false;
        return sk;
    }

    @Override
    public boolean isContinue() {
        return continued;
    }

}
