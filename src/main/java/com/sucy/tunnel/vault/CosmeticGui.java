package com.sucy.tunnel.vault;

import com.sucy.skill.SkillAPI;
import com.sucy.tunnel.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class CosmeticGui implements Listener {

    private static final ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    static {
        ItemMeta meta = filler.getItemMeta();
        assert meta != null;
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);
        //todo custom model data here
    }

    private final InventoryView view;
    private final Player player;

    public CosmeticGui(Player player){
        this.player = player;
        Inventory inv = Bukkit.createInventory(null,36,"§cPhụ kiện");
        for (int i = 0; i < 36; i++) inv.setItem(i,filler);
        Arrays.stream(CMT.values()).forEach(t->Arrays.stream(t.getSlots()).forEach(s->inv.setItem(s,t.icon)));
        SkillAPI.getPlayerData(player).getCosmetic().forEach(inv::setItem);
        this.view = player.openInventory(inv);
        Bukkit.getPluginManager().registerEvents(this,SkillAPI.inst());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if (event.getView()!=view) return;
        ItemStack from = event.getCurrentItem();
        ItemStack to   = event.getCursor();
        if (from==null) return;
        boolean cantPickup = cantPickup(from);
        CMT cursorType = getCosmeticType(to);
        System.out.println(event.getHotbarButton());
        if (cantPickup && cursorType==null){
            //pickup or invalid replace
            event.setCancelled(true);
            System.out.println("invalid");
        }
        else if (cantPickup){
            //place
            System.out.println("place");
        }
        else if(cursorType != null){
            //replace
            System.out.println("replace");
        }
        else if (to == null){
            //pickup
            System.out.println("pickup");
        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        if (event.getView()!=view) return;
        HandlerList.unregisterAll(this);
    }


    public static boolean cantPickup(ItemStack item){
        if (item==null) return false;
        return item.isSimilar(filler) || Arrays.stream(CMT.values()).parallel().anyMatch(i-> item.isSimilar(i.icon));
    }

    enum CMT{
        RING        (Material.BLUE_STAINED_GLASS_PANE,"&fRING",     Arrays.asList(" ","&7Put item here"),0,20,21,22,23,24,29,30,31,32,33),
        BRACELET    (Material.BLUE_STAINED_GLASS_PANE,"&fBRACELET", Arrays.asList(" ","&7Put item here"),0,2,6),
        EARRING     (Material.BLUE_STAINED_GLASS_PANE,"&fEARRING",  Arrays.asList(" ","&7Put item here"),0,3,5),
        GLOVE       (Material.BLUE_STAINED_GLASS_PANE,"&fGLOVE",    Arrays.asList(" ","&7Put item here"),0,11,15),
        NECKLACE    (Material.BLUE_STAINED_GLASS_PANE,"&fNECKLACE", Arrays.asList(" ","&7Put item here"),0,13),
        BELT        (Material.BLUE_STAINED_GLASS_PANE,"&fBELT",     Arrays.asList(" ","&7Put item here"),0,12),
        RELIC       (Material.BLUE_STAINED_GLASS_PANE,"&fRELIC",    Arrays.asList(" ","&7Put item here"),0,14);

        private final int[] slots;
        public final ItemStack icon;
        //todo add empty slot background
        CMT(Material bg, String name, List<String> lore, int data, int... slots){
            this.slots = slots;
            icon = new ItemStack(bg);
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',name));
            for (int i = 0; i < lore.size(); i++)
            lore.set(i,ChatColor.translateAlternateColorCodes('&',lore.get(i)));
            meta.setCustomModelData(data);
            icon.setItemMeta(meta);
        }

        public int[] getSlots() {
            return slots;
        }
    }

    public static void setCosmeticType(ItemStack item,CMT type){
        if (item==null) return;
        NBTItem nbt = new NBTItem(item);
        nbt.setString("cosmetic_type",type.name());
        nbt.applyNBT(item);
    }
    public static CMT  getCosmeticType(ItemStack item){
        if (item==null) return null;
        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey("cosmetic_type")) return null;
        String type = nbt.getString("cosmetic_type");
        try {return CMT.valueOf(type);}
        catch ( IllegalArgumentException e ) {return null;}
    }
}
