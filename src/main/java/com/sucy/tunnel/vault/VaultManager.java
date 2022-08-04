package com.sucy.tunnel.vault;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class VaultManager {
    private final YamlConfiguration data;
    public VaultManager(OfflinePlayer player){
        PlayerData data = SkillAPI.getPlayerData(player);
        this.data = data.getVaultData();
    }

    public ItemStack getItem(int id){
        return data.getItemStack(String.valueOf(id),new ItemStack(Material.AIR));
    }
    public void update(int id, ItemStack item){
        data.set(String.valueOf(id),item);
    }
    public int getSize(){
        return data.getInt("size",96);
    }
    public void setSize(int newSize){
        data.set("size", newSize);
    }
    public void sort(){
        List<ItemStack> list = new ArrayList<>();
        int size = getSize();
        for (int i = 0; i < size; i++) {
            ItemStack item = data.getItemStack(String.valueOf(i));
            if (item==null) continue;
            boolean add = true;
            for (ItemStack ie : list) {
                if (ie==null) continue;
                if (!ie.isSimilar(item)) continue;
                int ms = ie.getType().getMaxStackSize();
                if (ie.getAmount()>=ms) continue;
                int na = ie.getAmount()+item.getAmount();
                ie.setAmount(Math.min(na,ms));
                if (na<=ms) { add = false; break; }
                else item.setAmount(na-ms);
            }
            if (add) list.add(item);
        }
        list.sort((i1, i2) -> {
            int i = ItemCategory.getValue(i1)-ItemCategory.getValue(i2);
            if (i<0) return 1;
            else if (i>0) return -1;
            return name(i1).compareToIgnoreCase(name(i2));
        });
        for (int i = 0; i < size; i++) data.set(String.valueOf(i),list.size()>i?list.get(i):null);

    }
    public String name(ItemStack i){
        if (!i.hasItemMeta()) return i.getType().name();
        ItemMeta meta = i.getItemMeta();
        assert meta != null;
        if (!meta.hasDisplayName()) return i.getType().name();
        return meta.getDisplayName();
    }
}
