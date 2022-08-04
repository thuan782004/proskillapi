package com.sucy.tunnel.vault;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class ItemCategory {

    public static int getValue(ItemStack item){
        return getRarity(item).num+getType(item).num;
    }

    public static RARITY getRarity(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if (!item.hasItemMeta() || meta==null || !meta.hasLore()) return RARITY.UNDENTIFIED;
        String lore = Objects.requireNonNull(meta.getLore()).get(0);
        return RARITY.getNyName(ChatColor.stripColor(lore).split(" ")[0]);
    }

    public static TYPE getType(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if (!item.hasItemMeta() || meta==null || !meta.hasLore()) return TYPE.OTHER;
        String lore = Objects.requireNonNull(meta.getLore()).get(0);
        String[] h = ChatColor.stripColor(lore).split(" ");
        return (h.length>1)?TYPE.getNyName(h[1]):TYPE.OTHER;
    }

    public static void setRarity(ItemStack item,RARITY rarity){
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = meta.getLore();
        String str = ChatColor.translateAlternateColorCodes('&', rarity.color+rarity.name+" "+getType(item).name);
        assert lore != null;
        if (lore.size()>0&&isDefined(lore.get(1))) lore.set(0, str);
        else lore.add(0,str);
    }
    public static void setType(ItemStack item, TYPE type){
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = meta.getLore();
        RARITY rarity = getRarity(item);
        String str = ChatColor.translateAlternateColorCodes('&', rarity.color+rarity.name+" "+type.name);
        assert lore != null;
        if (lore.size()>0&&isDefined(lore.get(1))) lore.set(0, str);
        else lore.add(0,str);
    }

    private static boolean isDefined(String s){
        String[] split = s.toLowerCase().split(" ");
        if (split.length==0) return false;
        switch (split[0]){
             case "common":
             case "rare":
             case "treasure":
             case "mythical":
             case "ultimate":
             case "undentified":
             case "armor":
             case "weapon":
             case "artifact":
             case "skill":
             case "drop":
             case "other":
                 return true;
             default: return false;
         }
    }



    public static class RARITY{
        public static final RARITY UNDENTIFIED  = new RARITY(0,"?","&8","Undentified");
        public static final RARITY COMMON       = new RARITY(1,"C","&a","Common");
        public static final RARITY RARE         = new RARITY(2,"R","&9","Rare");
        public static final RARITY TREASURE     = new RARITY(3,"T","&d","Treasure");
        public static final RARITY MYTHICAL     = new RARITY(4,"M","&e","Mythical");
        public static final RARITY ULTIMATE     = new RARITY(5,"U","&6","Ultimate");
        public final int num;
        public final String icon;
        public final String color;
        public final String name;

        private RARITY(int num,String icon, String color,String name){
            this.num = num;
            this.icon = icon;
            this.color = color;
            this.name = name;
        }
        public static RARITY getByInt(int i){
            switch (i){
                case 1: return COMMON;
                case 2: return RARE;
                case 3: return TREASURE;
                case 4: return MYTHICAL;
                case 5: return ULTIMATE;
                default: return UNDENTIFIED;
            }
        }
        public static RARITY getNyName(String name){
            switch (name.toLowerCase()){
                case "common": return COMMON;
                case "rare": return RARE;
                case "treasure": return TREASURE;
                case "mythical": return MYTHICAL;
                case "ultimate": return ULTIMATE;
                default: return UNDENTIFIED;
            }
        }
    }
    public static class TYPE{
        public static final TYPE OTHER      = new TYPE(0 ,"?","other");
        public static final TYPE ARMOR      = new TYPE(10,"?","armor");
        public static final TYPE WEAPON     = new TYPE(20,"?","weapon");
        public static final TYPE ARTIFACT   = new TYPE(30,"?","artifact");
        public static final TYPE SKILL      = new TYPE(40,"?","skill");
        public static final TYPE DROP       = new TYPE(50,"?","drop");
        public final int num;
        public final String icon;
        public final String name;

        private TYPE(int num,String icon,String name){
            this.num = num;
            this.icon = icon;
            this.name = name;
        }
        public static TYPE getByInt(int i){
            switch (i){
                case 1:             return ARMOR;
                case 2:             return WEAPON;
                case 3:             return ARTIFACT;
                case 4:             return SKILL;
                case 5:             return DROP;
                default:            return OTHER;
            }
        }
        public static TYPE getNyName(String name){
            switch (name.toLowerCase()){
                case "armor":       return ARMOR;
                case "weapon":        return WEAPON;
                case "artifact":    return ARTIFACT;
                case "skill":       return SKILL;
                case "drop":        return DROP;
                default:            return OTHER;
            }
        }
    }
}
