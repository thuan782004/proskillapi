package com.sucy.tunnel;

import com.sucy.skill.SkillAPI;

import java.util.Objects;

public class CustomCore {
    private final SkillAPI api;
    private static CustomCore core = null;
    public SkillAPI getApi() {return api;}
    public static CustomCore getInstance(){return core;}
    public static void active(SkillAPI api){
        if (core!=null) return;
        core = new CustomCore(api);
    }
    private CustomCore(SkillAPI api){
        this.api = api;
        Objects.requireNonNull(api.getCommand("tunnel")).setExecutor(new CustomCommand());
        api.getServer().getConsoleSender().sendMessage("§2loaded CustomCore");
    }


}
