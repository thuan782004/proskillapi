package com.sucy.tunnel;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EasyAPI {

    public static List<String> getSkills(Player p){
        List<String> skills = new ArrayList<>();
        PlayerData data = SkillAPI.getPlayerData(p);
        data
                .getSkills()
                .parallelStream()
                .filter(s->s.getLevel()>0)
                .forEach(s->skills.add(s.getData().getName()));
        return skills;
    }

    public static void forceAddSkill(Player p,String name){
        PlayerData data = SkillAPI.getPlayerData(p);
        PlayerClass clazz = Objects.requireNonNull(data.getMainClass());
        Skill skill = SkillAPI.getSkill(name);
        if (!clazz.getData().getSkills().contains(skill)) clazz.getData().addSkill(name);
        data.addSkill(skill,clazz);
    }

    //just for remember
    public static void cast(Player p,String skill){
        ((SkillShot) SkillAPI.getSkill(skill)).cast(p,1,true);
    }

}
