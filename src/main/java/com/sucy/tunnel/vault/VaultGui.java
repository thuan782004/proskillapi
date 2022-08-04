package com.sucy.tunnel.vault;

import com.sucy.skill.SkillAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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

public class VaultGui implements Listener {
    private final OfflinePlayer player;
    private int offset;
    private final VaultManager manager;
    private static final int
    UP = 8,
    DOWN = 17,
    SORT = 26,
    TYPE = 35,
    SELL = 44,
    SETTING = 53;

    private static final ItemStack
    ItemLock    = new ItemStack(Material.RED_STAINED_GLASS_PANE),
    ItemUp      = new ItemStack(Material.LIME_STAINED_GLASS_PANE),
    ItemDown    = new ItemStack(Material.LIME_STAINED_GLASS_PANE),
    ItemSort    = new ItemStack(Material.BLUE_STAINED_GLASS_PANE),
    ItemType    = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE),
    ItemSell    = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE),
    ItemSetting = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);
    static {
        setName(ItemLock,"&cChưa mở khóa");
        setName(ItemUp,"&aItemUp");
        setName(ItemDown,"&aItemDown");
        setName(ItemSort,"&9ItemSort");
        setName(ItemType,"&8ItemType");
        setName(ItemSell,"&6ItemSell");
        setName(ItemSetting,"&8temSetting");
    }
    public static void setName(ItemStack item,String name){
        if (name == null) return;
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',name));
        item.setItemMeta(meta);
    }

    public int getIDOfSlot(int slot){
        int s = slot+1;
        if (s%9==0) return -1;
        return (int) (Math.floor(s/9D)*8+s%9+offset*8)-1;
    }
    public int getSlotOfID(int id){
        return (int) (9*Math.floor((id-offset*8)/8D)+id%8);
    }

    public VaultGui(OfflinePlayer player, int offset) {
        this.player = player;
        this.offset = offset;
        this.manager = new VaultManager(player);
    }

    InventoryView view;
    public void open(Player p){
        Inventory inv = Bukkit.createInventory(null,54,"Vault of "+player.getName());
        inv.setItem(UP      ,ItemUp);
        inv.setItem(DOWN    ,ItemDown);
        inv.setItem(SORT    ,ItemSort);
        inv.setItem(TYPE    ,ItemType);
        inv.setItem(SELL    ,ItemSell);
        inv.setItem(SETTING ,ItemSetting);
        load(inv);
        view = p.openInventory(inv);
        Bukkit.getServer().getPluginManager().registerEvents(this,SkillAPI.inst());
    }
    private void update(){
        for (int i = 0; i < 54; i++) {
            int j = getIDOfSlot(i); if (j < 0) continue;
            manager.update(j, view.getItem(i));
        }
    }
    private void load(Inventory inv){
        for (int i = 0; i < 48; i++) {
            inv.setItem(getSlotOfID(i+8*offset),i+8*offset<manager.getSize()?manager.getItem(i+8*offset):ItemLock);
        }
    }
    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if (e.getView()!=view) return;
        Bukkit.getScheduler().runTask(SkillAPI.inst(), this::update);
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if (e.getView()!=view) return;
        if (e.getRawSlot()<54 && e.getRawSlot()>-1){
            ItemStack itm = e.getInventory().getItem(e.getRawSlot());
            if (itm!=null && itm.isSimilar(ItemLock)){
                e.setCancelled(true);
                return;
            }
        }
        switch (e.getRawSlot()){
            case UP:
                e.setCancelled(true);
                if (offset!=0) {
                    update();
                    offset = offset - 1;
                    load(e.getInventory());
                }
                break;
            case DOWN:
                e.setCancelled(true);
                if (offset*8+48<manager.getSize()) {
                    update();
                    offset = offset + 1;
                    load(e.getInventory());
                }
                break;
            case SORT:
                e.setCancelled(true);
                update();
                manager.sort();
                load(e.getInventory());
                break;
            case TYPE:
                e.setCancelled(true);
                break;
            case SELL:
                e.setCancelled(true);
                break;
            case SETTING:
                e.setCancelled(true);
                break;
        }
    }


}
