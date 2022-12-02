package com.sucy.tunnel.cosmetic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.itemutils.ItemBuilder;

import java.util.Objects;

public class CosmeticGui implements Listener {

    private final Player owner;
    private final PlayerData playerData;
    private final Inventory gui;
    private final InventoryView view;
    private static final ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();
    public CosmeticGui(Player owner){
        this.owner = owner;
        this.playerData = SkillAPI.getPlayerData(this.owner);
        gui = Bukkit.createInventory(null,36,"Cosmetic");
        for (int i = 0; i < 36; i++) gui.setItem(i,filler); update();
        Bukkit.getPluginManager().registerEvents(this,SkillAPI.inst());
        view = owner.openInventory(gui);
    }

    //UTILITY AREA
    public void update(){
        CosmeticType.slotMap().keySet().forEach(slot -> gui.setItem(slot,CosmeticType.getEquip(playerData,slot)));
    }
    private CosmeticType getCursorType(){
        return CosmeticType.getType(view.getCursor());
    }
    private CosmeticType getItemType(ItemStack i){
        return CosmeticType.getType(i);
    }
    private void push(int slot,ItemStack item){
        playerData.setCosmetic(CosmeticType.slotToId(slot),item);
    }
    private boolean isInvalidItem(int slot, ItemStack item){
        return getItemType(item) != CosmeticType.slotMap().get(slot);
    }
    private ItemStack getHotBar(int slot){
        return view.getItem(gui.getSize()+slot+26);
    }
    private void setHotBar(int slot,ItemStack item){
        gui.setItem(gui.getSize()+slot+26,item);
    }

    //EVENT AREA
    @EventHandler public void onClose(InventoryCloseEvent e) {
        if (!Objects.equals(e.getInventory(), this.gui)) return;
        HandlerList.unregisterAll(this);
    }

    //todo Pickup PutDown Replace MoveUp MoveDown ReplaceBar
    @EventHandler public void onClick(InventoryClickEvent e){
        if (!Objects.equals(e.getInventory(),this.gui)) return;
        int slot = e.getRawSlot();
        ItemStack current = e.getCurrentItem();
        if (slot<36&&slot>=0&&!CosmeticType.slotMap().containsKey(slot))
            {e.setCancelled(true); return;}
        if (slot<36&&current!=null) {
            switch (e.getClick()) {
                case CONTROL_DROP: //drop all
                case SHIFT_LEFT: //move down
                case SHIFT_RIGHT: //move down
                case DROP: //drop one
                    if (isInvalidItem(slot, current)) {e.setCancelled(true);break;}
                    push(slot, null); break;
                case NUMBER_KEY: // swap
                case RIGHT:
                case LEFT: // swap or put one or pick up
                    e.setCancelled(true);
                    ItemStack swapTarget = e.getClick()==ClickType.NUMBER_KEY?getHotBar(e.getHotbarButton()):e.getCursor();
                    if (swapTarget==null){
                        //pickup
                        if (isInvalidItem(slot, current)) {break;}
                        push(slot, null); break;
                    }
                    if (swapTarget.getAmount()>1 || isInvalidItem(slot, swapTarget)) {
                        //invalid
                        break;
                    }
                    if (isInvalidItem(slot, current)){
                        //put one
                        swapTarget.setAmount(swapTarget.getAmount()-1);
                        ItemStack clone = swapTarget.clone();
                        clone.setAmount(1);
                        push(slot,clone);
                    }
                    //swap
                    ItemStack temp = swapTarget.clone();
                    if (e.getClick() == ClickType.NUMBER_KEY) {
                        setHotBar(e.getHotbarButton(), current);
                    } else {
                        e.getView().setCursor(current);
                    }
                    e.getView().setItem(slot,temp);
                    push(slot,temp);
            }
            Bukkit.getScheduler().runTask(SkillAPI.inst(),()-> gui.setItem(slot,CosmeticType.getEquip(playerData,slot)));
        }
    }
}
