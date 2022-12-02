package com.sucy.tunnel.cosmetic;

import com.sucy.skill.api.player.PlayerData;
import com.sucy.tunnel.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.itemutils.ItemBuilder;
import scala.Int;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public enum CosmeticType {
    RELIC("Thánh vật", 20, 1000, 21, 1001),
    RING("Nhẫn", 23, 1002, 24, 1003),
    GLOVE("Găng", 11, 1004),
    NECKLACE("Dây chuyền", 22, 1005),
    ELITIST("Tinh hoa", 15, 1006),
    SECRET("Bí bảo", 13, 1007),
    UNIDENTIFIED("Không xác định");


    public final String display;
    public final HashMap<Integer, Integer> slots = new HashMap<>();
    CosmeticType(String name, int... slots){
        this.display = name;
        for (int i=0;i+1<slots.length;i=i+2){this.slots.put(slots[i],slots[i+1]);}
    }
    public void apply(ItemStack item){
        if (item==null || item.getType()==Material.AIR) return;
        NBTItem nbt = new NBTItem(item);
        nbt.setString("tunnel_cosmetic_type",name());
        nbt.applyNBT(item);
    }
    public List<Integer> getSlots(){
        return new ArrayList<>(slots.keySet());
    }
    public List<Integer> getIds(){
        return new ArrayList<>(slots.values());
    }
    private ItemStack getEmptyIcon(){
        switch (this){
            case RING:       return emptyRing;
            case RELIC:      return emptyRelic;
            case GLOVE:      return emptyGlove;
            case SECRET:     return emptySecret;
            case ELITIST:    return emptyElitist;
            case NECKLACE:   return emptyNecklace;
            default:     return emptyUnidentified;
        }
    }



    private static final ItemStack
            emptyRing         = new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).setName("§9"+CosmeticType.RING.display),
            emptyRelic        = new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE)  .setName("§9"+CosmeticType.RELIC.display),
            emptyGlove        = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE) .setName("§9"+CosmeticType.GLOVE.display),
            emptySecret       = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE) .setName("§9"+CosmeticType.SECRET.display),
            emptyElitist      = new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE)  .setName("§9"+CosmeticType.ELITIST.display),
            emptyNecklace     = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName("§9"+CosmeticType.NECKLACE.display),
            emptyUnidentified = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)   .setName("§9"+CosmeticType.UNIDENTIFIED.display);
    private static final HashMap<Integer,CosmeticType> idMap = new HashMap<>();
    private static final HashMap<Integer,CosmeticType> slotMap = new HashMap<>();
    private static final HashMap<Integer,Integer> slotToId = new HashMap<>();
    static {
        Arrays.stream(CosmeticType.values()).forEach((t) -> {
            if (t.slots.size()>0) {
                t.slots.forEach((k,v)->{
                    slotMap.put(k,t);
                    idMap.put(v,t);
                    slotToId.put(k,v);
                });
            }
        });
    }

    static CosmeticType getType(ItemStack item){
        if (item==null || item.getType()==Material.AIR) return UNIDENTIFIED;
        NBTItem nbt = new NBTItem(item);
        String type = nbt.getString("tunnel_cosmetic_type");
        if (type == null) return UNIDENTIFIED;
        CosmeticType ct = null;
        try {
            ct = CosmeticType.valueOf(type);
        } catch ( IllegalArgumentException ignored ) {
        }
        return ct != null ? ct : UNIDENTIFIED;
    }
    static HashMap<Integer,CosmeticType> idMap(){return idMap;}
    static HashMap<Integer,CosmeticType> slotMap(){return slotMap;}
    static int slotToId(int slot){
        return slotToId.get(slot);
    }
    public static ItemStack getEquip(PlayerData data, int slot){
        ItemStack item = data.getCosmetic().get(1000+slotToId.get(slot));
        return item!=null?item:slotMap().get(slot).getEmptyIcon();
    }
}
